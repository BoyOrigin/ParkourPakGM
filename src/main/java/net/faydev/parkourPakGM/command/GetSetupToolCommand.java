package net.faydev.parkourPakGM.command;

import net.faydev.parkourPakGM.ParkourPakGM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GetSetupToolCommand implements CommandExecutor {

    private final ParkourPakGM plugin;

    public GetSetupToolCommand(ParkourPakGM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be executed by a player"));
            return false;
        }
        Player player = (Player) sender;
        ItemStack tool = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = tool.getItemMeta();
        itemMeta.displayName(Component.text("Area Wand Tool").color(NamedTextColor.YELLOW));
        List<Component> toolComponents = new ArrayList<>();
        toolComponents.add(Component.text("(Left click or Right click to set as an offset of area box)").color(NamedTextColor.DARK_GRAY));
        itemMeta.lore(toolComponents);
        tool.setItemMeta(itemMeta);
        player.getInventory().addItem(tool);
        return true;
    }
}
