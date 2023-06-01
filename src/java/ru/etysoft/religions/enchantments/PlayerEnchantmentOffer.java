package ru.etysoft.religions.enchantments;

import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerEnchantmentOffer {

    private Player player;

    private Offer currentOffer = null;

    private ArrayList<Offer> offers = new ArrayList<>();

    private static HashMap<String, PlayerEnchantmentOffer> playerEnchantments = new HashMap<>();

    private PlayerEnchantmentOffer(Player player, String itemName , EnchantmentOffer offer) {
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
            PlayerEnchantmentOffer playerEnchantmentOffer = playerEnchantments.get(player.getName());

            playerEnchantmentOffer.getOffers().add(new Offer(itemName, offer));
        } else {
            new PlayerEnchantmentOffer(player, itemName, offer);
        }
    }

    public static void ClearPlayerOffers(String playerName) {
        playerEnchantments.remove(playerName);
    }

    public static PlayerEnchantmentOffer getPlayerEnchantmentOffers(String playerName) {
        return playerEnchantments.get(playerName);
    }
}
