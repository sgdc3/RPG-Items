/*
 *  This file is part of RPG Items.
 *
 *  RPG Items is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RPG Items is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RPG Items.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.rllcommunity.plugins.rpgitems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.rllcommunity.plugins.rpgitems.commands.Commands;
import net.rllcommunity.plugins.rpgitems.config.ConfigUpdater;
import net.rllcommunity.plugins.rpgitems.data.Font;
import net.rllcommunity.plugins.rpgitems.data.Locale;
import net.rllcommunity.plugins.rpgitems.item.ItemManager;
import net.rllcommunity.plugins.rpgitems.power.Power;
import net.rllcommunity.plugins.rpgitems.power.PowerArrow;
import net.rllcommunity.plugins.rpgitems.power.PowerCommand;
import net.rllcommunity.plugins.rpgitems.power.PowerConsume;
import net.rllcommunity.plugins.rpgitems.power.PowerFireball;
import net.rllcommunity.plugins.rpgitems.power.PowerFlame;
import net.rllcommunity.plugins.rpgitems.power.PowerIce;
import net.rllcommunity.plugins.rpgitems.power.PowerKnockup;
import net.rllcommunity.plugins.rpgitems.power.PowerLightning;
import net.rllcommunity.plugins.rpgitems.power.PowerPotionHit;
import net.rllcommunity.plugins.rpgitems.power.PowerPotionSelf;
import net.rllcommunity.plugins.rpgitems.power.PowerPotionTick;
import net.rllcommunity.plugins.rpgitems.power.PowerRainbow;
import net.rllcommunity.plugins.rpgitems.power.PowerRumble;
import net.rllcommunity.plugins.rpgitems.power.PowerRush;
import net.rllcommunity.plugins.rpgitems.power.PowerSkyHook;
import net.rllcommunity.plugins.rpgitems.power.PowerTNTCannon;
import net.rllcommunity.plugins.rpgitems.power.PowerTeleport;
import net.rllcommunity.plugins.rpgitems.power.PowerTicker;
import net.rllcommunity.plugins.rpgitems.power.PowerUnbreakable;
import net.rllcommunity.plugins.rpgitems.power.PowerUnbreaking;
import net.rllcommunity.plugins.rpgitems.support.WorldGuard;

@SuppressWarnings("deprecation")
public class RpgItems extends JavaPlugin {

    public static Logger logger = Logger.getLogger("RPGItemsReloaded");

    public static RpgItems plugin;

    @Override
    public void onLoad() {
        plugin = this;
        reloadConfig();
        Font.load();
        Power.powers.put("arrow", PowerArrow.class);
        Power.powers.put("tntcannon", PowerTNTCannon.class);
        Power.powers.put("rainbow", PowerRainbow.class);
        Power.powers.put("flame", PowerFlame.class);
        Power.powers.put("lightning", PowerLightning.class);
        Power.powers.put("ice", PowerIce.class);
        Power.powers.put("command", PowerCommand.class);
        Power.powers.put("potionhit", PowerPotionHit.class);
        Power.powers.put("teleport", PowerTeleport.class);
        Power.powers.put("fireball", PowerFireball.class);
        Power.powers.put("knockup", PowerKnockup.class);
        Power.powers.put("rush", PowerRush.class);
        Power.powers.put("potionself", PowerPotionSelf.class);
        Power.powers.put("consume", PowerConsume.class);
        Power.powers.put("unbreakable", PowerUnbreakable.class);
        Power.powers.put("unbreaking", PowerUnbreaking.class);
        Power.powers.put("rumble", PowerRumble.class);
        Power.powers.put("skyhook", PowerSkyHook.class);
        Power.powers.put("potiontick", PowerPotionTick.class);
    }

    @Override
    public void onEnable() {
        Locale.init(this);
        updateConfig();
        WorldGuard.init(this);
        ConfigurationSection conf = getConfig();
        if (conf.getBoolean("localeInv", false)) {
            Events.useLocaleInv = true;
        }
        getServer().getPluginManager().registerEvents(new Events(), this);
        ItemManager.load(this);
        Commands.register(new Handler());
        Commands.register(new PowerHandler());
        new PowerTicker().runTaskTimer(this, 0, 1);
    }

    @Override
    public void saveConfig() {
        FileConfiguration config = getConfig();
        FileOutputStream out = null;
        try {
            File f = new File(getDataFolder(), "config.yml");
            if (!f.exists())
                f.createNewFile();
            out = new FileOutputStream(f);
            out.write(config.saveToString().getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private FileConfiguration config;

    @Override
    public void reloadConfig() {
        FileInputStream in = null;
        config = new YamlConfiguration();
        try {
            File f = new File(getDataFolder(), "config.yml");
            in = new FileInputStream(f);
            byte[] data = new byte[(int) f.length()];
            in.read(data);
            String str = new String(data, "UTF-8");
            config.loadFromString(str);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (InvalidConfigurationException e) {
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public void updateConfig() {
        ConfigUpdater.updateConfig(getConfig());
        saveConfig();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        StringBuilder out = new StringBuilder();
        out.append(label).append(' ');
        for (String arg : args)
            out.append(arg).append(' ');
        Commands.exec(sender, out.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        StringBuilder out = new StringBuilder();
        out.append(alias).append(' ');
        for (String arg : args)
            out.append(arg).append(' ');
        return Commands.complete(sender, out.toString());
    }
}
