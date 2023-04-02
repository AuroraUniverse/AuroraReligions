package ru.etysoft.religions.GUIRelision;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.etysoft.aurorauniverse.events.GUITownOpenEvent;
import ru.etysoft.aurorauniverse.exceptions.WorldNotFoundedException;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.commands.ReligionChangeCommand;
import ru.etysoft.religions.exceptions.StructureException;
import ru.etysoft.religions.logic.ReligionStructures;
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

        TownReligion townReligion = Religions.getTownReligionFromHashMap(town.getName());

        if (townReligion != null) {
            ItemStack stack;
            if (townReligion.isStructureFullBuilt(town)) {
                stack = Items.createNamedItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("religion")),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("gui.town-religion").replace
                                ("%s", ReligionsLanguage.getColorString(townReligion.getReligion()))),
                        ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore")));
            } else {
                stack = Items.createNamedItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1),
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
                        Religions.deleteTownReligion(town);
                        town.sendMessage(ReligionsLanguage.getColorString("religion-deleted"));
                        player.closeInventory();
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
            ItemStack stack = Items.createNamedItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1),
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

        ItemStack chr = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("chr")),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore-price")
                        .replace("%s", String.valueOf(AuroraReligions.getInstance().getConfig().getDouble("chr-price")))));

        ItemStack mus = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("mus")),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore-price")
                        .replace("%s", String.valueOf(AuroraReligions.getInstance().getConfig().getDouble("mus-price")))));

        ItemStack bud = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("bud")),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.lore-price")
                        .replace("%s", String.valueOf(AuroraReligions.getInstance().getConfig().getDouble("bud-price")))));

        Slot chrSlot = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                GUIchrSelect(player, sender, town);
            }
        }, chr);

        Slot musSlot = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                GUImusSelect(player, sender, town);
            }
        }, mus);

        Slot budSlot = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                GUIbudSelect(player, sender, town);
            }
        }, bud);

        HashMap<Integer, Slot> matrix = new HashMap<>();

        matrix.put(1, chrSlot);
        matrix.put(5, musSlot);
        matrix.put(9, budSlot);

        try {
            GUITable guiTable = new GUITable(ReligionsLanguage.getColorString("gui.title"), 1, matrix,
                    AuroraReligions.getInstance(), Material.AIR, true);
            guiTable.open(player);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error(player.getName() + " can't open GUIReligionBuy");
        }

    }

    private static void GUIchrSelect(Player player, CommandSender sender, Town town) {
        ItemStack chr1 = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.first-chr")));
        ItemStack chr2 = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.second-chr")));

        Slot slot1 = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                try {
                    createReligionStructure("chr", ReligionStructures.Types.CHR_1, player, town,
                            AuroraReligions.getInstance().getConfig().getDouble("chr-price"));
                } catch (StructureException e) {
                    Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString(e.getCode()), player);
                } catch (WorldNotFoundedException e) {
                    LoggerReligions.error("Structure create. WorldNotFoundedException");
                }
            }
        }, chr1);

        Slot slot2 = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                try {
                    createReligionStructure("chr", ReligionStructures.Types.CHR_2, player, town,
                            AuroraReligions.getInstance().getConfig().getDouble("chr-price"));
                } catch (StructureException e) {
                    Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString(e.getCode()), player);
                } catch (WorldNotFoundedException e) {
                    LoggerReligions.error("Structure create. WorldNotFoundedException");
                }
            }
        }, chr2);

        HashMap<Integer, Slot> matrix = new HashMap<>();

        matrix.put(1, slot1);
        matrix.put(2, slot2);

        try {
            GUITable guiTable = new GUITable(ReligionsLanguage.getColorString("gui.title"), 1, matrix,
                    AuroraReligions.getInstance(), Material.AIR, true);
            guiTable.open(player);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error(player.getName() + " can't open GUIChrSelect");
        }

    }

    private static void GUImusSelect(Player player, CommandSender sender, Town town) {
        ItemStack mus1 = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.first-mus")));
        ItemStack mus2 = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.second-mus")));

        Slot slot1 = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                try {
                    createReligionStructure("mus", ReligionStructures.Types.MUS_1, player, town,
                            AuroraReligions.getInstance().getConfig().getDouble("mus-price"));
                } catch (StructureException e) {
                    Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString(e.getCode()), player);
                } catch (WorldNotFoundedException e) {
                    LoggerReligions.error("Structure create. WorldNotFoundedException");
                }
            }
        }, mus1);

        Slot slot2 = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                try {
                    createReligionStructure("mus", ReligionStructures.Types.MUS_2, player, town,
                            AuroraReligions.getInstance().getConfig().getDouble("mus-price"));
                } catch (StructureException e) {
                    Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString(e.getCode()), player);
                } catch (WorldNotFoundedException e) {
                    LoggerReligions.error("Structure create. WorldNotFoundedException");
                }
            }
        }, mus2);

        HashMap<Integer, Slot> matrix = new HashMap<>();

        matrix.put(1, slot1);
        matrix.put(2, slot2);

        try {
            GUITable guiTable = new GUITable(ReligionsLanguage.getColorString("gui.title"), 1, matrix,
                    AuroraReligions.getInstance(), Material.AIR, true);
            guiTable.open(player);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error(player.getName() + " can't open GUIMusSelect");
        }
    }

    private static void GUIbudSelect(Player player, CommandSender sender, Town town) {
        ItemStack bud1 = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.first-bud")));
        ItemStack bud2 = Items.createNamedItem(new ItemStack(Material.COBBLESTONE, 1),
                ColorCodes.toColor(ReligionsLanguage.getColorString("gui.second-bud")));

        Slot slot1 = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                try {
                    createReligionStructure("bud", ReligionStructures.Types.BUD_1, player, town,
                            AuroraReligions.getInstance().getConfig().getDouble("bud-price"));
                } catch (StructureException e) {
                    Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString(e.getCode()), player);
                } catch (WorldNotFoundedException e) {
                    LoggerReligions.error("Structure create. WorldNotFoundedException");
                }
            }
        }, bud1);

        Slot slot2 = new Slot(new SlotRunnable() {
            @Override
            public void run() {
                try {
                    createReligionStructure("bud", ReligionStructures.Types.BUD_2, player, town,
                            AuroraReligions.getInstance().getConfig().getDouble("bud-price"));
                } catch (StructureException e) {
                    Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString(e.getCode()), player);
                } catch (WorldNotFoundedException e) {
                    LoggerReligions.error("Structure create. WorldNotFoundedException");
                }
            }
        }, bud2);

        HashMap<Integer, Slot> matrix = new HashMap<>();

        matrix.put(1, slot1);
        matrix.put(2, slot2);

        try {
            GUITable guiTable = new GUITable(ReligionsLanguage.getColorString("gui.title"), 1, matrix,
                    AuroraReligions.getInstance(), Material.AIR, true);
            guiTable.open(player);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerReligions.error(player.getName() + " can't open GUIBudSelect");
        }
    }
}
