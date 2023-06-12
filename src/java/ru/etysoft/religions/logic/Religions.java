package ru.etysoft.religions.logic;

import ru.etysoft.aurorauniverse.data.Towns;
import ru.etysoft.aurorauniverse.structures.Structure;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.religions.enchantments.PlayerEnchantmentOffers;

import java.util.*;

public class Religions {

    private static HashMap<String, TownReligion> religionsOfTowns = new HashMap<>();

    public static List<String> religionNames = new ArrayList<>();

    public static HashMap<String, TownReligion> getReligionsOfTowns() {
        return religionsOfTowns;
    }

    public static boolean initialiseReligions() {

        boolean status = true;

        try {
            religionNames.clear();
            List<String> religions = AuroraReligions.getInstance().getConfig().getStringList("religions");
            religionNames.addAll(religions);

            for (Town town : Towns.getTowns()) {

                if (religionsOfTowns.containsKey(town.getName())) {
                    TownReligion townReligion = religionsOfTowns.get(town.getName());

                    if (!religionNames.contains(townReligion.getReligion())) {
                        religionsOfTowns.remove(town.getName());
                    }

                }
            }

            ReligionEffect.initialiseBannedFood();
            ReligionEffect.initialiseOnKillEffects();

            PlayerEnchantmentOffers.initialiseEnchantments();

        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error("Some Religions has not been initialised!");
            status = false;
        }

        return status;
    }

    public static void createTownReligion(Town town, String religion, Structure structure) {
        religionsOfTowns.put(town.getName(), new TownReligion(
                town.getName(), religion, structure
        ));

        setMultipliers(town, religion);
    }

    public static void deleteTownReligion(Town town) {
        religionsOfTowns.remove(town.getName());

        // Chunk Tax
        resetMultipliers(town);
    }

    public static void setMultipliers(Town town, String religion) {
        // Chunk Tax
        double chunkTax = AuroraReligions.getInstance().getConfig().getDouble("chunk-tax." + religion);
        town.setTownChunkTaxMultiplier(AuroraReligions.getInstance().getDescription().getName(), chunkTax);
    }

    public static void resetMultipliers(Town town) {
        // Chunk Tax
        town.setTownChunkTaxMultiplier(AuroraReligions.getInstance().getDescription().getName(), 1);
    }

    public static TownReligion getTownReligion(String townName) {
        try {
            TownReligion townReligion = religionsOfTowns.get(townName);

            return townReligion;
        } catch (Exception e) {
            return null;
        }
    }

}
