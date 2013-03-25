package net.daboross.bukkitdev.rankdata;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author daboross
 */
public class ReloadSurvivors {

    private RankData rDataMain;

    public ReloadSurvivors(RankData main) {
        this.rDataMain = main;
    }

    protected void reload() {
        PlayerDataHandler handler = rDataMain.getPDataMain().getHandler();
        if (handler == null) {
            return;
        }
        PData[] pDataList = rDataMain.getPDataMain().getHandler().getAllPDatas();
        for (int i = 0; i < pDataList.length; i++) {
            PData current = pDataList[i];
            if (isReadyCheck(current)) {
                setSurvivor(current);
                rDataMain.getLogger().log(Level.INFO, "{0} is ready for Survivor!", current.userName());
            }
        }
    }
    private static final int daysSinceOnlineAllowed = 4;
    private static final int hoursSpentOnline = 15;
    private static final int daysSinceJoin = 60;

    private boolean isReadyCheck(PData pData) {
        if (isCorrectGroup(pData)) {
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
        return false;
    }

    private boolean isCorrectGroup(PData pData) {
        if (pData.hasPermission("trusted") && !pData.hasPermission("survivor")) {
            return true;
        } else {
            return false;
        }
    }

    private void setSurvivor(PData pData) {
        PermissionUser permUser = pData.getPermUser();
        if (permUser != null) {
            if (permUser.inGroup("Trusted")) {
                permUser.addGroup("Survivor");
                permUser.removeGroup("Trusted");
            }
        }
    }
}
