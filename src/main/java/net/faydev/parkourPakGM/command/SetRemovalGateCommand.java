package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class SetRemovalGateCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public SetRemovalGateCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        Player player = (Player) sender;
        ParkourPakGM.TwoWayLocation twoWayLocation = this.plugin.getWandTool().get(player);
        if (twoWayLocation == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease set positions with Area Wand Tool. (/getsetuptool)"));
            return false;
        }
        if (twoWayLocation.getOne() == null || twoWayLocation.getTwo() == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOne of the position is not set with Area Wand Tool. (/getsetuptool)"));
            return false;
        }
        if (twoWayLocation.getOne().getWorld() != twoWayLocation.getTwo().getWorld()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease set positions in the same world."));
            return false;
        }
        BoundingBox areaBox = BoundingBox.of(twoWayLocation.getOne(), twoWayLocation.getTwo());
        this.plugin.setData("gate.removalArea", areaBox);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aRemoval Gate Area has been set"));
        return true;
    }
}
