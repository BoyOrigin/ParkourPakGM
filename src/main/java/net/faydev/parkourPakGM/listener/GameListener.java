package net.faydev.parkourPakGM.listener;

import net.faydev.parkourPakGM.EventPlayer;
import net.faydev.parkourPakGM.ParkourPakGM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BoundingBox;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameListener implements Listener {

    private final ParkourPakGM plugin;

    public GameListener(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("parkourPakGM.panitia")) {
            EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
            if (eventPlayer == null) {
                if (this.plugin.getGameState()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The event has been started. You're late!");
                    return;
                }
                eventPlayer = new EventPlayer(player, ParkourPakGM.getPlugin());
            } else {
                eventPlayer.setPlayer(player);
            }
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
        if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
            event.setSpawnLocation(eventPlayer.getCheckpointLoc());
            if (this.plugin.getGameState()) {
                player.getInventory().clear();
                for (int i = 0; i < 41; i++) {
                    Object data = this.plugin.getData("kit.slot." + i);
                    if (data == null) continue;
                    ItemStack item = (ItemStack) data;
                    player.getInventory().setItem(i, item);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
        if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
            if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
                player.setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerUseSetupTool(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parkourPakGM.panitia")) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Area Wand Tool")) {
                    this.plugin.getWandTool().putIfAbsent(player, new ParkourPakGM.TwoWayLocation(null, null));
                    Location location = event.getClickedBlock().getLocation();
                    this.plugin.getWandTool().get(player).setOne(location);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePosition one has been set to (" +
                        location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
                    event.setCancelled(true);
                }
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Area Wand Tool")) {
                    this.plugin.getWandTool().putIfAbsent(player, new ParkourPakGM.TwoWayLocation(null, null));
                    Location location = event.getClickedBlock().getLocation();
                    if (location.equals(this.plugin.getWandTool().get(player).getTwo())) {
                        return;
                    }
                    this.plugin.getWandTool().get(player).setTwo(location);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePosition two has been set to (" +
                        location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
        if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
            event.setKeepInventory(true);
            event.setCancelled(true);
            Location location = eventPlayer.getCheckpointLoc().clone();
            location.setX(player.getX());
            CompletableFuture<Boolean> future = player.teleportAsync(location);
            future.whenComplete((state, exception) -> {
                if (exception == null && state.booleanValue() == true) {
                    player.setFireTicks(0);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.getInventory().clear();
                    for (int i = 0; i < 41; i++) {
                        Object data = this.plugin.getData("kit.slot." + i);
                        if (data == null) continue;
                        ItemStack item = (ItemStack) data;
                        player.getInventory().setItem(i, item);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
            if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
                if (!this.plugin.getGameState()) {
                    event.setCancelled(true);
                }
                if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
        if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
            if (player.getY() <= 16 && !eventPlayer.getTeleportingState().getAndSet(true)) {
                Location location = eventPlayer.getCheckpointLoc().clone();
                location.setX(player.getX());
                CompletableFuture<Boolean> future = player.teleportAsync(location);
                future.whenComplete((state, exception) -> {
                    try {
                        if (exception == null && state.booleanValue() == true) {
                            player.setFireTicks(0);
                            player.setHealth(20);
                            player.setFoodLevel(20);
                            player.getInventory().clear();
                            for (int i = 0; i < 41; i++) {
                                Object data = this.plugin.getData("kit.slot." + i);
                                if (data == null) continue;
                                ItemStack item = (ItemStack) data;
                                player.getInventory().setItem(i, item);
                            }
                        }
                    } finally {
                        eventPlayer.getTeleportingState().set(false);
                    }
                });
            } else if (!eventPlayer.getTeleportingState().get()) {
                Object disableBowAreaData = this.plugin.getData("bow.disableArea");
                if (disableBowAreaData != null) {
                    BoundingBox disableBowArea = (BoundingBox) disableBowAreaData;
                    if (disableBowArea.contains(player.getBoundingBox())) {
                        if (!eventPlayer.getDisableBowState().getAndSet(true)) {
                            player.getInventory().remove(Material.BOW);
                        }
                    } else {
                        if (eventPlayer.getDisableBowState().getAndSet(false)) {
                            player.getInventory().clear();
                            for (int i = 0; i < 41; i++) {
                                Object data = this.plugin.getData("kit.slot." + i);
                                if (data == null) continue;
                                ItemStack item = (ItemStack) data;
                                player.getInventory().setItem(i, item);
                            }
                        }
                    }
                }
                int nextCheckpoint = eventPlayer.getCheckpointStage() + 1;
                Object data = this.plugin.getData("checkpoint.area-" + nextCheckpoint);
                if (data != null) {
                    BoundingBox nextCheckpointArea = (BoundingBox) data;
                    if (nextCheckpointArea.contains(player.getBoundingBox())) {
                        Object dataLoc = this.plugin.getData("checkpoint.location-" + nextCheckpoint);
                        if (dataLoc != null) {
                            Location location = (Location) dataLoc;
                            eventPlayer.setCheckpointStage(nextCheckpoint);
                            eventPlayer.setCheckpointLoc(location);
                            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                            FireworkMeta fireworkMeta = firework.getFireworkMeta();
                            fireworkMeta.addEffect(FireworkEffect.builder()
                                .flicker(true)
                                .trail(true)
                                .with(FireworkEffect.Type.BALL)
                                .withColor(Color.RED)
                                .withColor(Color.WHITE)
                                .build());
                            firework.setTicksToDetonate(20);
                            firework.setFireworkMeta(fireworkMeta);
                            player.sendActionBar(Component.text("Anda telah mencapai ke checkpoint yang baru!").color(NamedTextColor.YELLOW));
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e[PESERTA] " + player.getName() + " &atelah mencapai checkpoint ke " + nextCheckpoint));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("parkourPakGM.panitia")) {
            if (this.plugin.getChatLockState()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cChat is in locked!"));
                event.setCancelled(true);
                return;
            }
            EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
            if (eventPlayer != null) {
                event.setFormat(ChatColor.translateAlternateColorCodes('&', "&e[PESERTA] %1$s: &r%2$s"));
            } else {
                event.setFormat(ChatColor.translateAlternateColorCodes('&', "&7[PENONTON] %1$s: &r%2$s"));
            }
        } else if (player.hasPermission("parkourPakGM.panitia")) {
            event.setFormat(ChatColor.translateAlternateColorCodes('&', "&c[PANITIA] %1$s: &r%2$s"));
        } else {
            event.setFormat(ChatColor.translateAlternateColorCodes('&', "%1$s: %2$s"));
        }
    }

    @EventHandler
    public void onButtonPressed(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
        if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Object finishLocData = this.plugin.getData("finish.location");
                if (finishLocData != null) {
                    Location finishLoc = (Location) finishLocData;
                    if (finishLoc.equals(event.getClickedBlock().getLocation())) {
                        if (this.plugin.getWinnerName() == null) {
                            this.plugin.setWinnerName(player.getName());
                            Location loc1 = player.getLocation().clone();
                            loc1.setX(loc1.getX() + 2);
                            Location loc2 = player.getLocation().clone();
                            loc1.setX(loc1.getX() - 2);
                            spawnFireworkWinner(loc1);
                            spawnFireworkWinner(loc2);
                            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                                Location loc3 = loc1.clone();
                                loc3.setX(loc3.getX() + 2);
                                Location loc4 = loc2.clone();
                                loc4.setX(loc4.getX() - 2);
                                spawnFireworkWinner(loc3);
                                spawnFireworkWinner(loc4);
                                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                                    Location loc5 = loc3.clone();
                                    loc5.setX(loc5.getX() + 2);
                                    Location loc6 = loc4.clone();
                                    loc6.setX(loc6.getX() - 2);
                                    spawnFireworkWinner(loc5);
                                    spawnFireworkWinner(loc6);
                                }, 5L);
                            }, 5L);
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&l============================"));
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&l"));
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e&l    Winner: &r&e" + this.plugin.getWinnerName()));
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e&l    Reward: &r&aRp. 300.000,00"));
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&l"));
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&l============================"));
                            for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
                                otherPlayer.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lSELAMAT!"), ChatColor.translateAlternateColorCodes('&', "&ePemenang: " + this.plugin.getWinnerName()), 0, 20 * 10, 0);
                                otherPlayer.playSound(otherPlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                            }
                            eventPlayer.setCheckpointLoc(player.getLocation());
                            //this.plugin.getServer().getWorld("world").setType(0, 0, 0, Material.AIR);
                        }
                    }
                }
            }
        }
    }

    private void spawnFireworkWinner(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
            .flicker(true)
            .trail(true)
            .with(FireworkEffect.Type.BALL)
            .withColor(Color.RED)
            .withColor(Color.WHITE)
            .build());
        firework.setTicksToDetonate(60);
        firework.setFireworkMeta(fireworkMeta);
    }

    @EventHandler
    public void onPlayerUseWinnerTool(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parkourPakGM.panitia")) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Winner Tool")) {
                    Location location = event.getClickedBlock().getLocation();
                    this.plugin.setData("finish.location", location);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eWinner button has been set to (" +
                        location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUseTargetTool(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parkourPakGM.panitia")) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Bow Target Tool")) {
                    Location location = event.getClickedBlock().getLocation();
                    Object data = this.plugin.getData("gate.target");
                    ConcurrentLinkedQueue<Location> list = data != null ? (ConcurrentLinkedQueue<Location>) data : new ConcurrentLinkedQueue<>();
                    list.add(location);
                    this.plugin.setData("gate.target", list);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eTarget button has been added to (" +
                        location.getX() + ", " + location.getY() + ", " + location.getZ() + ")"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
            if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onButtonPressedByArrow(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.ARROW) {
            if (event.getEntity().getShooter() instanceof Player player) {
                EventPlayer eventPlayer = ParkourPakGM.getPlugin().getEventPlayer(player.getName());
                if (!player.hasPermission("parkourPakGM.panitia") && eventPlayer != null && event.getHitBlock() != null) {
                    Object data = this.plugin.getData("gate.target");
                    ConcurrentLinkedQueue<Location> list = data != null ? (ConcurrentLinkedQueue<Location>) data : new ConcurrentLinkedQueue<>();
                    World world = event.getHitBlock().getLocation().getWorld();
                    for (Location location : list) {
                        if (location.equals(event.getHitBlock().getLocation())) {
                            Object gateData = this.plugin.getData("gate.removalArea");
                            if (gateData != null) {
                                BoundingBox removalAreaBox = (BoundingBox) gateData;
                                for (int i = removalAreaBox.getMin().getBlockX(); i <= removalAreaBox.getMax().getBlockX(); i++) {
                                    for (int j = removalAreaBox.getMin().getBlockY(); j <= removalAreaBox.getMax().getBlockY(); j++) {
                                        for (int k = removalAreaBox.getMin().getBlockZ(); k <= removalAreaBox.getMax().getBlockZ(); k++) {
                                            world.setType(i, j, k, Material.AIR);
                                        }
                                    }
                                }
                            }
                            for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
                                otherPlayer.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lGATE OPEN"), ChatColor.translateAlternateColorCodes('&', "&eGate is now open by " + player.getName()), 0, 20 * 3, 0);
                                otherPlayer.playSound(otherPlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                            }
                            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aGate is now open by " + player.getName()));
                        }
                    }
                }
            }
        }
    }
}
