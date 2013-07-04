package net.daboross.bukkitdev.rankdata;

import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.PlayerDataBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author daboross
 */
public class RankDataBukkit extends JavaPlugin {

    private static RankDataBukkit currentInstance;
    private PlayerDataBukkit playerDataBukkit;
    private RankDataCommandExecutor commandExecutor;
    private SurvivorChecker survivorChecker;

    @Override
    public void onEnable() {
        Plugin playerDataPlugin = Bukkit.getPluginManager().getPlugin("PlayerData");
        if (playerDataPlugin == null) {
            getLogger().log(Level.SEVERE, "PlayerData Not Found!");
        } else if (playerDataPlugin instanceof PlayerDataBukkit) {
            playerDataBukkit = (PlayerDataBukkit) playerDataPlugin;
        } else {
            getLogger().log(Level.SEVERE, "PlayerData Not instanceof PlayerData!");
        }
        if (playerDataBukkit == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        commandExecutor = new RankDataCommandExecutor(this);
        PluginCommand rankdata = getCommand("rankdata:rankdata");
        if (rankdata != null) {
            commandExecutor.registerCommand(rankdata);
        }
        if (!playerDataBukkit.isPermissionLoaded()) {
            getLogger().log(Level.SEVERE, "Permission Handler not found! Can't Enable!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        survivorChecker.reload();
        currentInstance = this;
    }

    @Override
    public void onDisable() {
        currentInstance = null;
    }

    protected static RankDataBukkit getCurrentInstance() {
        return currentInstance;
    }

    protected PlayerDataBukkit getPDataMain() {
        return playerDataBukkit;
    }

    protected SurvivorChecker getSurvivorChecker() {
        return survivorChecker;
    }
}
