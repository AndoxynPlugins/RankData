package net.daboross.bukkitdev.rankdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author daboross
 */
public class AutoRanker {

    public static void addTrusted(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "Trusted", ranker);
    }

    public static void addArchitect(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "Architect", ranker);
    }

    public static void addSurvivor(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "Survivor", ranker);
    }

    public static void addTechnician(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "Technician", ranker);
    }

    public static void addAgent(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "Agent", ranker);
    }

    public static void addSpy(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "Spy", ranker);
    }

    public static void addPvpmaster(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "pvpmaster", ranker);
    }

    public static void addSigner(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "signer", ranker);
    }

    public static void addSpawner(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "spawner", ranker);
    }

    public static void addRegionOwnerAdder(PData pDataToBeRanked, CommandSender ranker) {
        addGroup(pDataToBeRanked, "regionOwnerAdder", ranker);
    }

    public static void addGroup(PData pData, String groupName, CommandSender ranker) {
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
            rawData.add("ADD " + ranker.getName() + " " + groupName);
            Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
            pData.addData(finalData);
            ranker.sendMessage(ColorList.NUMBER + groupName + ColorList.MAIN + " has been added to " + ColorList.NAME + pData.userName());
        } else {
            ranker.sendMessage(ColorList.NUMBER + groupName + ColorList.MAIN + " has already been added to " + ColorList.NAME + pData.userName() + ColorList.MAIN + "...");
        }
    }

    public static void setRanks(PermissionUser permissionUser, String[] permissionGroups) {
        for (PermissionGroup permissionGroup : permissionUser.getGroups()) {
            permissionUser.removeGroup(permissionGroup);
        }
        permissionUser.setGroups(permissionGroups);
    }
}
