package net.daboross.bukkitdev.rankdata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.ColorList;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class RankDataCommandExecutor implements CommandExecutor {

    private final Map<String, String> aliasMap = new HashMap<>();
    private final Map<String, Boolean> isConsoleMap = new HashMap<>();
    private final Map<String, String> helpList = new HashMap<>();
    private final Map<String, String[]> helpAliasMap = new HashMap<>();
    private final Map<String, String> permMap = new HashMap<>();
    private RankData pluginMain;
    private PlayerData pDataM;
    private PlayerDataHandler pDataH;

    /**
     *
     */
    protected RankDataCommandExecutor(RankData mainPlugin) {
        pluginMain = mainPlugin;
        pDataM = mainPlugin.getPDataMain();
        pDataH = pDataM.getHandler();
        initCommand("help", new String[]{"?"}, true, "dplugin.help", "This Command Views This Page");
        initCommand("subcommand", new String[]{"subcommandalias", "anotheralias"}, /* Can This Command Be Run From The Console? */ true, "dplugin.runcommand", "This Is The Help Message For Sub Command");
    }

    private void initCommand(String cmd, String[] aliases, boolean isConsole, String permission, String helpString) {
        aliasMap.put(cmd, cmd);
        for (String alias : aliases) {
            aliasMap.put(alias, cmd);
        }
        isConsoleMap.put(cmd, isConsole);
        permMap.put(cmd, permission);
        helpList.put(cmd, helpString);
        helpAliasMap.put(cmd, aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dcommand")) {
            if (args.length < 1) {
                sender.sendMessage(ColorList.MAIN + "This is a base command, Please Use a sub command after it.");
                sender.sendMessage(ColorList.MAIN + "To see all possible sub commands, type " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " ?");
                return true;
            }
            String commandName;
            if (aliasMap.containsKey(args[0].toLowerCase())) {
                commandName = aliasMap.get(args[0].toLowerCase());
            } else {
                sender.sendMessage(ColorList.MAIN + "The SubCommand: " + ColorList.CMD + args[0] + ColorList.MAIN + " Does not exist.");
                sender.sendMessage(ColorList.MAIN + "To see all possible sub commands, type " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " ?");
                return true;
            }
            if (sender instanceof Player) {
                if (!sender.hasPermission(permMap.get(commandName))) {
                    sender.sendMessage(ColorList.NOPERM + "You don't have permission to do this command!");
                    return true;
                }
            }
            boolean isConsole;
            if (isConsoleMap.containsKey(commandName)) {
                isConsole = isConsoleMap.get(commandName);
            } else {
                isConsole = false;
            }
            if (!(sender instanceof Player)) {
                if (!isConsole) {
                    sender.sendMessage(ColorList.NOPERM + "This command must be run by a player");
                    return true;
                }
            }
            if (commandName.equalsIgnoreCase("help")) {
                runHelpCommand(sender, cmd, getSubArray(args));
            } else if (commandName.equalsIgnoreCase("subcommand")) {
                runSubCommand(sender, cmd, getSubArray(args));
            }
            return true;
        }
        return false;
    }

    private String[] getSubArray(String[] array) {
        if (array.length > 1) {
            return Arrays.asList(array).subList(1, array.length).toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    private void runSubCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(ColorList.MAIN + "You Just Ran Sub Command");
        pluginMain.getLogger().log(Level.INFO, "{0} just ran Sub Command", sender.getName());
    }

    private void runHelpCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(net.daboross.bukkitdev.playerdata.ColorList.MAIN + "List Of Possible Sub Commands:");
        for (String str : aliasMap.keySet()) {
            if (str.equalsIgnoreCase(aliasMap.get(str))) {
                if (sender.hasPermission(str)) {
                    sender.sendMessage(getMultipleAliasHelpMessage(str, cmd.getLabel()));
                }
            }
        }
    }

    private String getHelpMessage(String alias, String baseCommand) {
        String str = aliasMap.get(alias);
        return (net.daboross.bukkitdev.playerdata.ColorList.CMD + "/" + baseCommand + net.daboross.bukkitdev.playerdata.ColorList.SUBCMD + " " + alias + net.daboross.bukkitdev.playerdata.ColorList.HELP + " " + helpList.get(aliasMap.get(str)));
    }

    private String getMultipleAliasHelpMessage(String subcmd, String baseCommand) {
        String[] aliasList = helpAliasMap.get(subcmd);
        String commandList = subcmd;
        for (String str : aliasList) {
            commandList += net.daboross.bukkitdev.playerdata.ColorList.DIVIDER + "/" + net.daboross.bukkitdev.playerdata.ColorList.SUBCMD + str;
        }
        return (net.daboross.bukkitdev.playerdata.ColorList.CMD + "/" + baseCommand + net.daboross.bukkitdev.playerdata.ColorList.SUBCMD + " " + commandList + net.daboross.bukkitdev.playerdata.ColorList.HELP + " " + helpList.get(subcmd));
    }
}
