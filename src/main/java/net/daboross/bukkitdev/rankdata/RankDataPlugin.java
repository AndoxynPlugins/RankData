/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.rankdata;

import java.io.IOException;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.api.PlayerDataPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

/**
 *
 * @author daboross
 */
public class RankDataPlugin extends JavaPlugin {

    private static RankDataPlugin currentInstance;
    private PlayerDataPlugin playerDataBukkit;
    private RankDataCommandExecutor commandExecutor;
    private SurvivorChecker survivorChecker;

    @Override
    public void onEnable() {
        Plugin playerDataPlugin = Bukkit.getPluginManager().getPlugin("PlayerData");
        if (playerDataPlugin == null) {
            getLogger().log(Level.SEVERE, "PlayerData Not Found!");
        } else if (playerDataPlugin instanceof PlayerDataPlugin) {
            playerDataBukkit = (PlayerDataPlugin) playerDataPlugin;
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
        survivorChecker = new SurvivorChecker(this);
        survivorChecker.reload();
        currentInstance = this;
        try {
            new MetricsLite(this).start();
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "Unable to initialize metrics");
        }
    }

    @Override
    public void onDisable() {
        currentInstance = null;
    }

    protected static RankDataPlugin getCurrentInstance() {
        return currentInstance;
    }

    protected PlayerDataPlugin getPlayerDataPlugin() {
        return playerDataBukkit;
    }

    protected SurvivorChecker getSurvivorChecker() {
        return survivorChecker;
    }
}
