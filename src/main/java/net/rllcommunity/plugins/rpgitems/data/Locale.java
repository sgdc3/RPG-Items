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
package net.rllcommunity.plugins.rpgitems.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.rllcommunity.plugins.rpgitems.RpgItems;

public class Locale extends BukkitRunnable {
    
    private static Method getHandle;
    private static Method getLocale;
    private static Field language;
    private static boolean canLocale = true;
    private static boolean firstTime = true;
    
    private static HashMap<String, HashMap<String, String>> localeStrings = new HashMap<String, HashMap<String,String>>();
    
    private Locale(RpgItems plugin) {
        plugin.getDataFolder();
        reloadLocales(plugin);
    }
    
    public static Set<String> getLocales() {
        return localeStrings.keySet();
    }

    @Override
    public void run() {
        cancel();
    }
    
    public static void reloadLocales(RpgItems plugin) {
        localeStrings.clear();
        localeStrings.put("en_GB", loadLocaleStream(plugin.getResource("locale/en_GB.lang")));

        File localesFolder = new File(plugin.getDataFolder(), "locale/");
        localesFolder.mkdirs();
        
        for (File file : localesFolder.listFiles()) {
            if (!file.isDirectory() && file.getName().endsWith(".lang")) {

                FileInputStream in = null;
                try {
                    String locale = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    HashMap<String, String> map = localeStrings.get(locale);
                    map = map == null ? new HashMap<String, String>() : map;
                    in = new FileInputStream(file);
                    localeStrings.put(locale, loadLocaleStream(in, map));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            }
        }
    }
    
    private static HashMap<String, String> loadLocaleStream(InputStream in, HashMap<String, String> map) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;
            while((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] args = line.split("=");
                map.put(args[0].trim(), args[1].trim());
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static HashMap<String, String> loadLocaleStream(InputStream in) {
        return loadLocaleStream(in, new HashMap<String, String>());
    }
    
    public static String getPlayerLocale(Player player) {
        if (firstTime) {
            try {
                getHandle = player.getClass().getMethod("getHandle", (Class<?>[]) null);
                getLocale = getHandle.getReturnType().getMethod("getLocale", (Class<?>[]) null);
                language = getLocale.getReturnType().getDeclaredField("e");
                language.setAccessible(true);
                if (!language.getType().equals(String.class)) {
                    canLocale = false;
                }
            } catch (Exception e) {
                RpgItems.plugin.getLogger().warning("Failed to get player locale");
                canLocale = false;
            }
            firstTime = false;
        }
        if (!canLocale) {
            return "en_GB";
        }
        try {
            Object minePlayer = getHandle.invoke(player,(Object[]) null);
            Object locale = getLocale.invoke(minePlayer, (Object[]) null);
            return (String) language.get(locale);
        } catch (Exception e) {
            RpgItems.plugin.getLogger().warning("Failed to get player locale");
            canLocale = false;
        } 
        //Any error default to en_GB
        return "en_GB";
    }

    public static void init(RpgItems plugin) {
        (new Locale(plugin)).runTaskTimerAsynchronously(plugin, 0, 24l * 60l * 60l * 20l);
    }
    
    public static String get(String key, String locale) {
        if (!localeStrings.containsKey(locale))
            return get(key);
        HashMap<String, String> strings = localeStrings.get(locale);
        if (strings == null || !strings.containsKey(key))
            return get(key);
        return strings.get(key);
    }
    
    private static String get(String key) {
        HashMap<String, String> strings = localeStrings.get("en_GB");
        if (!strings.containsKey(key))
            return "!" + key + "!";
        return strings.get(key);
    }
}
