package ru.etysoft.religions.logic;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.LoggerReligions;

import java.util.*;

public class ReligionEffect {

    private static HashMap<String, HashMap<String, ArrayList<ReligionEffect>>> bannedFood = new HashMap<>();

    private static HashMap<String, ArrayList<ReligionEffect>> onKillEffect = new HashMap<>();

    private String name;

    private int duration = 0;

    private int LVL = 0;

    private int possibility = 0;


    public static ArrayList<ReligionEffect> getBannedFoodEffects(String religion, String itemName) {
        return bannedFood.get(religion).get(itemName);
    }

    public static ArrayList<ReligionEffect> getOnKillEffect(String religion) {
        return onKillEffect.get(religion);
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getLVL() {
        return LVL;
    }

    public int getPossibility() {
        return possibility;
    }

    public ReligionEffect(String name, int duration, int LVL, int possibility) {
        this.name = name;
        this.duration = duration;
        this.LVL = LVL;
        this.possibility = possibility;
    }

    public ReligionEffect(String name, int LVL, int possibility) {
        this.name = name;
        this.LVL = LVL;
        this.possibility = possibility;
    }

    public static void giveEffectToPlayer(Player player, ReligionEffect religionEffect) {
        Random random = new Random();
        int rand = random.nextInt(100);

        if (rand > religionEffect.possibility) return;

        if (religionEffect.name.equals("EXP")) {
            player.getWorld().createExplosion(player.getLocation(), religionEffect.LVL);
            player.setHealth(0);
            LoggerReligions.info(player.getName() + " exploded 'cause he ate ");
        } else {
            player.addPotionEffect(new PotionEffect(Objects.requireNonNull(
                    PotionEffectType.getByName(religionEffect.name)), religionEffect.duration, religionEffect.LVL));
        }

        LoggerReligions.info(religionEffect.name + " !!!!!!!!!!");
    }

    public static boolean isBannedFood(String name) {
        for (String religionName : Religions.religionNames) {
            if (bannedFood.get(religionName).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public static void initialiseBannedFood() {
        bannedFood.clear();
        for (String religionName : Religions.religionNames) {
            bannedFood.put(religionName, new HashMap<>());

            List<String> banned = AuroraReligions.getInstance().getConfig().getStringList("banned-food." + religionName);

            if (banned.size() > 0) {
                for (String food : banned) {
                    ArrayList<ReligionEffect> religionEffects = new ArrayList<>();
                    String[] string = food.split("!");
                    String itemName = string[0].toUpperCase();
                    String[] bidString = string[1].split("/");
                    for (String oneEffect : bidString) {
                        String[] oneEffectStrings = oneEffect.split(":");

                        String effectName = oneEffectStrings[0].toUpperCase();
                        ReligionEffect religionEffect;
                        if (effectName.equals("EXP")) {
                            int level = Integer.parseInt(oneEffectStrings[1]);
                            int possibility = Integer.parseInt(oneEffectStrings[2]);

                            religionEffect = new ReligionEffect(effectName, level, possibility);
                        } else {

                            int timeOfEffect = Integer.parseInt(oneEffectStrings[1]) * 20;
                            int levelOfEffect = Integer.parseInt(oneEffectStrings[2]);
                            int possibility = Integer.parseInt(oneEffectStrings[3]);

                            religionEffect = new ReligionEffect(effectName, timeOfEffect, levelOfEffect, possibility);
                        }

                        religionEffects.add(religionEffect);
                    }
                    bannedFood.get(religionName).put(itemName, religionEffects);
                }
            }
        }
    }

    public static void initialiseOnKillEffects() {
        onKillEffect.clear();
        for (String religionName : Religions.religionNames) {
            onKillEffect.put(religionName, new ArrayList<>());
            List<String> effects = AuroraReligions.getInstance().getConfig().getStringList("event.kill." + religionName);

            for (String effect: effects) {
                String[] values = effect.split(":");

                String effectName = values[0].toUpperCase();
                ReligionEffect religionEffect;
                if (effectName.equals("EXP")) {
                    int level = Integer.parseInt(values[1]);
                    int possibility = Integer.parseInt(values[2]);

                    religionEffect = new ReligionEffect(effectName, level, possibility);
                } else {

                    int timeOfEffect = Integer.parseInt(values[1]) * 20;
                    int levelOfEffect = Integer.parseInt(values[2]);
                    int possibility = Integer.parseInt(values[3]);

                    religionEffect = new ReligionEffect(effectName, timeOfEffect, levelOfEffect, possibility);
                }

                onKillEffect.get(religionName).add(religionEffect);

            }
        }
    }
}
