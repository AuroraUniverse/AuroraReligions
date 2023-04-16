package ru.etysoft.religions.utils;

import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.aurorauniverse.utils.ColorCodes;

public class ReligionsLanguage {

    public static String getColorString(String path)
    {
        try {
            String fullPath = AuroraReligions.getLanguage() + "." + path;
            String text = AuroraReligions.getInstance().getConfig().getString(fullPath);

            if (text != null) {
                return ColorCodes.toColor(text);
            } else {
                LoggerReligions.warning("Can't get string " + fullPath + " in language file, trying default value!");
                try {
                    String result = AuroraReligions.getInstance().getConfig().getString(fullPath);
                    if (result != null) {
                        return result;
                    } else {
                        LoggerReligions.error("Can't find default string with path " + fullPath);
                        return ColorCodes.toColor("&cWrong path to String!");
                    }

                } catch (Exception e) {
                    LoggerReligions.error("Can't find default string with path " + fullPath + " (NullPointerException)");
                    return ColorCodes.toColor("&cWrong path to translation (" + fullPath + ")");
                }
            }
        }
        catch (Exception e)
        {
            return ColorCodes.toColor("&cConfig string error!");
        }
    }
}
