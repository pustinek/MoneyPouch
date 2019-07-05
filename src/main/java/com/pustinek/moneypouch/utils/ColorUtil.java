package com.pustinek.moneypouch.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ColorUtil {


    public static String chatColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> chatColor(List<String> lore) {
        return lore.stream().map(ColorUtil::chatColor).collect(Collectors.toList());
    }


}
