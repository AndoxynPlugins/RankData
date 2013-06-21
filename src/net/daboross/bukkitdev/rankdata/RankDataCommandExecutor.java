package net.daboross.bukkitdev.rankdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 *
 * @author daboross
 */
public class RankDataCommandExecutor implements SubCommandHandler {

    private final CommandExecutorBase commandExecutorBase;
    private final RankData rDataM;
    private final PlayerDataHandler playerDataHandler;
    private final Set<String> groups;

    protected RankDataCommandExecutor(RankData mainPlugin) {
        rDataM = mainPlugin;
        playerDataHandler = rDataM.getPDataMain().getHandler();
        groups = Collections.unmodifiableSet(findGroups());
        commandExecutorBase = new CommandExecutorBase("rankdata.help");
        initRegCommands();
        initAddCommands();
        initRemoveCommands();
        initSetCommands();
    }

    protected void registerCommand(PluginCommand command) {
        command.setExecutor(commandExecutorBase);
    }

    private Set<String> findGroups() {
        return new HashSet<String>(Arrays.asList(PlayerData.getPermissionHandler().getGroups()));
    }

    private void initRegCommands() {
        commandExecutorBase.addSubCommand(new SubCommand("checksurvivors", true, "rankdata.checksurvivors", "Reload Survivor Info", this));
        commandExecutorBase.addSubCommand(new SubCommand("viewrecords", true, "rankdata.viewrecords", new String[]{"Player"}, "Views Rank Records on a Player", this));
        commandExecutorBase.addSubCommand(new SubCommand("reload", true, "rankdata.reload", new String[]{"Player"}, "Reconfigures rankdata after a permissions group change", new SubCommandHandler() {
            @Override
            public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
                sender.sendMessage(ColorList.MAIN + "Reloading RankData");
                rDataM.getLogger().log(Level.INFO, "{0}Reloading RankData", ColorList.MAIN);
                rDataM.onEnable();
                rDataM.getLogger().log(Level.INFO, "{0}RankData Reloaded", ColorList.MAIN);
                sender.sendMessage(ColorList.MAIN + "RankData Reloaded");
            }
        }));
    }

    private void initAddCommands() {
        for (final String group : groups) {
            commandExecutorBase.addSubCommand(new SubCommand("add" + group, true, "rankdata.addgroup." + group, new String[]{"Player"}, "Adds " + group + " to the given Player", new SubCommandHandler() {
                @Override
                public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
                    if (!groups.contains(group)) {
                        sender.sendMessage("This group doesn't exist? This is an error with RankData! Try reloading it with /rd reload!");
                        return;
                    }
                    PData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
                    if (pData != null) {
                        RankTracker.addGroup(pData, group, sender);
                    }
                }
            }));
        }
    }

    private void initRemoveCommands() {
        for (final String group : groups) {
            commandExecutorBase.addSubCommand(new SubCommand("remove" + group, true, "rankdata.removegroup." + group, new String[]{"Player"}, "Removes  " + group + " from the given Player", new SubCommandHandler() {
                @Override
                public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
                    if (!groups.contains(group)) {
                        sender.sendMessage("This group doesn't exist? This is an error with RankData! Try reloading it with /rd reload!");
                    }
                    PData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
                    if (pData != null) {
                        RankTracker.removeGroup(pData, group, sender);
                    }
                }
            }));
        }
    }

    private void initSetCommands() {
        for (final String group : groups) {
            commandExecutorBase.addSubCommand(new SubCommand("set" + group, true, "rankdata.setgroup." + group, new String[]{"Player"}, "Removes all groups from and then adds " + group + " to the given Player", new SubCommandHandler() {
                @Override
                public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
                    if (!groups.contains(group)) {
                        sender.sendMessage("This group doesn't exist? This is an error with RankData! Try reloading it with /rd reload!");
                    }
                    PData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
                    if (pData != null) {
                        RankTracker.setGroups(pData, new String[]{group}, sender);
                    }
                }
            }));
        }
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommand.getName().equals("checksurvivors")) {
            sender.sendMessage(ColorList.MAIN + "Checking Survivor Info");
            rDataM.getSurvivorChecker().reload();
            sender.sendMessage(ColorList.MAIN + "Done Checking. Results sent to logger");
        } else if (subCommand.getName().equals("viewrecords")) {
            PData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                Data d = pData.getData("rankrecord");
                if (d == null) {
                    sender.sendMessage(ColorList.MAIN + "No Rank Record found for Player: " + ColorList.NAME + pData.userName());
                    return;
                }
                String[] datatosend = d.getData();
                sender.sendMessage(ColorList.MAIN + "Data on player " + ColorList.NAME + pData.userName() + ":");
                for (int i = 0; i < datatosend.length; i++) {
                    datatosend[i] = formatDataLine(datatosend[i]);
                }
                sender.sendMessage(datatosend);
            }
        }
    }

    private static String formatDataLine(String dataline) {
        String[] data = dataline.split(" ");
        if (data.length < 4) {
            return "Invalid Data '" + dataline + "'";
        }
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(ColorList.NAME).append(data[1]).append(ColorList.MAIN);
        if (data[0].equalsIgnoreCase("set")) {
            resultBuilder.append(" set ");
        } else if (data[0].equalsIgnoreCase("add")) {
            resultBuilder.append(" added ");
        } else if (data[0].equalsIgnoreCase("remove")) {
            resultBuilder.append(" removed ");
        } else {
            resultBuilder.append(" ?:").append(data[0]).append(" ");
        }
        long date;
        try {
            date = Long.parseLong(data[3]);
        } catch (NumberFormatException nfe) {
            date = -1;
        }
        String dateString = date == -1 ? "UnknownDate:" + date : new Date(date).toString();
        return resultBuilder.append(ColorList.NUMBER).append(data[2]).append(ColorList.MAIN).append(" at ").append(ColorList.NUMBER).append(dateString).toString();
    }

    private PData getPDataFromCommand(CommandSender sender, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Specify a Player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return null;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please only use one argument");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return null;
        }
        PData pData = playerDataHandler.getPData(subCommandArgs[0]);
        if (pData == null) {
            sender.sendMessage(ColorList.ERROR + "Player: " + ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " not found!");
            return null;
        }
        return pData;
    }
}
