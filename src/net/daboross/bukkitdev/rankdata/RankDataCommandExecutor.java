package net.daboross.bukkitdev.rankdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class RankDataCommandExecutor extends CommandExecutorBase implements CommandExecutorBase.CommandReactor {

    private final RankData rDataM;
    private final PlayerDataHandler playerDataHandler;
    private final Set<String> groups;

    protected RankDataCommandExecutor(RankData mainPlugin) {
        rDataM = mainPlugin;
        playerDataHandler = rDataM.getPDataMain().getHandler();
        groups = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PlayerData.getPermissionHandler().getGroups())));
        initCommands();
    }

    private void initCommands() {
        initCommand("checksurvivors", true, "rankdata.checksurvivors", "Reload Survivor Info", this);
        initCommand("viewrecords", true, "rankdata.viewrecords", new String[]{"Player"}, "Views Rank Records on a Player", this);
        for (String group : groups) {
            initCommand("add" + group, true, "rankdata.addgroup." + group, new String[]{"Player"}, "Adds " + group + " to the given Player", this);
        }
    }

    @Override
    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel,
            String[] subCommandArgs, CommandExecutorBridge executorBridge) {
        if (subCommand.equals("checksurvivors")) {
            sender.sendMessage(ColorList.MAIN + "Checking Survivor Info");
            rDataM.getSurvivorChecker().reload();
            sender.sendMessage(ColorList.MAIN + "Done Checking. Results sent to logger");
        } else if (subCommand.equals("viewrecords")) {
            PData pData = getPDataFromCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                Data d = pData.getData("rankrecord");
                if (d == null) {
                    sender.sendMessage(ColorList.MAIN + "No Rank Record found for Player: " + pData.userName());
                    return;
                }
                String[] rawData = d.getData();
                sender.sendMessage(ColorList.MAIN + "Data:");
                for (String str : rawData) {
                    sender.sendMessage(ColorList.MAIN + str);
                }
                sender.sendMessage(ColorList.MAIN + "End Data");
            }
        } else if (subCommand.startsWith("add")) {
            String group = subCommand.substring(3);//3 because "add" has a length of 3.
            if (!groups.contains(group)) {
                sender.sendMessage("This group doesn't exist? This is an error with RankData or your permissions plugin.");
            }
            PData pData = getPDataFromCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                RankTracker.addGroup(pData, group, sender);
            }
        } else if (subCommand.startsWith("remove")) {
            String group = subCommand.substring(6);//6 because "remove" has a length of 6.
            if (!groups.contains(group)) {
                sender.sendMessage("This group doesn't exist? This is an error with RankData or your permissions plugin.");
            }
            PData pData = getPDataFromCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
//                RankTracker.removeGroup(pData, group, sender);
                sender.sendMessage("This command still needs to be implemented! Dabo was lazy while making this plugin, so now you can't remove people from groups!");
            }
        }
    }

    private PData getPDataFromCommand(CommandSender sender, String mainCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Specify a Player");
            sender.sendMessage(getHelpMessage(subCommandLabel, mainCommandLabel));
            return null;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please only use one argument");
            sender.sendMessage(getHelpMessage(subCommandLabel, mainCommandLabel));
            return null;
        }
        PData pData = playerDataHandler.getPData(subCommandArgs[0]);
        if (pData == null) {
            sender.sendMessage(ColorList.ERROR + "Player: " + ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " not found!");
            return null;
        }
        return pData;
    }

    @Override
    public String getCommandName() {
        return "rd";
    }

    @Override
    protected String getMainCmdPermission() {
        return "rankdata.help";
    }
}
