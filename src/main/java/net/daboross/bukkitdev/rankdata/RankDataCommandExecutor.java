package net.daboross.bukkitdev.rankdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.PlayerDataStatic;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 *
 * @author daboross
 */
public class RankDataCommandExecutor implements SubCommandHandler {

    private final CommandExecutorBase commandExecutorBase;
    private final RankDataBukkit rankDataBukkit;
    private final PlayerHandler playerDataHandler;
    private final Set<String> groups;

    protected RankDataCommandExecutor(RankDataBukkit rankDataBukkit) {
        this.rankDataBukkit = rankDataBukkit;
        playerDataHandler = rankDataBukkit.getPDataMain().getHandler();
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
        return new HashSet<String>(Arrays.asList(PlayerDataStatic.getPermissionHandler().getGroups()));
    }

    private void initRegCommands() {
        commandExecutorBase.addSubCommand(new SubCommand("checksurvivors", true, "rankdata.checksurvivors", "Reload Survivor Info", this));
        commandExecutorBase.addSubCommand(new SubCommand("viewrecords", true, "rankdata.viewrecords", new String[]{"Player"}, "Views Rank Records on a Player", this));
        commandExecutorBase.addSubCommand(new SubCommand("reload", true, "rankdata.reload", new String[]{"Player"}, "Reconfigures rankdata after a permissions group change", new SubCommandHandler() {
            @Override
            public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
                sender.sendMessage(ColorList.REG + "Reloading RankData");
                rankDataBukkit.getLogger().log(Level.INFO, "{0}Reloading RankData", ColorList.REG);
                rankDataBukkit.onEnable();
                rankDataBukkit.getLogger().log(Level.INFO, "{0}RankData Reloaded", ColorList.REG);
                sender.sendMessage(ColorList.REG + "RankData Reloaded");
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
                    PlayerData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
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
                    PlayerData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
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
                    PlayerData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
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
            sender.sendMessage(ColorList.REG + "Checking for survivor");
            rankDataBukkit.getSurvivorChecker().reload();
            sender.sendMessage(ColorList.REG + "Done checking. Results sent to console");
        } else if (subCommand.getName().equals("viewrecords")) {
            PlayerData pData = getPDataFromCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
            if (pData != null) {
                String[] rankData = pData.getExtraData("rankrecord");
                if (rankData == null) {
                    sender.sendMessage(ColorList.REG + "No rank record found for player '" + ColorList.NAME + pData.getUsername() + ColorList.REG + "'");
                    return;
                }
                sender.sendMessage(ColorList.TOP_SEPERATOR + " -- " + ColorList.NAME + pData.getUsername() + ColorList.TOP + "'s Rank Data" + ColorList.TOP_SEPERATOR + " --");
                for (int i = 0; i < rankData.length; i++) {
                    rankData[i] = formatDataLine(rankData[i]);
                }
                sender.sendMessage(rankData);
            }
        }
    }

    private static String formatDataLine(String dataline) {
        String[] data = dataline.split(" ");
        if (data.length < 4) {
            return "Invalid data: '" + dataline + "'";
        }
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(ColorList.NAME).append(data[1]).append(ColorList.REG);
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
        return resultBuilder.append(ColorList.DATA).append(data[2]).append(ColorList.REG).append(" at ").append(ColorList.DATA).append(dateString).toString();
    }

    private PlayerData getPDataFromCommand(CommandSender sender, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return null;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "Please only use one argument");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return null;
        }
        PlayerData pData = playerDataHandler.getPlayerDataPartial(subCommandArgs[0]);
        if (pData == null) {
            sender.sendMessage(ColorList.ERR + "Player '" + ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + "' not found");
            return null;
        }
        return pData;
    }
}
