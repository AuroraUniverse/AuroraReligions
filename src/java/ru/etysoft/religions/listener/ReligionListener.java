package ru.etysoft.religions.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import ru.etysoft.aurorauniverse.data.Residents;
import ru.etysoft.aurorauniverse.events.*;
import ru.etysoft.aurorauniverse.exceptions.WorldNotFoundedException;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.religions.GUIRelision.GUIReligions;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.logic.ReligionStructures;
import ru.etysoft.religions.logic.Religions;
import ru.etysoft.aurorauniverse.world.Resident;
import ru.etysoft.religions.logic.TownReligion;

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

        TownReligion townReligion = Religions.getTownReligionFromHashMap(resident.getTownName());

        if (townReligion == null) return;

        try {
            if (!townReligion.getStructure().isFullBuilt()) return;
        } catch (WorldNotFoundedException e) {
            e.printStackTrace();
        }

        String string = AuroraReligions.getInstance().getConfig().getString("event.kill." + townReligion.getReligion());

        if (string == null | string.equals("")) return;

        Religions.giveEffectOnPlayer(killer, string);
    }

//    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
//    public void onEating(PlayerInteractEvent event) {
//
//        Action action = event.getAction();
//        if (!action.equals(Action.RIGHT_CLICK_AIR)) return;
//
//        Player player = event.getPlayer();
//
//        ItemStack item = event.getItem();
//
//        if (item == null) return;
//        String itemName = item.getType().name();
//
//        LoggerReligions.info(itemName);
//        LoggerReligions.info(Religions.getBannedFood("mus").toString());
//
//        if (!Religions.getBannedFood("chr").containsKey(itemName) &
//                !Religions.getBannedFood("mus").containsKey(itemName) &
//                !Religions.getBannedFood("bud").containsKey(itemName)) return;
//
//        LoggerReligions.info("1");
//
//        Resident resident = Residents.getResident(player.getName());
//
//        TownReligion townReligion = Religions.getTownReligionFromHashMap(resident.getTownName());
//
//        if (townReligion == null) return;
//
//        try {
//            if (!townReligion.getStructure().isFullBuilt()) return;
//        } catch (WorldNotFoundedException e) {
//            e.printStackTrace();
//        }
//
//        String religion = townReligion.getReligion();
//
//        String effectName = Religions.getBannedFood(religion).get(itemName);
//
//        Religions.giveEffectOnPlayer(player, effectName);
//        LoggerReligions.info("oooooooooooo");
//
////        if (effectName.contains("exp")) {
////            int force = Integer.parseInt(effectName.replace("exp", ""));
////            player.getWorld().createExplosion(player.getLocation(), force);
////            player.setHealth(0);
////            LoggerReligions.info(player.getName() + " exploded 'cause he ate " + itemName);
////        }
////
////        if (effectName.contains(":")){
////            Religions.giveEffectOnPlayer(player, effectName);
////        }
//
//    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onEating(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        String itemName = item.getType().name();

        if (!Religions.isBannedFood(itemName)) return;

        Resident resident = Residents.getResident(player.getName());

        TownReligion townReligion = Religions.getTownReligionFromHashMap(resident.getTownName());

        if (townReligion == null) return;

        try {
            if (!townReligion.getStructure().isFullBuilt()) return;
        } catch (WorldNotFoundedException e) {
            e.printStackTrace();
        }

        String religion = townReligion.getReligion();

        String effectName = Religions.getBannedFood(religion).get(itemName);

        if (effectName == null) return;

        Religions.giveEffectOnPlayer(player, effectName);
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
            TownReligion townReligion = Religions.getTownReligionFromHashMap(event.getTown().getName());

            if (townReligion == null) return;

            townReligion.isStructureFullBuilt(event.getTown());

        } catch (Exception e) {

        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
    public void onPreTownGetTaxEvent(PreTownGetTaxEvent event) {
        Town town = event.getTown();

        TownReligion townReligion = Religions.getTownReligionFromHashMap(town.getName());

        if (townReligion != null) {
            townReligion.isStructureFullBuilt(town);
        }
    }

//    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOW)
//    public void onEat()


}
