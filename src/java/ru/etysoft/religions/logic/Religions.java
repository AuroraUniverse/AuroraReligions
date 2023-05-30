package ru.etysoft.religions.logic;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.etysoft.aurorauniverse.data.Towns;
import ru.etysoft.aurorauniverse.structures.Structure;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.aurorauniverse.world.Town;

import java.util.*;

public class Religions {

    private static HashMap<String, TownReligion> religionsOfTowns = new HashMap<>();

//    private static HashMap<String, String> bannedFoodChr = new HashMap<>();
//    private static HashMap<String, String> bannedFoodMus = new HashMap<>();
//    private static HashMap<String, String> bannedFoodBud = new HashMap<>();

    private static HashMap<String, HashMap<String, String>> bannedFood = new HashMap<>();

    public static List<String> religionNames = new ArrayList<>();

    public static HashMap<String, TownReligion> getReligionsOfTowns() {
        return religionsOfTowns;
    }

    public static HashMap<String, String> getBannedFood(String religion) {
        return bannedFood.get(religion);
    }

    public static boolean isBannedFood(String name) {
        for (String religionName: religionNames) {
            if (bannedFood.get(religionName).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean initialiseReligions() {

        boolean status = true;

        try {
            List<String> religions = AuroraReligions.getInstance().getConfig().getStringList("religions");
            religionNames.addAll(religions);

            for (Town town: Towns.getTowns()) {

                if (religionsOfTowns.containsKey(town.getName()))
                {
                    TownReligion townReligion = religionsOfTowns.get(town.getName());
                    String religion = townReligion.getReligion();
                    Structure structure = townReligion.getStructure();

                    boolean isFullBuilt = townReligion.getStructure().isFullBuilt();

                    if (isFullBuilt) {
                        // Chunk Tax
                        double chunkTaxMultiplier = AuroraReligions.getInstance().getConfig().getDouble("chunk-tax." + religion);

                        town.setTownChunkTaxMultiplier(AuroraReligions.getInstance().getDescription().getName(), chunkTaxMultiplier);
                    }

                }
            }

            // BannedFood

            for (String religionName: religionNames) {
                bannedFood.put(religionName, new HashMap<>());
                List<String> banned = AuroraReligions.getInstance().getConfig().getStringList("banned-food." + religionName);

                if (banned.size() > 0) {
                    for (String food: banned) {
                        String[] food1 = food.split("!");
                        bannedFood.get(religionName).put(food1[0], food1[1]);
                    }
                }
            }

//            List<String> chrBanned = AuroraReligions.getInstance().getConfig().getStringList("banned-food.chr");
//            List<String> musBanned = AuroraReligions.getInstance().getConfig().getStringList("banned-food.mus");
//            List<String> budBanned = AuroraReligions.getInstance().getConfig().getStringList("banned-food.bud");
//
//            if (chrBanned.size() > 0) {
//                for (String food: chrBanned) {
//                    String[] food1 = food.split("!");
//                    bannedFoodChr.put(food1[0], food1[1]);
//                }
//            }
//
//            if (musBanned.size() > 0) {
//                for (String food: musBanned) {
//                    String[] food1 = food.split("!");
//                    bannedFoodMus.put(food1[0], food1[1]);
//                }
//            }
//
//            if (budBanned.size() > 0) {
//                for (String food: budBanned) {
//                    String[] food1 = food.split("!");
//                    bannedFoodBud.put(food1[0], food1[1]);
//                }
//            }

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

    public static String getReligionOfTown(String townName) {
        try {
            String result = religionsOfTowns.get(townName).getReligion();

            return  result;
        } catch (Exception e) {
            return null;
        }
    }

    public static TownReligion getTownReligionFromHashMap(String towName) {
        try {
            TownReligion townReligion = religionsOfTowns.get(towName);

            return townReligion;
        } catch (Exception e) {
            return null;
        }
    }

    public static void giveEffectOnPlayer(Player player, String string) {

        if (string.contains("exp")) {
            String[] str = string.split(":");

            Random random = new Random();
            int rand = random.nextInt(100);
            int a = Integer.parseInt(str[2]);

            if (rand < a) {
                int force = Integer.parseInt(str[1]);
                player.getWorld().createExplosion(player.getLocation(), force);
                player.setHealth(0);
                LoggerReligions.info(player.getName() + " exploded 'cause he ate ");
            }
        } else {
            String[] bidString = string.split("/");
            for (String oneEffect: bidString) {
                String[] oneEffectStrings = oneEffect.split(":");
                LoggerReligions.info(player.getName() + " took effect " + oneEffect);

                String effectName = oneEffectStrings[0];
                int timeOfEffect = Integer.parseInt(oneEffectStrings[1]) * 20;
                int levelOfEffect = Integer.parseInt(oneEffectStrings[2]);

                player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(effectName)),
                        timeOfEffect, levelOfEffect));
            }
        }

    }

}
