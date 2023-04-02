package ru.etysoft.religions.data;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.etysoft.aurorauniverse.structures.Structure;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.logic.Religions;
import ru.etysoft.religions.logic.TownReligion;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ReligionsData {
    public static boolean toJSON() {

        boolean status = true;


        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();

        try
        {
            for (TownReligion townReligion : Religions.getReligionsOfTowns().values()) {
                JSONObject object = new JSONObject();

                object.put(Types.TOWN_NAME, townReligion.getTownName());
                object.put(Types.TOWN_RELIGION, townReligion.getReligion());
                object.put(Types.TOWN_STRUCTURE, townReligion.getStructure().toJson());

                jsonArray.add(object);
            }

            jsonObject.put(Types.DATA, jsonArray);

            saveStringToFile(jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error("Couldn't save AuroraReligions ");
            status = false;
        }

        return status;

    }

    public static boolean fromJSON() {

        boolean status = true;

        try
        {
            if (new File("plugins/AuroraReligions/religions.json").exists())
            {
                JSONParser jsonParser = new JSONParser();
                JSONObject mainJson = (JSONObject) jsonParser.parse(readFile());

                JSONArray jsonArray = (JSONArray) mainJson.get(Types.DATA);

                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JSONObject object = (JSONObject) jsonArray.get(i);

                    String townReligion = object.get(Types.TOWN_RELIGION).toString();
                    String townName = object.get(Types.TOWN_NAME).toString();
                    Structure structure = Structure.fromJSON((JSONObject) object.get(Types.TOWN_STRUCTURE));

                    Religions.getReligionsOfTowns().put(townName, new TownReligion(
                            townName, townReligion, structure
                    ));
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error("Couldn't load AuroraUniverse-Religions from JSON");
            status = false;
        }

        return status;

    }

    private static void saveStringToFile(String string) {
        try (PrintWriter out = new PrintWriter("plugins/AuroraReligions/religions.json")) {
            out.println(string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String readFile() throws IOException {
        File file = new File("plugins/AuroraReligions/religions.json");
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }


    private static class Types {
        public static final String TOWN_NAME = "TOWN_NAME";
        public static final String TOWN_RELIGION = "TOWN_RELIGION";
        public static final String TOWN_STRUCTURE = "TOWN_STRUCTURE";
        public static  final String DATA = "DATA";
    }
}
