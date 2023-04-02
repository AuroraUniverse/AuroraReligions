package ru.etysoft.religions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReligionCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                new ReloadCommand(commandSender);
            } else if (args[0].equalsIgnoreCase("reset")) {
                new ReligionResetCommand(commandSender, args);
            } else if (args[0].equalsIgnoreCase("change")) {
                new ReligionChangeCommand(commandSender, args);
            }
        }

        return true;
    }
}
