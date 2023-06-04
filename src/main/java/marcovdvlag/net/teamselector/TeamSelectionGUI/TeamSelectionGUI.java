package marcovdvlag.net.teamselector.TeamSelectionGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.HashMap;

import java.util.Map;

public class TeamSelectionGUI extends JavaPlugin implements Listener {
    private static final String GUI_TITLE = "Team Selection";
    private Map<String, ChatColor> teamColors = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teamselector")) {
            if (args.length % 2 != 1) {
                sender.sendMessage(ChatColor.RED + "Invalid command format. Usage: /teamselector <amount of teams> <teamname1> <team_colour> <teamname2> <team_colour> ...");
                return true;
            }

            int teamCount;
            try {
                teamCount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid team count specified.");
                return true;
            }

            if (teamCount <= 0 || (args.length - 1) / 2 < teamCount) {
                sender.sendMessage(ChatColor.RED + "Invalid number of teams or insufficient team details provided.");
                return true;
            }

            teamColors.clear();
            for (int i = 0; i < teamCount; i++) {
                int index = 1 + i * 2;
                String teamName = args[index];
                String colorName = args[index + 1];
                ChatColor color = ChatColor.valueOf(colorName.toUpperCase());
                teamColors.put(teamName, color);
            }

            sender.sendMessage(ChatColor.GREEN + "Team Selection GUI has been updated successfully.");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        giveTeamSelectionItem(player);
    }

    private void giveTeamSelectionItem(Player player) {
        ItemStack teamSelectionItem = createTeamSelectionItemSelector();
        player.getInventory().addItem(teamSelectionItem);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.COMPASS && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.getDisplayName().equals(ChatColor.YELLOW + "Team Selection")) {
                event.setCancelled(true);
                openTeamSelectionGUI(player);
            }
        }
    }

    private void openTeamSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);

        int slot = 0;
        for (Map.Entry<String, ChatColor> entry : teamColors.entrySet()) {
            String teamName = entry.getKey();
            ChatColor color = entry.getValue();
            ItemStack woolBlock = createTeamSelectionItem(teamName, Material.valueOf(color + "_WOOL"));
            gui.setItem(slot, woolBlock);
            slot++;
        }

        ItemStack glassPane = createGlassPane();
        for (int i = slot; i < 9; i++) {
            gui.setItem(i, glassPane);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null || !teamColors.containsValue(item.getType().name())) {
            return;
        }

        String teamName = getTeamNameByColor(item.getType());
        if (teamName == null) {
            return;
        }

        ChatColor color = teamColors.get(teamName);

        // Set the player's team in your plugin logic using player.getName() and teamName

        player.sendMessage(ChatColor.GREEN + "You joined the " + color + teamName + " team!");

        player.closeInventory();
    }

    private String getTeamNameByColor(Material color) {
        for (Map.Entry<String, ChatColor> entry : teamColors.entrySet()) {
            if (color.name().equalsIgnoreCase(entry.getValue() + "_WOOL")) {
                return entry.getKey();
            }
        }
        return null;
    }

    private ItemStack createTeamSelectionItem(String teamName, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + teamName + " Team");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createGlassPane() {
        ItemStack item = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createTeamSelectionItemSelector() {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Team Selection");
        item.setItemMeta(meta);

        return item;
    }
}