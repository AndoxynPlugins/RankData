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
    private SurvivorChecker survivorChecker;

    @Override
    public void onEnable() {
        Plugin playerDataPlugin = Bukkit.getPluginManager().getPlugin("PlayerData");
        if (playerDataPlugin == null) {
            getLogger().log(Level.SEVERE, "PlayerData Not Found!");
        } else if (playerDataPlugin instanceof PlayerData) {
            pDataM = (PlayerData) playerDataPlugin;
        } else {
            getLogger().log(Level.SEVERE, "PlayerData Not instanceof PlayerData!");
        }
        if (pDataM == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        commandExecutor = new RankDataCommandExecutor(this);
        PluginCommand rankdata = getCommand("rankdata:rankdata");
        if (rankdata != null) {
            commandExecutor.registerCommand(rankdata);
        }
        if (!PlayerData.isVaultLoaded()) {
            getLogger().log(Level.SEVERE, "Vault Permissions Handler not found! Can't Enable!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        survivorChecker = new SurvivorChecker(this);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                survivorChecker.reload();
            }
        };
        pDataM.getHandler().runAfterLoad(r);
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

    protected SurvivorChecker getSurvivorChecker() {
        return survivorChecker;
    }
}
