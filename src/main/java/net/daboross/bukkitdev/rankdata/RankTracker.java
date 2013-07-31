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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerDataStatic;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class RankTracker {

    public static void addGroup(PlayerData pData, String group, CommandSender ranker) {
        addGroup(PlayerDataStatic.getPermissionHandler(), pData, group, ranker);
    }

    public static void setGroups(PlayerData pData, String[] groups, CommandSender ranker) {
        setGroups(PlayerDataStatic.getPermissionHandler(), pData, groups, ranker);
    }

    public static void removeGroup(PlayerData pData, String group, CommandSender ranker) {
        removeGroup(PlayerDataStatic.getPermissionHandler(), pData, group, ranker);
    }

    public static void addGroup(Permission permissionHandler, PlayerData pData, String group, CommandSender ranker) {
        if (permissionHandler == null || pData == null || group == null || ranker == null) {
            throw new IllegalArgumentException("One or more null arguments");
        }
        if (!permissionHandler.playerInGroup((String) null, pData.getUsername(), group)) {
            permissionHandler.playerAddGroup((String) null, pData.getUsername(), group);
            List<String> rawData;
            if (pData.hasExtraData("rankrecord")) {
                rawData = new ArrayList<String>(Arrays.asList(pData.getExtraData("rankrecord")));
            } else {
                rawData = new ArrayList<String>();
            }
            rawData.add("ADD " + ranker.getName() + " " + group + " " + System.currentTimeMillis());
            pData.addExtraData("rankrecord", rawData.toArray(new String[rawData.size()]));
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has been added to " + ColorList.NAME + pData.getUsername());
        } else {
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has already been added to " + ColorList.NAME + pData.getUsername() + ColorList.REG + "...");
        }
    }

    public static void setGroups(Permission permissionHandler, PlayerData pData, String[] groups, CommandSender ranker) {
        for (String group : permissionHandler.getPlayerGroups((String) null, pData.getUsername())) {
            permissionHandler.playerRemoveGroup((String) null, pData.getUsername(), group);
        }
        for (String group : groups) {
            permissionHandler.playerAddGroup((String) null, pData.getUsername(), group);
        }
        List<String> rawData;
        if (pData.hasExtraData("rankrecord")) {
            rawData = new ArrayList<String>(Arrays.asList(pData.getExtraData("rankrecord")));
        } else {
            rawData = new ArrayList<String>(1);
        }
        rawData.add("SET " + ranker.getName() + " " + Arrays.toString(groups) + " " + System.currentTimeMillis());
        pData.addExtraData("rankrecord", rawData.toArray(new String[rawData.size()]));
    }

    public static void removeGroup(Permission permissionHandler, PlayerData pData, String group, CommandSender ranker) {
        if (permissionHandler == null || pData == null || group == null || ranker == null) {
            throw new IllegalArgumentException("One or more null arguments");
        }
        if (permissionHandler.playerInGroup((String) null, pData.getUsername(), group)) {
            permissionHandler.playerRemoveGroup((String) null, pData.getUsername(), group);
            List<String> rawData;
            if (pData.hasExtraData("rankrecord")) {
                rawData = new ArrayList<String>(Arrays.asList(pData.getExtraData("rankrecord")));
            } else {
                rawData = new ArrayList<String>();
            }
            rawData.add("REMOVE " + ranker.getName() + " " + group + " " + System.currentTimeMillis());
            pData.addExtraData("rankrecord", rawData.toArray(new String[rawData.size()]));
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has been removed from " + ColorList.NAME + pData.getUsername());
        } else {
            ranker.sendMessage(ColorList.DATA + group + ColorList.REG + " has already been removed from " + ColorList.NAME + pData.getUsername() + ColorList.REG + "...");
        }
    }
}
