package ru.etysoft.religions.enchantments;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.logic.Religions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerEnchantmentOffers {

    private Player player;

    private Offer currentOffer = null;

    private static HashMap<String, ArrayList<Offer>> enchantmentOffers = new HashMap<>();

    private ArrayList<Offer> offers = new ArrayList<>();

    private static HashMap<String, PlayerEnchantmentOffers> playerEnchantments = new HashMap<>();

    private PlayerEnchantmentOffers(Player player, String itemName , EnchantmentOffer offer) {
        this.player = player;
        this.offers.add(new Offer(itemName, offer));

        playerEnchantments.put(player.getName(), this);
    }

    public Player getPlayer() {
        return player;
    }

    public Offer getCurrentOffer() {
        return currentOffer;
    }

    public void setCurrentOffer(Offer currentOffer) {
        this.currentOffer = currentOffer;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public static void AddPlayerOffer(Player player, String itemName, EnchantmentOffer offer) {
        if (playerEnchantments.containsKey(player.getName())) {
            PlayerEnchantmentOffers playerEnchantmentOffers = playerEnchantments.get(player.getName());

            playerEnchantmentOffers.getOffers().add(new Offer(itemName, offer));
        } else {
            new PlayerEnchantmentOffers(player, itemName, offer);
        }
    }

    public static void ClearPlayerOffers(String playerName) {
        playerEnchantments.remove(playerName);
    }

    public static PlayerEnchantmentOffers getPlayerEnchantmentOffers(String playerName) {
        return playerEnchantments.get(playerName);
    }

    public static ArrayList<Offer> getReligionEnchantmentOffers(String itemName, String religionName) {
        ArrayList<Offer> result = new ArrayList<>();

        for (Offer offer : enchantmentOffers.get(religionName)) {
            if (offer.getItemName().equalsIgnoreCase(itemName)) {
                result.add(offer);
            }
        }

        return result;
    }

    public static void initialiseEnchantments() {
        enchantmentOffers.clear();
        for (String religionName : Religions.religionNames) {

            enchantmentOffers.put(religionName, new ArrayList<>());
            List<String> offers = AuroraReligions.getInstance().getConfig().getStringList("enchantments." + religionName);

            if (offers.size() > 0) {
                for (String string : offers) {
                    String[] strings = string.split("!");
                    String[] enchantmentString = strings[1].split(":");
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentString[0].toLowerCase()));
                    if (enchantment != null) {
                        EnchantmentOffer enchantmentOffer = new EnchantmentOffer(enchantment,
                                Integer.parseInt(enchantmentString[1]), Integer.parseInt(enchantmentString[2]));
                        enchantmentOffers.get(religionName).add(new Offer(strings[0].toUpperCase(), enchantmentOffer, Integer.parseInt(enchantmentString[3])));
                    } else {
                        LoggerReligions.error("Can't initialise this enchantment: " + string + " in enchantments." + religionName);
                    }
                }
            }

        }
    }

}
