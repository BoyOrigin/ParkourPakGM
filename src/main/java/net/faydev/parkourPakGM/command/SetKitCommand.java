package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetKitCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public SetKitCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        Player player = (Player) sender;
        this.plugin.removeData("kit");
        for (int i = 0; i < 41; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;
            this.plugin.setData("kit.slot." + i, item);
        }
        this.plugin.saveData();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEvent's Kit has been set!"));
        return true;
    }
}
