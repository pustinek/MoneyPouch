package com.pustinek.moneypouch.listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import com.pustinek.moneypouch.MoneyPouch;
import com.pustinek.moneypouch.PouchItem;
import com.pustinek.moneypouch.managers.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OnRightClick implements Listener {

    private MoneyPouch plugin;
    // Used to store the players that are currently opening the moneypouch
    private ArrayList<UUID> openingPlayers = new ArrayList<>();

    public OnRightClick(MoneyPouch plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        // This function ONLY compares the clicked item NAME to the one saved in config files [name}
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getHand() == EquipmentSlot.HAND) {
                ItemStack itemStack = event.getItem();
                if (itemStack == null) return;
                if (!itemStack.hasItemMeta()) return;


                HashMap<String, PouchItem> pouches = plugin.getMoneyPouches();
                PouchItem matchedPouchItem = null;
                for (PouchItem pi : pouches.values()) {
                    if (pi.getItem().isSimilar(itemStack)) {
                        matchedPouchItem = pi;
                    }
                }
                if (matchedPouchItem == null) return; //Not a singe pouch was matched with this item
                event.setCancelled(true);
                Player player = event.getPlayer();
                if(openingPlayers.contains(player.getUniqueId()) && !this.plugin.getConfigManager().isRecursiveOpening()){
                    MoneyPouch.message(player, " &cYou are already in the process of receiving a reward, please wait !");
                    return;
                }
                openingPlayers.add(player.getUniqueId());


                final int amount = player.getInventory().getItemInMainHand().getAmount();
                if (amount == 1) {
                    player.getInventory().getItemInMainHand().setAmount(0);
                    player.updateInventory();
                    event.setCancelled(true);
                } else {
                    player.getInventory().getItemInMainHand().setAmount(amount - 1);
                    player.updateInventory();
                }


                int moneyToGive = ThreadLocalRandom.current().nextInt(matchedPouchItem.getPriceRange()[0], matchedPouchItem.getPriceRange()[1] + 1);
                int moneyToGiveLength = String.valueOf(moneyToGive).length();
                playSound(player, plugin.getConfigManager().getSoundOpen(), 3.0F, 0.533F);
                new BukkitRunnable() {
                    int x = 0;

                    @Override
                    public void run() {
                        String s = String.valueOf(moneyToGive).substring(moneyToGiveLength - x, moneyToGiveLength);
                        String ss = String.valueOf(moneyToGive).substring(0, moneyToGiveLength - x);
                        TitleAPI.sendTitle(player, 5, 20, 5, "&5&k" + ss + "&r&2" + s, "You will receive");
                        x++;
                        playSound(player, plugin.getConfigManager().getSoundReveal(), 3.0F, 0.533F);
                        if (x > moneyToGiveLength) {
                            Economy econ = MoneyPouch.getEconomy();
                            EconomyResponse r = econ.depositPlayer(player, moneyToGive);
                            if (r.transactionSuccess()) {
                                MoneyPouch.message(player, String.format(" You received %s, your new balance is %s", econ.format(r.amount), econ.format(r.balance)));
                                openingPlayers.remove(player.getUniqueId());
                                playSound(player, plugin.getConfigManager().getSoundEnd(), 3.0F, 0.533F);
                            } else {
                                MoneyPouch.messageNoPrefix(player, String.format(" An error occured: %s", r.errorMessage));
                            }
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 5, 20);
            }
        }


    }

    private void playSound(Player player, Sound sound, float vol, float pitch) {
        if (plugin.getConfigManager().isSoundEnabled()) {
            player.playSound(player.getLocation(), sound, vol, pitch);
        }
    }
}
