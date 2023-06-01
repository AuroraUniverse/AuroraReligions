package ru.etysoft.religions.enchantments;

import org.bukkit.enchantments.EnchantmentOffer;

public class Offer {

    private String itemName;

    private EnchantmentOffer enchantmentOffer;

    private int possibility;

    public Offer(String itemName, EnchantmentOffer enchantmentOffer) {
        this.itemName = itemName;
        this.enchantmentOffer = enchantmentOffer;
    }

    public Offer(String itemName, EnchantmentOffer enchantmentOffer, int possibility) {
        this.itemName = itemName;
        this.enchantmentOffer = enchantmentOffer;
        this.possibility = possibility;
    }

    public int getPossibility() {
        return possibility;
    }

    public String getItemName() {
        return itemName;
    }

    public EnchantmentOffer getEnchantmentOffer() {
        return enchantmentOffer;
    }
}
