package ru.etysoft.religions.commands;

import org.bukkit.command.CommandSender;
import ru.etysoft.aurorauniverse.utils.AuroraLanguage;
import ru.etysoft.aurorauniverse.utils.ColorCodes;
import ru.etysoft.aurorauniverse.utils.Messaging;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.utils.ReligionsLanguage;

public class AureInfoCommand {

    public AureInfoCommand(CommandSender sender) {
        if (sender.hasPermission("aurorareligions.info")) {
            String message = AuroraReligions.getPrefix() + " AuroraReligions is running successfully on version "
                    + AuroraReligions.getInstance().getDescription().getVersion();
            sender.sendMessage(ColorCodes.toColor("&b" + message));
        } else {
            LoggerReligions.info(sender.getName() + " tried to get AuroraReligions info");
            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("wrong-access"), sender);
        }

    }
}
