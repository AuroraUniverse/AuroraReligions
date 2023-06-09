package ru.etysoft.religions.GUIRelision;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.etysoft.aurorauniverse.events.GUITownOpenEvent;
import ru.etysoft.aurorauniverse.exceptions.WorldNotFoundedException;
import ru.etysoft.aurorauniverse.structures.StructureBuildException;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.exceptions.StructureException;
import ru.etysoft.religions.logic.Religions;
import ru.etysoft.religions.logic.TownReligion;
import ru.etysoft.religions.utils.ReligionsLanguage;
import ru.etysoft.aurorauniverse.utils.ColorCodes;
import ru.etysoft.aurorauniverse.utils.Messaging;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.epcore.gui.GUITable;
import ru.etysoft.epcore.gui.Items;
import ru.etysoft.epcore.gui.Slot;
import ru.etysoft.epcore.gui.SlotRunnable;

import java.util.HashMap;

import static ru.etysoft.religions.logic.ReligionStructures.createReligionStructure;

public class GUIReligions {

    public static void GUIChanger(GUITownOpenEvent event) {

        Player player = event.getPlayer();
        CommandSender sender = event.getSender();
        Town town = event.getTown();

        TownReligion townReligion = Religions.getTownReligion(town.getName());

        if (townReligion != null) {
            ItemStack stack;
            if (townReligion.isStructureFullBuild()) {
                stack = Items.createNamedItem(new ItemStack(
                        Material.getMaterial(AuroraReligions.getInstance().getConfig().getString("materials.main")), 1),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("religion")),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("gui.town-religion").replace
                                ("%s", ReligionsLanguage.getColorString(townReligion.getReligion()))),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore")));
            } else {
                stack = Items.createNamedItem(new ItemStack(
                        Material.getMaterial(AuroraReligions.getInstance().getConfig().getString("materials.main")), 1),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("religion")),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("gui.rebuild-temple")),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("gui.town-religion").replace
                                ("%s", ReligionsLanguage.getColorString(townReligion.getReligion()))),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore")));
            }

            Slot.SlotListener listener = new Slot.SlotListener() {
                @Override
                public void onRightClicked(Player player, GUITable guiTable) {
                    if (sender.hasPermission("town.toggle.*")) {
                        try {
                            townReligion.getStructure().destroy(new Runnable() {
                                @Override
                                public void run() {
                                    Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("structure-destroy-finished"), player);
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                    LoggerReligions.error("Error destroying structure!");
                                }
                            }, true);
                        } catch (WorldNotFoundedException e) {
                            throw new RuntimeException(e);
                        } catch (StructureBuildException e) {
                            throw new RuntimeException(e);
                        }
                        Religions.deleteTownReligion(town);
                        ReligionsLanguage.sendTownMessage(town, ReligionsLanguage.getColorString("religion-deleted"));
                        player.closeInventory();

                        LoggerReligions.info(player.getName() + " deleted religion of " + town.getName());
                    } else {
                        Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("wrong-access"), player);
                    }
                }

                @Override
                public void onLeftClicked(Player player, GUITable guiTable) {

                }

                @Override
                public void onShiftClicked(Player player, GUITable guiTable) {

                }
            };

            Slot slot = new Slot(listener, stack);

            event.getMatrix().put(40, slot);


        } else {
            ItemStack stack = Items.createNamedItem(new ItemStack(
                    Material.getMaterial(AuroraReligions.getInstance().getConfig().getString("materials.main")), 1),
                    ColorCodes.toColor(ReligionsLanguage.getColorString("religion")),
                    ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore-buy")));

            Slot.SlotListener listener = new Slot.SlotListener() {
                @Override
                public void onRightClicked(Player player, GUITable guiTable) {

                }

                @Override
                public void onLeftClicked(Player player, GUITable guiTable) {
                    if (sender.hasPermission("town.toggle.*")) {
                        GUIReligionBuy(player, sender, town);
                    } else {
                        Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("wrong-access"), player);
                    }
                }

                @Override
                public void onShiftClicked(Player player, GUITable guiTable) {

                }
            };

            Slot slot = new Slot(listener, stack);

            event.getMatrix().put(40, slot);
        }
    }

    private static void GUIReligionBuy(Player player, CommandSender sender, Town town) {

        HashMap<Integer, Slot> matrix = new HashMap<>();

        int i = 1;

        for (String religionName: Religions.religionNames) {
            ItemStack thisReligion = Items.createNamedItem(new ItemStack(
                    Material.getMaterial(AuroraReligions.getInstance().getConfig().getString("materials." + religionName + "-main")), 1),
                    ColorCodes.toColor(ReligionsLanguage.getColorString(religionName)),
                    ColorCodes.toColor(ReligionsLanguage.getColorString(religionName + "-lore")),
                    ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore-price")
                            .replace("%s", String.valueOf(AuroraReligions.getInstance().getConfig().getDouble( religionName + "-price")))));

            Slot chrSlot = new Slot(new SlotRunnable() {
                @Override
                public void run() {
                    GUISelect(player, sender, town, religionName);
                }
            }, thisReligion);

            matrix.put(i, chrSlot);
            i++;
        }

        try {
            GUITable guiTable = new GUITable(ReligionsLanguage.getColorString("gui.title"), 1, matrix,
                    AuroraReligions.getInstance(), Material.AIR, true);
            guiTable.open(player);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error(player.getName() + " can't open GUIReligionBuy");
        }

    }

    private static void GUISelect(Player player, CommandSender sender, Town town, String religion) {
        HashMap<Integer, Slot> matrix = new HashMap<>();

        for (int i = 1; i < 10; i++) {
            String newPath = AuroraReligions.getLanguage() + ".gui." + i + "-" + religion;
            if (!AuroraReligions.getInstance().getConfig().isString(newPath)) break;

            ItemStack text = Items.createNamedItem(new ItemStack(
                    Material.getMaterial(AuroraReligions.getInstance().getConfig().getString("materials." + religion + "-" + i)), 1),
                    ColorCodes.toColor(ReligionsLanguage.getColorString("gui." + i + "-" + religion)));

            int finalI = i;
            Slot slot = new Slot(new SlotRunnable() {
                @Override
                public void run() {
                    try {
                        createReligionStructure(religion, "religion-structure-" + religion + "-" + finalI, player, town,
                                AuroraReligions.getInstance().getConfig().getDouble(religion + "-price"));
                    } catch (StructureException e) {
                        Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString(e.getCode()), player);
                    } catch (WorldNotFoundedException e) {
                        LoggerReligions.error("Structure create. WorldNotFoundedException");
                    }
                }
            }, text);

            matrix.put(i, slot);
        }

        try {
            GUITable guiTable = new GUITable(ReligionsLanguage.getColorString("gui.title"), 1, matrix,
                    AuroraReligions.getInstance(), Material.AIR, true);
            guiTable.open(player);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error(player.getName() + " can't open GUIReligionSelect");
        }
    }
}
