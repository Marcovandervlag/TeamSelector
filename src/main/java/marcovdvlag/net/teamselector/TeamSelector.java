package marcovdvlag.net.teamselector;

import marcovdvlag.net.teamselector.TeamSelectionGUI.TeamSelectionGUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TeamSelector extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        Bukkit.getPluginManager().registerEvents(new TeamSelectionGUI(), this);
        // Plugin startup logic
        logger.info("Plugin made by Marco van der Vlag");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
