package com.chrisstar123.chestsorter.pluginsupport;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.chrisstar123.chestsorter.ChestSorter;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;

public class Factions {
    private static Plugin factions = null;

    public static void setup() {
        factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
        if (factions == null || !factions.isEnabled())
            return;

        ChestSorter.cs.getLogger()
                .info("Successfully hooked into Factions!"
                        + (ChestSorter.cs.getConfig().getBoolean("factions.integration", false) ? ""
                                : " Integration is currently disabled (\"factions.integration\")."));
    }

    public static boolean getEnabled() {
        return factions != null && ChestSorter.cs.getConfig().getBoolean("factions.integration", false);
    }

    public static boolean canModifyChest(Player player, Block block) {
        FLocation fLocation = new FLocation(block.getLocation());
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        Faction myFaction = fPlayer.getFaction();

        boolean canSort = false;
        boolean fBypass = Conf.playersWhoBypassAllProtection.contains(fPlayer.getName()) || fPlayer.isAdminBypassing();
        boolean fWilderness = faction.isWilderness() && !Conf.wildernessDenyBuild;
        boolean fWarzone = faction.isWarZone() && !Conf.warZoneDenyBuild;
        boolean fSafezone = faction.isSafeZone() && !Conf.safeZoneDenyBuild;

        if (fBypass || fWilderness || fWarzone || fSafezone) {
            canSort = true;
        } else {
            Access fAccess = faction.getAccess(fPlayer, PermissableAction.CONTAINER);

            // Your own faction
            if ((faction.getId() == myFaction.getId()) && fAccess == Access.UNDEFINED) {
                canSort = true;
            } else if (fAccess == Access.ALLOW) {
                canSort = true;
            }
        }

        if (!canSort) {
            return false;
        }

        return true;
    }
}
