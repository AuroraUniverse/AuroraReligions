package ru.etysoft.religions.logic;

import ru.etysoft.aurorauniverse.exceptions.WorldNotFoundedException;
import ru.etysoft.aurorauniverse.structures.Structure;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.religions.AuroraReligions;


public class TownReligion {

    private String townName;

    private String religion;

    private Structure structure;

    public TownReligion(String townName, String religion, Structure structure) {
        this.townName = townName;
        this. religion = religion;
        this.structure = structure;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public boolean isStructureFullBuilt(Town town) {
        try {
            if (!structure.isFullBuilt()) {
                town.setTownChunkTaxMultiplier(AuroraReligions.getInstance().getDescription().getName(), 1);
                return false;
            } else {
                Religions.setMultipliers(town, religion);
                return true;
            }
        } catch (WorldNotFoundedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
