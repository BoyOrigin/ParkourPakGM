package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AddSpawnCommand implements CommandExecutor {

    private ParkourPakGM plugin;

    public AddSpawnCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        Player player = (Player) sender;
        Object data = this.plugin.getData("location.spawn");
        ConcurrentLinkedQueue<Location> list = data != null ? (ConcurrentLinkedQueue<Location>) data : new ConcurrentLinkedQueue<>(); 
        list.add(player.getLocation());
        this.plugin.setData("location.spawn", list);
        this.plugin.saveData();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSpawn Location has been added!"));
        return true;
    }
}
