package net.daboross.bukkitdev.rankdata;

import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author daboross
 */
public class RankDataCommandExecutor extends CommandExecutorBase implements CommandExecutorBase.CommandReactor {

    private final RankData rDataM;
    private final PlayerDataHandler playerDataHandler;

    protected RankDataCommandExecutor(RankData mainPlugin) {
        rDataM = mainPlugin;
        playerDataHandler = rDataM.getPDataMain().getHandler();
        initCommand("checksurvivors", true, "rankdata.checksurvivors", "Reload Survivor Info", this);
        initCommand("viewrecords", true, "rankdata.viewrecords", new String[]{"Player"}, "Views Rank Records on a Player", this);
        initCommand("addtrusted", true, "rankdata.addgroup.trusted", new String[]{"Player"}, "Adds the rank Trusted to the given player", this);
        initCommand("addsurvivor", true, "rankdata.addgroup.survivor", new String[]{"Player"}, "Adds the rank Survivor to the given player", this);
        initCommand("addarchitect", true, "rankdata.addgroup.architect", new String[]{"Player"}, "Adds the rank Architect to the given player", this);
        initCommand("addtechnician", true, "rankdata.addgroup.technician", new String[]{"Player"}, "Adds the rank Technician to the given player", this);
        initCommand("addagent", true, "rankdata.addgroup.agent", new String[]{"Player"}, "Adds the rank Agent to the given player", this);
        initCommand("addspy", true, "rankdata.addgroup.spy", new String[]{"Player"}, "Adds the rank Spy to the given player", this);
        initCommand("addpvpmaster", true, "rankdata.addgroup.pvpmaster", new String[]{"Player"}, "Adds the rank PvPMaster to the given player", this);
        initCommand("addsigner", true, "rankdata.addgroup.signer", new String[]{"Player"}, "Adds the rank Signer to the given player", this);
        initCommand("addspawner", true, "rankdata.addgroup.spawner", new String[]{"Player"}, "Adds the rank Spawner to the given player", this);
        initCommand("addregionowneradder", true, "rankdata.addgroupregionowneradder", new String[]{"Player"}, "Adds the rank RegionOwnerAdder to the given player", this);
    }

    @Override
    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel,
            String[] subCommandArgs, CommandExecutorBridge executorBridge) {
        if (subCommand.equals("checksurvivors")) {
            sender.sendMessage(ColorList.MAIN + "Checking Survivor Info");
            rDataM.getSurvivorChecker().reload();
            sender.sendMessage(ColorList.MAIN + "Done Checking. Results sent to logger");
        } else if (subCommand.equals("viewrecords")) {
            PData pData = checkPDataOnlyCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
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
        } else if (subCommand.equals("addtrusted")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addTrusted(pData, sender);
            }
        } else if (subCommand.equals("addsurvivor")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addSurvivor(pData, sender);
            }
        } else if (subCommand.equals("addarchitect")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addArchitect(pData, sender);
            }
        } else if (subCommand.equals("addtechnician")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addTechnician(pData, sender);
            }
        } else if (subCommand.equals("addagent")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addAgent(pData, sender);
            }
        } else if (subCommand.equals("addspy")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addSpy(pData, sender);
            }
        } else if (subCommand.equals("addpvpmaster")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addPvpmaster(pData, sender);
            }
        } else if (subCommand.equals("addsigner")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addSigner(pData, sender);
            }
        } else if (subCommand.equals("addspawner")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addSpawner(pData, sender);
            }
        } else if (subCommand.equals("addregionowneradder")) {
            PData pData = checkPermissionUserCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                AutoRanker.addRegionOwnerAdder(pData, sender);
            }
        }
    }

    private PData checkPermissionUserCommand(CommandSender sender, String mainCommandLabel, String subCommandLabel, String[] subCommandArgs) {
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
        PermissionUser permissionUser = pData.getPermUser();
        if (permissionUser == null) {
            sender.sendMessage(ColorList.ERROR + "PermissionsEx Not Loaded Correctly");
            return null;
        }
        return pData;
    }

    private PData checkPDataOnlyCommand(CommandSender sender, String mainCommandLabel, String subCommandLabel, String[] subCommandArgs) {
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
        PermissionUser permissionUser = pData.getPermUser();
        if (permissionUser == null) {
            sender.sendMessage(ColorList.ERROR + "PermissionsEx Not Loaded Correctly");
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
