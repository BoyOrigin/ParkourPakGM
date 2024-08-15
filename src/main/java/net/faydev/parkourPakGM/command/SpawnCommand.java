package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SpawnCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public SpawnCommand(ParkourPakGM plugin) {
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
        if (data == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSpawn Location is not set"));
            return false;
        }
        ConcurrentLinkedQueue<Location> list = (ConcurrentLinkedQueue<Location>) data;

        Location spawnLoc = list.stream().skip(new Random().nextInt(list.size())).findFirst().get();
        player.teleportAsync(spawnLoc);
        return true;
    }
}
