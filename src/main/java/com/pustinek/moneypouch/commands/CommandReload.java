package com.pustinek.moneypouch.commands;

import com.pustinek.moneypouch.MoneyPouch;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload extends CommandDefault {


    private MoneyPouch plugin;

    public CommandReload(MoneyPouch plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "moneypouch reload";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (!target.hasPermission("moneypouch.reload")) {
            return null;
        }
        return "&2/moneypouch reload &f- reload the plugin config files";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moneypouch.reload")) {
            MoneyPouch.message(sender, noPermission);
            return;
        }

        MoneyPouch.message(sender, "&2 reloading plugin config files");

        plugin.getConfigManager().reloadConfig();


    }
}
