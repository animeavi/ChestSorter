package com.chrisstar123.chestsorter.pluginsupport;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.chrisstar123.chestsorter.ChestSorter;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;

public class LWC {
    private static com.griefcraft.lwc.LWC lwc;

    public static void setup() {
        Plugin test = Bukkit.getServer().getPluginManager().getPlugin("LWC");
        if (!(test instanceof LWCPlugin) || !test.isEnabled())
            return;

        lwc = ((LWCPlugin) test).getLWC();
        ChestSorter.cs.getLogger().info(
                "Successfully hooked into LWC!" + (ChestSorter.cs.getConfig().getBoolean("lwc.integration", false) ? ""
                        : " Integration is currently disabled (\"lwc.integration\")."));
    }

    public static boolean getEnabled() {
        return lwc != null && ChestSorter.cs.getConfig().getBoolean("lwc.integration", false);
    }

    public static boolean canModifyChest(Player player, Block block) {
        Protection protection = lwc.findProtection(block);

        if (protection != null && !(player.getUniqueId().compareTo(UUID.fromString(protection.getOwner())) == 0)) {
            return false;
        }

        return true;
    }
}
