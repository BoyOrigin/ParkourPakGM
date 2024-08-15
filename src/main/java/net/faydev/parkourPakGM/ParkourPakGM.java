package net.faydev.parkourPakGM;

import net.faydev.parkourPakGM.command.ChatLockCommand;
import net.faydev.parkourPakGM.command.GetKitCommand;
import net.faydev.parkourPakGM.command.GetSetupToolCommand;
import net.faydev.parkourPakGM.command.GetTargetToolCommand;
import net.faydev.parkourPakGM.command.GetWinnerToolCommand;
import net.faydev.parkourPakGM.command.ResetSpawnCommand;
import net.faydev.parkourPakGM.command.ResetTargetCommand;
import net.faydev.parkourPakGM.command.SetCheckpointAreaCommand;
import net.faydev.parkourPakGM.command.SetCheckpointLocationCommand;
import net.faydev.parkourPakGM.command.SetDisableBowAreaCommand;
import net.faydev.parkourPakGM.command.SetKitCommand;
import net.faydev.parkourPakGM.command.AddSpawnCommand;
import net.faydev.parkourPakGM.command.SetRemovalGateCommand;
import net.faydev.parkourPakGM.command.SetRemovalLobbyCommand;
import net.faydev.parkourPakGM.command.SpawnCommand;
import net.faydev.parkourPakGM.command.StartCommand;
import net.faydev.parkourPakGM.listener.GameListener;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class ParkourPakGM extends JavaPlugin {

    public static class TwoWayLocation {
        private Location one;
        private Location two;

        public TwoWayLocation(Location one, Location two) {
            this.one = one;
            this.two = two;
        }

        public Location getOne() {
            return one;
        }

        public Location getTwo() {
            return two;
        }

        public void setOne(Location location) {
            this.one = location;
        }

        public void setTwo(Location location) {
            this.two = location;
        }
    }

    private final WeakHashMap<Player, TwoWayLocation> wandTool = new WeakHashMap<>();

    private final ConcurrentHashMap<String, EventPlayer> players = new ConcurrentHashMap<>();

    private static ParkourPakGM plugin;
    private String winnerName;
    private boolean gameState = false;
    private boolean chatLockState = false;
    private BukkitTask startingTask = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.loadData();
        this.getCommand("addspawn").setExecutor(new AddSpawnCommand(this));
        this.getCommand("resetspawn").setExecutor(new ResetSpawnCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("setkit").setExecutor(new SetKitCommand(this));
        this.getCommand("getkit").setExecutor(new GetKitCommand(this));
        this.getCommand("getsetuptool").setExecutor(new GetSetupToolCommand(this));
        this.getCommand("setremovallobby").setExecutor(new SetRemovalLobbyCommand(this));
        this.getCommand("setcheckpointarea").setExecutor(new SetCheckpointAreaCommand(this));
        this.getCommand("setcheckpointlocation").setExecutor(new SetCheckpointLocationCommand(this));
        this.getCommand("setdisablebowarea").setExecutor(new SetDisableBowAreaCommand(this));
        this.getCommand("getwinnertool").setExecutor(new GetWinnerToolCommand(this));
        this.getCommand("start").setExecutor(new StartCommand(this));
        this.getCommand("chatlock").setExecutor(new ChatLockCommand(this));
        this.getCommand("setremovalgate").setExecutor(new SetRemovalGateCommand(this));
        this.getCommand("gettargettool").setExecutor(new GetTargetToolCommand(this));
        this.getCommand("resettarget").setExecutor(new ResetTargetCommand(this));
        this.getServer().getPluginManager().registerEvents(new GameListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveData();
    }

    public boolean getGameState() {
        return gameState;
    }

    public BukkitTask getStartingTask() {
        return startingTask;
    }

    public void setGameState(boolean gameState) {
        this.gameState = gameState;
    }

    public void setStartingTask(BukkitTask startingTask) {
        this.startingTask = startingTask;
    }

    public boolean getChatLockState() {
        return chatLockState;
    }

    public void setChatLockState(boolean chatLockState) {
        this.chatLockState = chatLockState;
    }

    public static ParkourPakGM getPlugin() {
        return plugin;
    }

    public WeakHashMap<Player, TwoWayLocation> getWandTool() {
        return wandTool;
    }

    private File dataConfigFile;
    private FileConfiguration dataConfig;
    private ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();

    public FileConfiguration getDataConfig() {
        return this.dataConfig;
    }

    public void setData(String key, Object data) {
        this.data.put(key, data);
    }

    public void removeData(String key) {
        this.data.remove(key);
    }

    public Object getData(String key) {
        return this.data.get(key);
    }

    public void saveData() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(data);

            dataOutput.close();
            this.getDataConfig().set("data", Base64Coder.encodeLines(outputStream.toByteArray()));
            this.getDataConfig().save(dataConfigFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        dataConfigFile = new File(getDataFolder(), "data.yml");
        if (!dataConfigFile.exists()) {
            dataConfigFile.getParentFile().mkdirs();
            try {
                dataConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load(dataConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String code = this.getDataConfig().getString("data");
        if (code == null) {
            return;
        }
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(code));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            Object data = dataInput.readObject();

            dataInput.close();
            this.data = (ConcurrentHashMap<String, Object>) data;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public void addEventPlayer(EventPlayer eventPlayer) {
        this.players.put(eventPlayer.getPlayerName(), eventPlayer);
    }

    public EventPlayer getEventPlayer(String playerName) {
        return this.players.get(playerName);
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }
}
