package com.pustinek.moneypouch;

import com.pustinek.moneypouch.listeners.OnRightClick;
import com.pustinek.moneypouch.managers.CommandManager;
import com.pustinek.moneypouch.managers.ConfigManager;
import com.pustinek.moneypouch.utils.ColorUtil;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class MoneyPouch extends JavaPlugin {


    private static JavaPlugin plugin;
    private static Logger logger;
    //Vault
    private static Economy econ = null;
    //Managers
    private CommandManager commandManager;
    private static ConfigManager configManager;
    private HashMap<String, PouchItem> moneyPouches = new HashMap<>();

    public static void messageNoPrefix(CommandSender sender, String message) {
        sender.sendMessage(ColorUtil.chatColor(message));
    }

    public static void message(CommandSender sender, String message) {
        sender.sendMessage(ColorUtil.chatColor(configManager.getPluginMessagePrefix()) + ColorUtil.chatColor(message));
    }

    /**
     * Print an error to the console.
     *
     * @param message The message to print
     */
    public static void error(Object... message) {
        MoneyPouch.plugin.getLogger().severe(StringUtils.join(message, " "));
    }

    public static Economy getEconomy() {
        return econ;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public HashMap<String, PouchItem> getMoneyPouches() {
        return this.moneyPouches;
    }

    public void setMoneyPouches(HashMap<String, PouchItem> newMoneyPouches) {
        this.moneyPouches = newMoneyPouches;
    }

    @Override
    public void onEnable() {

        // Plugin startup logic
        logger = getLogger();
        plugin = this;

        // If any errors
        boolean error = false;

        // Vault setup
        if (!setupEconomy()) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        // Plugin startup logic


        loadManagers();
        configManager.reloadConfig();
        registerListeners();

        debug("Enabling MoneyPouch version " + getDescription().getVersion());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void debug(String msg) {
        if (configManager.isDebug()) logger.info(msg);
    }

    private void loadManagers() {
        commandManager = new CommandManager(this);
        configManager = new ConfigManager(this);
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        debug("Registering Listeners");
        pm.registerEvents(new OnRightClick(this), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
