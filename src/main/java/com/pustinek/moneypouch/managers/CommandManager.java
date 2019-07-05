package com.pustinek.moneypouch.managers;

import com.pustinek.moneypouch.MoneyPouch;
import com.pustinek.moneypouch.commands.CommandDefault;
import com.pustinek.moneypouch.commands.CommandGiveMP;
import com.pustinek.moneypouch.commands.CommandHelp;
import com.pustinek.moneypouch.commands.CommandReload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final ArrayList<CommandDefault> commands;
    private MoneyPouch plugin;

    public CommandManager(MoneyPouch plugin) {

        // store plugin instance
        this.plugin = plugin;

        commands = new ArrayList<>();
        // Here you add the commands that you want to use..
        commands.add(new CommandHelp());
        commands.add(new CommandReload(plugin));
        commands.add(new CommandGiveMP(plugin));


        plugin.getCommand("moneypouch").setExecutor(this);
        plugin.getCommand("moneypouch").setTabCompleter(this);
    }

    public void showHelp(CommandSender target) {
        if (!target.hasPermission("moneypouch.help")) {
            //MoneyPouch.message(tar);
            MoneyPouch.message(target, "sorry you dont have the required permissions");
        }

        // Add all messages to a list
        ArrayList<String> messages = new ArrayList<>();
        for (CommandDefault command : commands) {
            String help = command.getHelp(target);
            if (help != null && help.length() != 0) {
                messages.add(help);
            }
        }
        if (target.hasPermission("moneypouch.version")) {
            messages.add(0, "&2==== &fversion " + plugin.getDescription().getVersion() + " &2====");
        }

        for (String message : messages) {
            MoneyPouch.messageNoPrefix(target, message);
        }


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean executed = false;
        for (int i = 0; i < commands.size() && !executed; i++) {
            if (commands.get(i).canExecute(command, args)) {
                commands.get(i).execute(sender, args);
                executed = true;
            }
        }
        if (!executed && args.length == 0) {
            this.showHelp(sender);
        } else if (!executed && args.length > 0) {
            MoneyPouch.message(sender, "Command is not valid");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (!sender.hasPermission("moneypouch.tabcomplete")) {
            return result;
        }
        int toCompleteNumber = args.length;
        String toCompletePrefix = args[args.length - 1].toLowerCase();


        if (toCompleteNumber == 1) {
            for (CommandDefault c : commands) {
                String begin = c.getCommandStart();
                result.add(begin.substring(begin.indexOf(' ') + 1));
            }
        } else {
            String[] start = new String[args.length];
            start[0] = command.getName();
            System.arraycopy(args, 0, start, 1, args.length - 1);
            for (CommandDefault c : commands) {
                if (c.canExecute(command, args)) {
                    result = c.getTabCompleteList(toCompleteNumber, start, sender);
                }
            }
        }
        // Filter and sort the results
        if (!result.isEmpty()) {
            SortedSet<String> set = new TreeSet<>();
            for (String suggestion : result) {
                if (suggestion.toLowerCase().startsWith(toCompletePrefix)) {
                    set.add(suggestion);
                }
            }
            result.clear();
            result.addAll(set);
        }
        return result;

    }
}