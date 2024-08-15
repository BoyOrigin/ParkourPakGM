package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatLockCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public ChatLockCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        Player player = (Player) sender;
        if (!this.plugin.getChatLockState()) {
            this.plugin.setChatLockState(true);
            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] Chat is now locked!"));
        } else {
            this.plugin.setChatLockState(false);
            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[EVENT] Chat is now unlocked!"));
        }
        return true;
    }
}
