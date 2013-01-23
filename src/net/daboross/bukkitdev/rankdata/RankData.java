package net.daboross.bukkitdev.rankdata;

import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author daboross
 */
public class RankData extends JavaPlugin {

    private static RankData currentInstance;
    private PlayerData pDataM;
    private RankDataCommandExecutor commandExecutor;
    private Reloader reloader;

    @Override
    public void onEnable() {
        Plugin playerDataPlugin = Bukkit.getPluginManager().getPlugin("PlayerData");
        if (playerDataPlugin == null) {
            getLogger().log(Level.SEVERE, "PlayerData Not Found!");
        } else {
            if (playerDataPlugin instanceof PlayerData) {
                pDataM = (PlayerData) playerDataPlugin;
            } else {
                getLogger().log(Level.SEVERE, "PlayerData Not Instance Of Player Data!");
            }
        }
        if (pDataM == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        commandExecutor = new RankDataCommandExecutor(this);
        PluginCommand dCommand = getCommand("dcommand");
        if (dCommand != null) {
            dCommand.setExecutor(commandExecutor);
        } else {
            getLogger().log(Level.SEVERE, "dCommand Not Found!");
        }
        reloader = new Reloader(this);
        currentInstance = this;
    }

    @Override
    public void onDisable() {
        currentInstance = null;
    }

    protected static RankData getCurrentInstance() {
        return currentInstance;
    }

    protected PlayerData getPDataMain() {
        return pDataM;
    }

    protected Reloader getReloader() {
        return reloader;
    }
}
