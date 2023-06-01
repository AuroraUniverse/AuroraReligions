package ru.etysoft.religions.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import ru.etysoft.aurorauniverse.data.Residents;
import ru.etysoft.aurorauniverse.events.*;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.religions.GUIRelision.GUIReligions;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.enchantments.Offer;
import ru.etysoft.religions.enchantments.PlayerEnchantmentOffers;
import ru.etysoft.religions.logic.ReligionEffect;
import ru.etysoft.religions.logic.Religions;
import ru.etysoft.aurorauniverse.world.Resident;
import ru.etysoft.religions.logic.TownReligion;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class ReligionListener implements Listener {

    @EventHandler
    public void onGUITownOpenEvent(GUITownOpenEvent event) {
        GUIReligions.GUIChanger(event);
    }


    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Player killer = player.getKiller();
        if (killer == null) return;

        Resident resident = Residents.getResident(killer.getName());

        TownReligion townReligion = Religions.getTownReligion(resident.getTownName());

        if (townReligion == null) return;

        if (!townReligion.isStructureFullBuild()) return;

        ArrayList<ReligionEffect> effects = ReligionEffect.getOnKillEffect(townReligion.getReligion());

        if (effects.size() == 0) return;

        for (ReligionEffect effect: effects) {
            ReligionEffect.giveEffectToPlayer(killer, effect);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onEating(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        String itemName = item.getType().name();

        if (!ReligionEffect.isBannedFood(itemName)) return;

        Resident resident = Residents.getResident(player.getName());

        TownReligion townReligion = Religions.getTownReligion(resident.getTownName());

        if (townReligion == null) return;

        if (!townReligion.isStructureFullBuild()) return;

        String religion = townReligion.getReligion();

        ArrayList<ReligionEffect> effects = ReligionEffect.getBannedFoodEffects(religion, itemName);

        LoggerReligions.info(itemName + " hello from listener");

        if (effects.size() == 0) return;

        for (ReligionEffect effect: effects) {
            LoggerReligions.info(effect.getName() + " // " + effect.getLVL() + " // " + effect.getPossibility());
            ReligionEffect.giveEffectToPlayer(player, effect);
        }
        LoggerReligions.info(itemName + " hello from listener 2");
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onTownRename(TownRenameEvent event) {
        if (!Religions.getReligionsOfTowns().containsKey(event.getOldName())) return;

        TownReligion townReligion = Religions.getReligionsOfTowns().get(event.getOldName());
        Religions.getReligionsOfTowns().remove(event.getOldName());
        townReligion.setTownName(event.getNewName());

        Religions.getReligionsOfTowns().put(event.getNewName(), townReligion);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onTownDelete(TownDeleteEvent event) {
        if (!Religions.getReligionsOfTowns().containsKey(event.getDeletedTown().getName())) return;

        Religions.getReligionsOfTowns().remove(event.getDeletedTown().getName());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onSendTownInfoEvent(SendTownInfoEvent event) {
        try {
            TownReligion townReligion = Religions.getTownReligion(event.getTown().getName());

            if (townReligion == null) return;

            townReligion.updateReligionTax(event.getTown());

        } catch (Exception ignored) {

        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onPreTownGetTaxEvent(PreTownGetTaxEvent event) {
        Town town = event.getTown();

        TownReligion townReligion = Religions.getTownReligion(town.getName());

        if (townReligion != null) {
            townReligion.updateReligionTax(town);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {

        Player player = event.getEnchanter();
        PlayerEnchantmentOffers playerEnchantmentOffers =
                PlayerEnchantmentOffers.getPlayerEnchantmentOffers(player.getName());
        String itemName = event.getItem().getType().name();

        Resident resident = Residents.getResident(player.getName());
        TownReligion townReligion = Religions.getTownReligion(resident.getTownName());
        if (townReligion == null) return;

        Offer offer = null;
        boolean needRandom = true;

        // check if offer for this item is defined
        if (playerEnchantmentOffers != null) {

            for (Offer thisOffer: playerEnchantmentOffers.getOffers()) {
                if (thisOffer.getItemName().equals(itemName)) {
                    offer = thisOffer;
                    needRandom = false;
                    break;
                }
            }
        }

        // generate offer if it doesn't exist
        if (needRandom) {

            ArrayList<Offer> offers = PlayerEnchantmentOffers.getReligionEnchantmentOffers(itemName, townReligion.getReligion());

            if (offers.size() == 0) return;

            ArrayList<Integer> possibilities = new ArrayList<>();
            for (Offer thisOffer: offers) {
                possibilities.add(thisOffer.getPossibility());
            }

            Random random = new Random();
            int rand = random.nextInt(100);
            int sum = 0;

            for (int i=0; i < possibilities.size(); i++) {
                sum += possibilities.get(i);

                if (rand < sum) {
                    offer = offers.get(i);
                    break;
                }
            }

            if (offer == null) {
                PlayerEnchantmentOffers.AddPlayerOffer(player, itemName, null);
            } else {
                PlayerEnchantmentOffers.AddPlayerOffer(player, itemName, offer.getEnchantmentOffer());
            }

        }

        // place enchantmentOffer in gui
        if (offer != null) {
            if (offer.getEnchantmentOffer() != null) {
                int position;
                EnchantmentOffer[] enchantmentOffers = event.getOffers();

                try {
                    int lowest = enchantmentOffers[0].getCost();
                    int highest = enchantmentOffers[2].getCost();
                    int cost = offer.getEnchantmentOffer().getCost();

                    if (cost > highest | cost < lowest) return;

                    double diff = highest - lowest;
                    double relCost = cost - lowest;

                    double division = relCost / diff;
                    if (division < 0.33) { position = 0; }
                    else if (division < 0.66) { position = 1; }
                    else { position = 2; }
                } catch (Exception e) {
                    position = 0;
                }

                if (!townReligion.isStructureFullBuild()) return;

                event.getOffers()[position] = offer.getEnchantmentOffer();
            }
        }

        if (playerEnchantmentOffers == null) PlayerEnchantmentOffers.
                getPlayerEnchantmentOffers(player.getName()).setCurrentOffer(offer);
        else playerEnchantmentOffers.setCurrentOffer(offer);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onEnchantItemEvent(EnchantItemEvent event) {

        PlayerEnchantmentOffers playerEnchantmentOffers =
                PlayerEnchantmentOffers.getPlayerEnchantmentOffers(event.getEnchanter().getName());

        if (playerEnchantmentOffers == null) return;

        Offer currentOffer = playerEnchantmentOffers.getCurrentOffer();

        if (currentOffer != null) {
            if (currentOffer.getEnchantmentOffer() != null) {
                if (currentOffer.getEnchantmentOffer().getCost() == event.getExpLevelCost()) {
                    Map<Enchantment, Integer> map = event.getEnchantsToAdd();

                    map.clear();
                    map.put(currentOffer.getEnchantmentOffer().getEnchantment(),
                            currentOffer.getEnchantmentOffer().getEnchantmentLevel());
                }
            }
        }

        PlayerEnchantmentOffers.ClearPlayerOffers(event.getEnchanter().getName());
    }


}
