package com.pustinek.moneypouch;

import org.bukkit.inventory.ItemStack;

public class PouchItem {
    private String internalName;
    private String name;
    private ItemStack item;
    private Integer[] priceRange;


    public PouchItem(String internalName, String name, ItemStack item, Integer[] priceRange) {
        this.internalName = internalName;
        this.name = name;
        this.item = item;
        this.priceRange = priceRange;
    }

    public Integer[] getPriceRange() {
        return priceRange;
    }

    public String getInternalName() {
        return internalName;
    }

    public ItemStack getItem() {
        return item;
    }
}
