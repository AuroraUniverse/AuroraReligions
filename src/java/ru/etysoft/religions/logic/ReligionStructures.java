package ru.etysoft.religions.logic;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.etysoft.aurorauniverse.data.Residents;
import ru.etysoft.aurorauniverse.exceptions.WorldNotFoundedException;
import ru.etysoft.aurorauniverse.structures.Structure;
import ru.etysoft.aurorauniverse.structures.StructureBuildException;
import ru.etysoft.aurorauniverse.utils.Messaging;
import ru.etysoft.aurorauniverse.world.Resident;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.religions.AuroraReligions;
import ru.etysoft.religions.LoggerReligions;
import ru.etysoft.religions.exceptions.StructureException;
import ru.etysoft.religions.utils.ReligionsLanguage;

public class ReligionStructures {

    public static void createReligionStructure(String religion, String nameOfStructure, Player player, Town town, double price)
            throws WorldNotFoundedException, StructureException {
        if (Religions.getReligionsOfTowns().containsKey(town.getName())) throw  new StructureException("structure-error-religion-exists");

        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        Structure structure = new Structure(chunk.getX() * 16, location.getBlockY(), chunk.getZ() * 16,
                nameOfStructure, location.getWorld().getName());

        Resident resident = Residents.getResident(player.getName());

        if (!structure.isInSingleChunk()) throw new StructureException("structure-error-chunk");
        if (!structure.isSpaceClear()) throw new StructureException("structure-error-place");
        if (!town.hasChunk(location)) throw new StructureException("structure-error-chunk");

        if (town.withdrawBank(price)) {
            if (AuroraReligions.getInstance().isStructureEnabled()) {
                try {
                    structure.build(new Runnable() {
                        @Override
                        public void run() {
                            Religions.createTownReligion(town, religion, structure);
                            town.sendMessage(ReligionsLanguage.getColorString("new-" + religion + "-town"));
                            player.closeInventory();


                        }
                    });
                } catch (StructureBuildException e) {
                    LoggerReligions.error("Structure haven't created");
                }
            } else {
                Religions.createTownReligion(town, religion, structure);
                town.sendMessage(ReligionsLanguage.getColorString("new-" + religion + "-town"));
                player.closeInventory();
            }
        } else {
            Messaging.sendPrefixedMessage(ReligionsLanguage.getColorString("not-enough-money"), player);
        }
    }

    public static class Types {
        public static final String CHR_1 = "religion-structure-chr-1";
        public static final String CHR_2 = "religion-structure-chr-2";

        public static final String MUS_1 = "religion-structure-mus-1";
        public static final String MUS_2 = "religion-structure-mus-2";

        public static final String BUD_1 = "religion-structure-bud-1";
        public static final String BUD_2 = "religion-structure-bud-2";

    }

}
