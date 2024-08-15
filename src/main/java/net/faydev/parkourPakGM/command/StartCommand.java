package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class StartCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public StartCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        if (this.plugin.getGameState()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe event has been started!"));
            return false;
        }
        Player player = (Player) sender;
        if (this.plugin.getStartingTask() == null) {
            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] The countdown has started!"));
            this.plugin.setStartingTask(this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new StartTask(this.plugin, player.getWorld()), 20L, 20L));
        } else {
            this.plugin.getStartingTask().cancel();
            this.plugin.setStartingTask(null);
            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] The countdown canceled!"));
            for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
                otherPlayer.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cCanceled"), "", 0, 20 * 3, 0);
                otherPlayer.playSound(otherPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        }
        return true;
    }

    public static class StartTask implements Runnable {
        private final ParkourPakGM plugin;
        private int countdown = 60;
        private final World world;

        public StartTask(ParkourPakGM plugin, World world) {
            this.plugin = plugin;
            this.world = world;
        }

        @Override
        public void run() {
            if (countdown == 60 || countdown == 50 || countdown == 40 || countdown == 30 || countdown == 20 || countdown == 10) {
                this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] The event will start in " + countdown + " seconds!"));
                for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
                    otherPlayer.sendTitle(ChatColor.translateAlternateColorCodes('&', "&e" + countdown), "", 0, 20 * 3, 0);
                    otherPlayer.playSound(otherPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }
            if (countdown <= 5 && countdown > 0) {
                this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] The event will start in " + countdown));
                for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
                    otherPlayer.sendTitle(ChatColor.translateAlternateColorCodes('&', "&e" + countdown), "", 0, 20, 0);
                    otherPlayer.playSound(otherPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }
            if (countdown <= 0) {
                for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
                    otherPlayer.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lEVENT START"), ChatColor.translateAlternateColorCodes('&', "&eGood luck"), 0, 20 * 3, 0);
                    otherPlayer.playSound(otherPlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    if (this.plugin.getEventPlayer(otherPlayer.getName()) != null) {
                        otherPlayer.getInventory().clear();
                        for (int i = 0; i < 41; i++) {
                            Object data = this.plugin.getData("kit.slot." + i);
                            if (data == null) continue;
                            ItemStack item = (ItemStack) data;
                            otherPlayer.getInventory().setItem(i, item);
                        }
                    }
                }
                this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] The event has started!"));
                this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] Go to the finish line and good luck!"));
                this.plugin.setGameState(true);
                Object data = this.plugin.getData("lobby.removalArea");
                if (data != null) {
                    BoundingBox removalAreaBox = (BoundingBox) data;
                    for (int i = removalAreaBox.getMin().getBlockX(); i <= removalAreaBox.getMax().getBlockX(); i++) {
                        for (int j = removalAreaBox.getMin().getBlockY(); j <= removalAreaBox.getMax().getBlockY(); j++) {
                            for (int k = removalAreaBox.getMin().getBlockZ(); k <= removalAreaBox.getMax().getBlockZ(); k++) {
                                this.world.setType(i, j, k, Material.AIR);
                            }
                        }
                    }
                }
                this.plugin.getStartingTask().cancel();
            }
            countdown--;
        }
    }
}
