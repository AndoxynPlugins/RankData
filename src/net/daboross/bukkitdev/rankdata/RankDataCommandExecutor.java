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
    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommand.equals("reload")) {
            runReloadCommand(sender, mainCommand, subCommandArgs);
        }
    }

    @Override
    public String getCommandName() {
        return "rd";
    }

    private void runReloadCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(ColorList.MAIN + "Reloading Survivor Info");
        rDataM.getSurvivorReloader().reload();
    }
}
