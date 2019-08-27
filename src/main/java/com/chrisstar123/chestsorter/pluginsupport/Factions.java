package com.chrisstar123.chestsorter.pluginsupport;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.chrisstar123.chestsorter.ChestSorter;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig.Factions.Protection;
import com.massivecraft.factions.perms.PermissibleAction;

public class Factions {
    private static Plugin factions = null;
    private static boolean legacy = false;

    public static void setup() {
        factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
        if (factions == null || !factions.isEnabled())
            return;

        ChestSorter.cs.getLogger()
                .info("Successfully hooked into Factions!"
                        + (ChestSorter.cs.getConfig().getBoolean("factions.integration", false) ? ""
                                : " Integration is currently disabled (\"factions.integration\")."));
        
        String version = factions.getDescription().getVersion().replaceAll("[^\\d]", "");
        if (version.compareTo("1695049") < 0) {
            legacy = true;
        }
    }

    public static boolean getEnabled() {
        return factions != null && ChestSorter.cs.getConfig().getBoolean("factions.integration", false);
    }

    public static boolean canModifyChest(Player player, Block block) {
        FLocation fLocation = new FLocation(block.getLocation());
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = Board.getInstance().getFactionAt(fLocation);

        boolean canSort = false;

        if (!legacy) {
            canSort = canSort(fPlayer, faction);
        } else {
            try {
                canSort = canSortLegacy(fPlayer, faction);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException |
                    SecurityException | ClassNotFoundException e) {
            }
        }

        return canSort;
    }
    
    public static boolean canSort(FPlayer fPlayer, Faction faction) {
        Protection prot = FactionsPlugin.getInstance().conf().factions().protection();
        boolean fBypass = prot.getPlayersWhoBypassAllProtection().contains(fPlayer.getName())
                || fPlayer.isAdminBypassing();
        boolean fWilderness = faction.isWilderness() && !prot.isWildernessDenyBuild();
        boolean fWarzone = faction.isWarZone() && !prot.isWarZoneDenyBuild();
        boolean fSafezone = faction.isSafeZone() && !prot.isSafeZoneDenyBuild();

        if (fBypass || fWilderness || fWarzone || fSafezone) {
            return true;
        } else if (faction.hasAccess(fPlayer, PermissibleAction.BUILD)) {
            return true;
        }

        return false;
    }
    
    @SuppressWarnings("unchecked")
    public static boolean canSortLegacy(FPlayer fPlayer, Faction faction) throws IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
        Class<?> conf = Class.forName("com.massivecraft.factions.Conf");
        boolean wDenyBuild = conf.getDeclaredField("wildernessDenyBuild").getBoolean(null);
        boolean wzDenyBuild = conf.getDeclaredField("warZoneDenyBuild").getBoolean(null);
        boolean szDenyBuild = conf.getDeclaredField("safeZoneDenyBuild").getBoolean(null);
        Set<String> bypassPlayers = (Set<String>) conf.getDeclaredField("playersWhoBypassAllProtection").get(null);

        boolean fBypass = bypassPlayers.contains(fPlayer.getName())
                || fPlayer.isAdminBypassing();
        boolean fWilderness = faction.isWilderness() && !wDenyBuild;
        boolean fWarzone = faction.isWarZone() && !wzDenyBuild;
        boolean fSafezone = faction.isSafeZone() && !szDenyBuild;

        if (fBypass || fWilderness || fWarzone || fSafezone) {
            return true;
        } else if (faction.getFPlayers().contains(fPlayer)) {
            return true;
        }

        return false;
    }
}
