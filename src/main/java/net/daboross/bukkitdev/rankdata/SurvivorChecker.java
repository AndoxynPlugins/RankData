/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerDataStatic;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;

/**
 *
 * @author daboross
 */
public class SurvivorChecker {

    private RankDataPlugin rankDataBukkit;

    public SurvivorChecker(RankDataPlugin main) {
        this.rankDataBukkit = main;
    }

    protected void reload() {
        Logger l = rankDataBukkit.getLogger();
        rankDataBukkit.getLogger().info("Starting Survivor Check");
        PlayerHandler handler = rankDataBukkit.getPlayerDataPlugin().getHandler();
        if (handler == null) {
            return;
        }
        List<? extends PlayerData> pDataList = rankDataBukkit.getPlayerDataPlugin().getHandler().getAllPlayerDatas();
        int foundReady = 0;
        for (int i = 0; i < pDataList.size(); i++) {
            PlayerData current = pDataList.get(i);
            Permission perm = PlayerDataStatic.getPermissionHandler();
            if (isReadyCheck(perm, current)) {
                foundReady++;
                setSurvivor(perm, current);
            }
        }
        if (foundReady != 0) {
            l.log(Level.INFO, "Added Survivor to {0} users", foundReady);
        } else {
            l.log(Level.INFO, "Done Checking Survivors");
        }
    }
    private static final int daysSinceOnlineAllowed = 4;
    private static final long daysSinceOnlineAllowedInMillis = TimeUnit.DAYS.toMillis(daysSinceOnlineAllowed);
    private static final int hoursSpentOnline = 15;
    private static final int daysSinceJoin = 60;

    private boolean isReadyCheck(Permission p, PlayerData pd) {
        if (pd != null) {
            if (isCorrectGroup(p, pd)) {
                if (System.currentTimeMillis() - pd.getLastSeen() >= daysSinceOnlineAllowedInMillis) {
                    if (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - pd.getAllLogins().get(0).getDate()) >= daysSinceJoin) {
                        if (TimeUnit.MILLISECONDS.toHours(pd.getTimePlayed()) >= hoursSpentOnline) {
                            return true;
                        } else {
                            rankDataBukkit.getLogger().log(Level.FINER, "{0} would be ready for Survivor, but time spent online is not enough", pd.getUsername());
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCorrectGroup(Permission p, PlayerData pd) {
        if (p.playerInGroup((String) null, pd.getUsername(), "Trusted") && !p.playerInGroup((String) null, pd.getUsername(), "Survivor")) {
            return true;
        }
        return false;
    }

    private void setSurvivor(Permission p, PlayerData pd) {
        RankTracker.addGroup(p, pd, "Survivor", Bukkit.getServer().getConsoleSender());
    }
}
