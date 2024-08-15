package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetCheckpointLocationCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public SetCheckpointLocationCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (StringUtils.isNumeric(args[0])) {
                int number = Integer.valueOf(args[0]);
                if (number > 0) {
                    this.plugin.setData("checkpoint.location-" + number, player.getLocation());
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCheckpoint Location has been set"));
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /setcheckpointlocation <1-...>"));
        return false;
    }
}
