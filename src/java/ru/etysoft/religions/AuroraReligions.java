package ru.etysoft.religions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import ru.etysoft.aurorauniverse.AuroraUniverse;
import ru.etysoft.aurorauniverse.Logger;
import ru.etysoft.aurorauniverse.utils.Messaging;
import ru.etysoft.religions.commands.ReligionCommands;
import ru.etysoft.religions.data.ReligionsData;
import ru.etysoft.religions.listener.ReligionListener;
import ru.etysoft.religions.logic.Religions;


public class AuroraReligions extends JavaPlugin {

    private static AuroraReligions instance;

    private static String language = "russian";

    private static boolean hasWarnings = false;

    private static String prefix = ChatColor.GRAY + "[" + ChatColor.AQUA + "AuroraReligions" + ChatColor.GRAY + "]" + ChatColor.RESET;

    public static String getPrefix() {
        return prefix;
    }

    public static AuroraReligions getInstance() {
        return instance;
    }

    public static String getLanguage() {
        return language;
    }

    @Override
    public void onEnable() {
        LoggerReligions.info(">> &bAuroraReligions &r" + getDescription().getVersion() + " by " + getDescription().getAuthors() + "<<");
        instance = this;

        try
        {
            LoggerReligions.info("Loading configuration...");
            saveDefaultConfig();

            String configLanguage = this.getConfig().getString("language");

            if (!(configLanguage.equals("russian") || configLanguage.equals("english"))) {
                LoggerReligions.error("Language is not defined in config! Selected language - english");
            } else {
                LoggerReligions.info("Selected language - " + configLanguage);
                language = configLanguage;
            }

        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error("Config file doesn't have the language variable");
            hasWarnings = true;
        }

        LoggerReligions.info("Loading data...");
        if (ReligionsData.fromJSON()) {
            LoggerReligions.info("Data has been loaded successfully");
        } else {
            hasWarnings = true;
        }

        LoggerReligions.info("Initialising townReligions...");
        if (Religions.initialiseReligions()) {
            LoggerReligions.info("TownReligions has been initialised successfully");
        } else {
            hasWarnings = true;
        }

        LoggerReligions.info("Registering listeners...");
        try {
            instance.getServer().getPluginManager().registerEvents(new ReligionListener(), instance);
            LoggerReligions.info("AuroraReligions listener has been registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error("AuroraReligions listener hasn't been registered!");
            hasWarnings = true;
        }

        LoggerReligions.info("Registering commands...");
        try {
            PluginCommand command = getCommand("aurorareligions");
            command.setExecutor(new ReligionCommands());
            LoggerReligions.info("AuroraReligions commands registered successfully");
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error("AuroraReligions commands didn't registered");
            hasWarnings = true;
        }

        if (hasWarnings) {
            LoggerReligions.info("Plugin started with warnings!");
        } else {
            LoggerReligions.info("Plugin started successfully!");
        }

    }

    @Override
    public void onDisable() {
        LoggerReligions.info("Disabling AuroraUniverse-Religions plugin...");

        LoggerReligions.info("Saving Plugin data...");
        if (ReligionsData.toJSON()) {
            LoggerReligions.info("Plugin data has been saved successfully!");
        } else {
            LoggerReligions.error("Plugin data has been saved with error!");
        }

        LoggerReligions.info("AuroraUniverse-Religions plugin has been disabled successfully");
    }

    public static void reload(CommandSender sender) {
        LoggerReligions.info("Reloading configuration...");
        instance.reloadConfig();
        instance.saveDefaultConfig();

        if (Religions.initialiseReligions()) {
            LoggerReligions.info("TownReligions has been initialised successfully");
        } else {
            LoggerReligions.error("");
        }

        Messaging.sendPrefixedMessage("AuroraReligions has been reloaded", sender);
        LoggerReligions.info("Reloaded successfully");
    }

    public boolean isStructureEnabled() {
        return getConfig().getBoolean("structure-enabled");
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

}