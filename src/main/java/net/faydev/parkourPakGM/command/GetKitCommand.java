package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetKitCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public GetKitCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        Player player = (Player) sender;
        for (int i = 0; i < 41; i++) {
            Object data = this.plugin.getData("kit.slot." + i);
            if (data == null) continue;
            ItemStack item = (ItemStack) data; 
            player.getInventory().setItem(i, item);
        }
        return true;
    }
}
