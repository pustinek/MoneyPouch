package com.pustinek.moneypouch.commands;

import com.pustinek.moneypouch.MoneyPouch;
import com.pustinek.moneypouch.PouchItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandGiveMP extends CommandDefault {
    private MoneyPouch plugin;

    public CommandGiveMP(MoneyPouch plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "moneypouch give";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (!target.hasPermission("moneypouch.give")) {
            return null;
        }
        return "&2/moneypouch give <player> <item> [amount] &f- give player an item";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moneypouch.give")) {
            MoneyPouch.message(sender, noPermission);
            return;
        }
        if (args.length > 0 && args[0] != null) {
            // Handing for <player>
            if (args.length > 1 && args[1] != null) {
                String userName = args[1];
                Player player = Bukkit.getServer().getPlayer(userName);
                if (player == null) {
                    MoneyPouch.messageNoPrefix(sender, "Player doest exist");
                    return;
                }

                // Handing for <item>
                if (args.length > 2 && args[2] != null) {
                    String itemName = args[2];
                    PouchItem pouchItem = null;
                    for (PouchItem pi : plugin.getMoneyPouches().values()) {
                        if (pi.getInternalName().equalsIgnoreCase(itemName)) {
                            pouchItem = pi;
                        }
                    }
                    if (pouchItem == null) {
                        MoneyPouch.messageNoPrefix(sender, "Item with that name doesn't exist");
                        return;
                    }
                    player.getInventory().addItem(pouchItem.getItem());
                    player.updateInventory();

                }
            }
        }
    }

    @Override
    public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
        List<String> result = new ArrayList<>();
        if (toComplete == 2) {
            if (sender.hasPermission("areashop.give")) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    result.add(p.getName());
                }
            }
        } else if (toComplete == 3) {
            for (PouchItem pi : plugin.getMoneyPouches().values()) {
                result.add(pi.getInternalName());
            }
        }

        return result;
    }

}
