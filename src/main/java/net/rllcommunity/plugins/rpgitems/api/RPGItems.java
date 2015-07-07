package net.rllcommunity.plugins.rpgitems.api;

import org.bukkit.inventory.ItemStack;

import net.rllcommunity.plugins.rpgitems.item.ItemManager;
import net.rllcommunity.plugins.rpgitems.item.RPGItem;

public class RPGItems {

    /**
     * If the itemstack is a RPGItem this will return the RPGItem version of the item.
     * If the itemstack isn't a RPGItem this will return null.
     * 
     * @param itemstack The item to converted
     * @return The RPGItem or null
     */
    public RPGItem toRPGItem(ItemStack itemstack) {
        return ItemManager.toRPGItem(itemstack);
    }
}
