package net.daboross.bukkitdev.rankdata;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import ru.tehkode.permissions.PermissionUser;

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
            PermissionUser permUser = current.getPermUser();
            if (isReadyCheck(current, permUser)) {
                foundReady++;
                setSurvivor(permUser);
                l.log(Level.INFO, "Survivor has been added to {0}", current.userName());
            }
        }
        l.log(Level.INFO, "Added Survivor to {0} users", foundReady);
    }
    private static final int daysSinceOnlineAllowed = 4;
    private static final int hoursSpentOnline = 15;
    private static final int daysSinceJoin = 60;

    private boolean isReadyCheck(PData pData, PermissionUser permUser) {
        if (permUser != null) {
            if (isCorrectGroup(permUser)) {
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

    private boolean isCorrectGroup(PermissionUser permUser) {
        if (permUser.inGroup("Trusted", true) && !permUser.inGroup("Survivor", true)) {
            return true;
        } else {
            return false;
        }
    }

    private void setSurvivor(PermissionUser permUser) {
        permUser.addGroup("Survivor");
    }
}
