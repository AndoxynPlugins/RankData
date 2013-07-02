package net.daboross.bukkitdev.rankdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
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

    public static void addGroup(PData pData, String group, CommandSender ranker) {
        addGroup(PlayerData.getPermissionHandler(), pData, group, ranker);
    }

    public static void setGroups(PData pData, String[] groups, CommandSender ranker) {
        setGroups(PlayerData.getPermissionHandler(), pData, groups, ranker);
    }

    public static void removeGroup(PData pData, String group, CommandSender ranker) {
        removeGroup(PlayerData.getPermissionHandler(), pData, group, ranker);
    }

    public static void addGroup(Permission permissionHandler, PData pData, String group, CommandSender ranker) {
        if (permissionHandler == null || pData == null || group == null || ranker == null) {
            throw new IllegalArgumentException("One or more null arguments");
        }
        if (!permissionHandler.playerInGroup((String) null, pData.userName(), group)) {
            permissionHandler.playerAddGroup((String) null, pData.userName(), group);
            List<String> rawData;
            if (pData.hasData("rankrecord")) {
                rawData = new ArrayList<String>(Arrays.asList(pData.getData("rankrecord").getData()));
            } else {
                rawData = new ArrayList<String>();
            }
            rawData.add("ADD " + ranker.getName() + " " + group + " " + System.currentTimeMillis());
            Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
            pData.addData(finalData);
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has been added to " + ColorList.NAME + pData.userName());
        } else {
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has already been added to " + ColorList.NAME + pData.userName() + ColorList.REG + "...");
        }
    }

    public static void setGroups(Permission permissionHandler, PData pData, String[] groups, CommandSender ranker) {
        for (String group : permissionHandler.getPlayerGroups((String) null, pData.userName())) {
            permissionHandler.playerRemoveGroup((String) null, pData.userName(), group);
        }
        for (String group : groups) {
            permissionHandler.playerAddGroup((String) null, pData.userName(), group);
        }
        List<String> rawData;
        if (pData.hasData("rankrecord")) {
            rawData = new ArrayList<String>(Arrays.asList(pData.getData("rankrecord").getData()));
        } else {
            rawData = new ArrayList<String>();
        }

        rawData.add("SET " + ranker.getName() + " " + Arrays.toString(groups) + " " + System.currentTimeMillis());
        Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
        pData.addData(finalData);
    }

    public static void removeGroup(Permission permissionHandler, PData pData, String group, CommandSender ranker) {
        if (permissionHandler == null || pData == null || group == null || ranker == null) {
            throw new IllegalArgumentException("One or more null arguments");
        }
        if (permissionHandler.playerInGroup((String) null, pData.userName(), group)) {
            permissionHandler.playerRemoveGroup((String) null, pData.userName(), group);
            List<String> rawData;
            if (pData.hasData("rankrecord")) {
                rawData = new ArrayList<String>(Arrays.asList(pData.getData("rankrecord").getData()));
            } else {
                rawData = new ArrayList<String>();
            }
            rawData.add("REMOVE " + ranker.getName() + " " + group + " " + System.currentTimeMillis());
            Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
            pData.addData(finalData);
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has been removed from " + ColorList.NAME + pData.userName());
        } else {
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has already been removed from " + ColorList.NAME + pData.userName() + ColorList.REG + "...");
        }
    }
}
