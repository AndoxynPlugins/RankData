package net.daboross.bukkitdev.rankdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author daboross
 */
public class AutoRanker {

    public static void addTrusted(PData pData, String ranker) {
        addGroup(pData, "Trusted", ranker);
    }

    public static void addArchitect(PData pData, String ranker) {
        addGroup(pData, "Architect", ranker);
    }

    public static void addSurvivor(PData pData, String ranker) {
        addGroup(pData, "Survivor", ranker);
    }

    public static void addTechnician(PData pData, String ranker) {
        addGroup(pData, "Technician", ranker);
    }

    public static void addAgent(PData pData, String ranker) {
        addGroup(pData, "Agent", ranker);
    }

    public static void addSpy(PData pData, String ranker) {
        addGroup(pData, "Spy", ranker);
    }

    public static void addPvpmaster(PData pData, String ranker) {
        addGroup(pData, "pvpmaster", ranker);
    }

    public static void addSigner(PData pData, String ranker) {
        addGroup(pData, "signer", ranker);
    }

    public static void addSpawner(PData pData, String ranker) {
        addGroup(pData, "spawner", ranker);
    }

    public static void addRegionOwnerAdder(PData pData, String ranker) {
        addGroup(pData, "regionOwnerAdder", ranker);
    }

    public static void addGroup(PData pData, String groupName, String ranker) {
        PermissionUser permissionUser = pData.getPermUser();
        if (permissionUser == null || groupName == null) {
            throw new IllegalArgumentException("One or more null arguments");
        }
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (!permissionUser.inGroup(group)) {
            for (PermissionGroup currentGroup : permissionUser.getGroups()) {
                if (group.isChildOf(currentGroup)) {
                    permissionUser.removeGroup(currentGroup);
                }
            }
            permissionUser.addGroup(group);
            List<String> rawData;
            if (pData.hasData("rankrecord")) {
                rawData = new ArrayList<String>(Arrays.asList(pData.getData("rankrecord").getData()));
            } else {
                rawData = new ArrayList<String>();
            }
            rawData.add("ADD " + ranker + " " + groupName);
            Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
            pData.addData(finalData);
        }
    }

    public static void setRanks(PermissionUser permissionUser, String[] permissionGroups) {
        for (PermissionGroup permissionGroup : permissionUser.getGroups()) {
            permissionUser.removeGroup(permissionGroup);
        }
        permissionUser.setGroups(permissionGroups);
    }
}
