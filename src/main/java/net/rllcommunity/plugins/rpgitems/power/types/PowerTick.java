package net.rllcommunity.plugins.rpgitems.power.types;

import org.bukkit.entity.Player;

public interface PowerTick extends Power {
    public void tick(Player player);
}
