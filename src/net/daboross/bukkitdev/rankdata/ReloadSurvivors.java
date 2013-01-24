package net.daboross.bukkitdev.rankdata;

import net.daboross.bukkitdev.playerdata.PlayerDataHandler;

/**
 *
 * @author daboross
 */
public class ReloadSurvivors {

    private RankData rDataMain;

    public ReloadSurvivors(RankData main) {
        this.rDataMain = main;
    }

    public void reload() {
        PlayerDataHandler handler = rDataMain.getPDataMain().getHandler();
        if (handler == null) {
            return;
        }
    }
}
