package net.daboross.bukkitdev.rankdata;

import net.daboross.bukkitdev.playerdata.ColorList;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class RankDataCommandExecutor extends CommandExecutorBase {

    private RankData rDataM;
    private PlayerData pDataM;
    private PlayerDataHandler pDataH;

    /**
     *
     */
    protected RankDataCommandExecutor(RankData mainPlugin) {
        rDataM = mainPlugin;
        pDataM = mainPlugin.getPDataMain();
        pDataH = pDataM.getHandler();
        initCommand("help", new String[]{"?"}, true, "rankdata.help", "This Command Views This Page");
        initCommand("reload", new String[]{}, true, "rankdata.reload", "Reload Survivor Info");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rd")) {
            String commandName = isCommandValid(sender, cmd, label, args);
            if (commandName == null) {
                return true;
            }
            if (commandName.equalsIgnoreCase("help")) {
                runHelpCommand(sender, cmd, getSubArray(args));
            } else if (commandName.equalsIgnoreCase("reload")) {
                runReloadCommand(sender, cmd, getSubArray(args));
            }
            return true;
        }
        return false;
    }

    private void runReloadCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(ColorList.MAIN + "Reloading Survivor Info");
        rDataM.getSurvivorReloader().reload();
    }
}
