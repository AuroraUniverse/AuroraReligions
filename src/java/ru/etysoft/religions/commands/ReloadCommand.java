package ru.etysoft.religions.commands;

import org.bukkit.command.CommandSender;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.LoggerReligions;

public class ReloadCommand {

    public ReloadCommand(CommandSender sender) {
        if (sender.hasPermission("aurorareligions.reload")) {
            AuroraReligions.reload(sender);
        } else {
            LoggerReligions.info(sender.getName() + " tried to reload AuroraReligion");
        }
    }
}
