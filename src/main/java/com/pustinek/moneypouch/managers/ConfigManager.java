package com.pustinek.moneypouch.managers;

import com.pustinek.moneypouch.MoneyPouch;
import com.pustinek.moneypouch.PouchItem;
import com.pustinek.moneypouch.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigManager {
    private MoneyPouch plugin;
    private FileConfiguration config;

    private boolean debug = false;
    private boolean soundEnabled = false;
    private Sound soundOpen = Sound.BLOCK_CHEST_OPEN;
    private Sound soundReveal = Sound.BLOCK_ANVIL_BREAK;
    private Sound soundEnd = Sound.ENTITY_GENERIC_EXPLODE;
    private String pluginMessagePrefix = "&f[&2MoneyPouch&f]";


    public ConfigManager(MoneyPouch plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() {

        try {

            this.plugin.debug("reloading Configs...");

            //Create config file if it doesn't exist
            plugin.saveDefaultConfig();
            plugin.reloadConfig();

            config = plugin.getConfig();

            loadOptions();
            loadPouches();

        } catch (Exception e) {
            this.plugin.debug(e.getMessage());
        }
    }


    private void loadPouches() {
        if (config.contains("pouches")) {
            ConfigurationSection pouchesCS = config.getConfigurationSection("pouches");

            HashMap<String, PouchItem> moneyPouches = new HashMap<>();

            //Iterate over the money pouches saved in the config files
            for (String pouch : pouchesCS.getKeys(false)) {
                String name = pouchesCS.getString(pouch + ".name");
                String item = pouchesCS.getString(pouch + ".item");
                Integer priceRangeFrom = pouchesCS.getInt(pouch + ".pricerange.from");
                Integer priceRangeTo = pouchesCS.getInt(pouch + ".pricerange.to");
                ArrayList<String> loreList = new ArrayList<>(pouchesCS.getStringList(pouch + ".lore"));


                if (priceRangeFrom > priceRangeTo) {
                    plugin.debug("priceRange <from> cant be bigger than <to>");
                    continue;
                }

                Integer[] priceRange = new Integer[]{priceRangeFrom, priceRangeTo};
                Material material = Material.getMaterial(item);
                ItemStack itemStack = new ItemStack(material);

                if (itemStack == null) {
                    plugin.debug("Item [" + name + "] has a wrong material specified [" + item + "] !");
                    continue;
                }

                // Setting of item meta:
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ColorUtil.chatColor(name));
                itemMeta.setLore(ColorUtil.chatColor(loreList));
                itemStack.setItemMeta(itemMeta);


                // Create new pouch item, and save it to hash map
                PouchItem pouchItem = new PouchItem(pouch, name, itemStack, new Integer[]{priceRangeFrom, priceRangeTo});
                moneyPouches.put(name, pouchItem);
                plugin.debug("Successfully loaded item [" + pouchItem.getInternalName() + "]");
            }
            //Set the money pouches in the main class
            plugin.setMoneyPouches(moneyPouches);


        } else {
            //There is no pouches section in configs (mayor error)
        }
    }

    private void loadOptions() {
        if (config.contains("options")) {
            ConfigurationSection optionsCS = config.getConfigurationSection("options");
            this.debug = optionsCS.getBoolean("debug");
            this.soundEnabled = optionsCS.getBoolean("sound.enabled");

            try {
                this.soundOpen = Sound.valueOf(optionsCS.getString("sound.opensound"));
                this.soundReveal = Sound.valueOf(optionsCS.getString("sound.revealsound"));
                this.soundEnd = Sound.valueOf(optionsCS.getString("sound.revealsound"));
            } catch (IllegalArgumentException ex) {
                plugin.debug("error occurred reading sound in config !!");
                plugin.debug(ex.getMessage());
            }

            this.pluginMessagePrefix = optionsCS.getString("messagePrefix") != null ? pluginMessagePrefix : optionsCS.getString("messagePrefix");

        }


    }


    public boolean isDebug() {
        return debug;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public Sound getSoundOpen() {
        return soundOpen;
    }

    public Sound getSoundReveal() {
        return soundReveal;
    }

    public Sound getSoundEnd() {
        return soundEnd;
    }

    public String getPluginMessagePrefix() {
        return pluginMessagePrefix;
    }
}
