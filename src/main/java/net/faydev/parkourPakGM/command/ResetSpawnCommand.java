package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetSpawnCommand implements CommandExecutor {

    private ParkourPakGM plugin;

    public ResetSpawnCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.plugin.removeData("location.spawn");
        this.plugin.saveData();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSpawn Location has been reset!"));
        return true;
    }
}
