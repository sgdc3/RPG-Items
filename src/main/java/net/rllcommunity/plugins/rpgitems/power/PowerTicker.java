package net.rllcommunity.plugins.rpgitems.power;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.rllcommunity.plugins.rpgitems.item.ItemManager;
import net.rllcommunity.plugins.rpgitems.item.RPGItem;

public class PowerTicker extends BukkitRunnable {

    @Override
    public void run() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            ItemStack[] armour = player.getInventory().getArmorContents();
            for (ItemStack part : armour) {
                RPGItem item = ItemManager.toRPGItem(part);
                if (item == null)
                    continue;
                item.tick(player);
            }
            ItemStack part = player.getItemInHand();
            RPGItem item = ItemManager.toRPGItem(part);
            if (item == null)
                continue;
            item.tick(player);
        }
    }

}
