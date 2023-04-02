package ru.etysoft.religions;

import org.bukkit.Bukkit;
import ru.etysoft.aurorauniverse.utils.ColorCodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class LoggerReligions {

    public static void log(String s)
    {
        writeToFile(s);
    }

    private static final String newLine = System.getProperty("line.separator");

    private static void writeToFile(String msg)  {
        String fileName = "plugins/AuroraReligions/log.txt";
        PrintWriter printWriter = null;
        File file = new File(fileName);
        try {
            if (!file.exists()) file.createNewFile();
            printWriter = new PrintWriter(new FileOutputStream(fileName, true));
            printWriter.write(newLine + msg);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }
    }

    public static void error(String s)
    {
        Bukkit.getConsoleSender().sendMessage(ColorCodes.toColor(AuroraReligions.getPrefix() + "&c " + s));
    }

    public static void info(String s)
    {
        Bukkit.getConsoleSender().sendMessage(ColorCodes.toColor(AuroraReligions.getPrefix() + " " + s));
    }

    public static void warning(String s)
    {
        Bukkit.getConsoleSender().sendMessage(ColorCodes.toColor(AuroraReligions.getPrefix() + "&e " + s));
    }
}
