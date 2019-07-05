package com.pustinek.moneypouch.commands;

import org.bukkit.command.CommandSender;

public class CommandHelp extends CommandDefault {
    public CommandHelp() {
    }

    @Override
    public String getCommandStart() {
        return "moneypouch help";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission("moneypouch.help")) {
            return "&2/moneypouch help &f- show plugin help information";
        }
        return null;

    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
