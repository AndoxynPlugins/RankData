package net.daboross.bukkitdev.rankdata;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;

/**
 *
 * @author daboross
 */
public class SurvivorChecker {

    private RankData rDataMain;

    public SurvivorChecker(RankData main) {
        this.rDataMain = main;
    }

    protected void reload() {
        Logger l = rDataMain.getLogger();
        rDataMain.getLogger().info("Starting Survivor Check");
        PlayerDataHandler handler = rDataMain.getPDataMain().getHandler();
        if (handler == null) {
            return;
        }
        PData[] pDataList = rDataMain.getPDataMain().getHandler().getAllPDatas();
        int foundReady = 0;
        for (int i = 0; i < pDataList.length; i++) {
            PData current = pDataList[i];
            Permission perm = PlayerData.getPermissionHandler();
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
    private static final int hoursSpentOnline = 15;
    private static final int daysSinceJoin = 60;

    private boolean isReadyCheck(Permission p, PData pData) {
        if (pData != null) {
            if (isCorrectGroup(p, pData)) {
                if (pData.joinedLastWithinDays(daysSinceOnlineAllowed)) {
                    if (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - pData.getFirstLogIn().time()) >= daysSinceJoin) {
                        if (TimeUnit.MILLISECONDS.toHours(pData.timePlayed()) >= hoursSpentOnline) {
                            return true;
                        } else {
                            rDataMain.getLogger().log(Level.FINER, "{0} would be ready for Survivor, but time spent online is not enough", pData.userName());
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCorrectGroup(Permission p, PData pData) {
        if (p.playerInGroup((String) null, pData.userName(), "Trusted") && !p.playerInGroup((String) null, pData.userName(), "Survivor")) {
            return true;
        }
        return false;
    }

    private void setSurvivor(Permission p, PData pData) {
        RankTracker.addGroup(p, pData, "Survivor", Bukkit.getServer().getConsoleSender());
    }
}
