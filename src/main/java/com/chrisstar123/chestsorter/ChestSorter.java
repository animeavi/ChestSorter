package com.chrisstar123.chestsorter;

import com.chrisstar123.chestsorter.command.SortChest;
import com.chrisstar123.chestsorter.pluginsupport.Factions;
import com.chrisstar123.chestsorter.pluginsupport.LWC;
import com.chrisstar123.chestsorter.util.JeffChestSortHelper;

import de.jeffclan.JeffChestSort.JeffChestSortOrganizer;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestSorter extends JavaPlugin {
    public static ChestSorter cs;
    public JeffChestSortOrganizer organizer;

    public ChestSorter() {
        cs = this;
    }

    @Override
    public void onEnable() {
        saveConfig();

        if (getConfig().getBoolean("use.jeffsort.method", true)) {
            JeffChestSortHelper.saveDefaultCategories();
            organizer = new JeffChestSortOrganizer(this);
        }

        if (resolvePlugin("LWC") != null) {
            LWC.setup();
        }

        if (resolvePlugin("Factions") != null) {
            Factions.setup();
        }

        getCommand("SortChest").setExecutor(new SortChest());
        getLogger().info("ChestSorter is now enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChestSorter is now disabled");
    }

    private Plugin resolvePlugin(String name) {
        Plugin temp = Bukkit.getServer().getPluginManager().getPlugin(name);

        if (temp == null) {
            return null;
        }

        return temp;
    }

    public void saveConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }
    }
}
