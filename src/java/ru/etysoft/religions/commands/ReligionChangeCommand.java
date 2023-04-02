package ru.etysoft.religions.commands;

import org.bukkit.command.CommandSender;
import ru.etysoft.aurorauniverse.data.Towns;
import ru.etysoft.aurorauniverse.exceptions.TownNotFoundedException;
import ru.etysoft.aurorauniverse.utils.Messaging;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.religions.logic.Religions;
import ru.etysoft.religions.utils.ReligionsLanguage;

public class ReligionChangeCommand {
    public ReligionChangeCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("aurorareligions.reset")) {
            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("wrong-access"), sender);
            return;
        }

        if (args.length < 3)  {
            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("wrong-args"), sender);
            return;
        }

        String townName = "";
        for (int i = 2; i < args.length; i++) {
            if (i != args.length - 1) {
                townName += args[i];
            } else {
                townName += args[i] + " ";
            }
        }

        Town town;
        try {
            town = Towns.getTown(townName);
        } catch (TownNotFoundedException e) {
            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("wrong-args"), sender);
            return;
        }

        String religion = args[1];

        if (!Religions.religionNames.contains(religion)) {
            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("wrong-args"), sender);
            return;
        }

//        if (Religions.getReligionsOfTowns().containsKey(townName)) {
//            Religions.deleteTownReligion(town);
//            Religions.createTownReligion(town, religion);
//
//            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("religion-change").replace("%s", townName)
//                    .replace("%r", religion), sender);
//        } else {
//            Religions.createTownReligion(town, religion);
//            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("religion-change").replace("%s", townName)
//                    .replace("%r", religion), sender);
//        }
    }
}
