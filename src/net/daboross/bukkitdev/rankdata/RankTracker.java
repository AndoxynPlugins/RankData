package net.daboross.bukkitdev.rankdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class RankTracker {

    public static void addGroup(PData pData, String groupName, CommandSender ranker) {
        addGroup(PlayerData.getPermissionHandler(), pData, groupName, ranker);
    }

    public static void addGroup(Permission permissionHandler, PData pData, String groupName, CommandSender ranker) {
        if (permissionHandler == null || pData == null || groupName == null || ranker == null) {
            throw new IllegalArgumentException("One or more null arguments");
        }
        if (!permissionHandler.playerInGroup((String) null, pData.userName(), groupName)) {
            permissionHandler.playerAddGroup((String) null, pData.userName(), groupName);
            List<String> rawData;
            if (pData.hasData("rankrecord")) {
                rawData = new ArrayList<String>(Arrays.asList(pData.getData("rankrecord").getData()));
            } else {
                rawData = new ArrayList<String>();
            }
            rawData.add("ADD " + ranker.getName() + " " + groupName + " " + System.currentTimeMillis());
            Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
            pData.addData(finalData);
            ranker.sendMessage(ColorList.NUMBER + groupName + ColorList.MAIN + " has been added to " + ColorList.NAME + pData.userName());
        } else {
            ranker.sendMessage(ColorList.NUMBER + groupName + ColorList.MAIN + " has already been added to " + ColorList.NAME + pData.userName() + ColorList.MAIN + "...");
        }
    }

    public static void setRanks(Permission permissionHandler, PData pData, String[] permissionGroups, CommandSender ranker) {
        for (String group : permissionHandler.getPlayerGroups((String) null, pData.userName())) {
            permissionHandler.playerRemoveGroup((String) null, pData.userName(), group);
        }
        for (String group : permissionGroups) {
            permissionHandler.playerAddGroup((String) null, pData.userName(), group);
        }
        List<String> rawData;
        if (pData.hasData("rankrecord")) {
            rawData = new ArrayList<String>(Arrays.asList(pData.getData("rankrecord").getData()));
        } else {
            rawData = new ArrayList<String>();
        }

        rawData.add("SET " + ranker.getName() + " " + Arrays.asList(permissionGroups) + " " + System.currentTimeMillis());
        Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
        pData.addData(finalData);
    }
}
