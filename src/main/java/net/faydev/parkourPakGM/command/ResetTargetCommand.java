package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ResetTargetCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public ResetTargetCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.plugin.removeData("gate.target");
        this.plugin.saveData();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTarget Button Locations has been reset!"));
        return true;
    }
}
