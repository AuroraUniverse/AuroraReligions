package ru.etysoft.religions.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.etysoft.aurorauniverse.world.Town;
import ru.etysoft.religions.logic.TownReligion;

public class TempleRebuiltEvent extends Event {
    private Town town;

    private TownReligion townReligion;

    public TempleRebuiltEvent(Town town, TownReligion townReligion) {
        this.town = town;
        this.townReligion = townReligion;
    }

    public Town getTown() {
        return town;
    }

    public TownReligion getTownReligion() {
        return townReligion;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
