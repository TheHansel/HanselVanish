package pl.hansel101.hanselvanish.Events;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.protocol.packets.interface_.RemoveFromServerPlayerList;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.HiddenPlayersManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import fi.sulku.hytale.TinyMsg;
import pl.hansel101.hanselvanish.HanselVanish;
import pl.hansel101.hanselvanish.UI.VanishStatus;


import java.util.Objects;
import java.util.UUID;

import static pl.hansel101.hanselvanish.HanselVanish.LOG;


public class PlayerReady {
    HanselVanish instance;

    public PlayerReady(HanselVanish instance) {
        this.instance = instance;
    }

    public void handle(PlayerReadyEvent event) {
        Player playerEntity = event.getPlayer();

        World world = playerEntity.getWorld();
        if(world == null) {
            LOG.atSevere().log("Failed to get world for player " + playerEntity.getDisplayName());
            return;
        }

        world.execute(() -> {
            PlayerRef player = world.getEntityStore().getStore().getComponent(event.getPlayerRef(), PlayerRef.getComponentType());
            if(player == null) {
                LOG.atSevere().log("Failed to get PlayerRef for " + playerEntity.getDisplayName());
                return;
            }

            UUID playerUUID = player.getUuid();

            RemoveFromServerPlayerList packet = new RemoveFromServerPlayerList(instance.vanishedPlayers.toArray(new UUID[]{}));
            if(instance.vanishedPlayers.contains(playerUUID)) {
                Universe.get().getWorlds().forEach((_, iterWorld) -> {
                    iterWorld.execute(() -> {
                        iterWorld.getPlayerRefs().stream()
                                .filter(a -> !(Objects.equals(a, player) || PermissionsModule.get().hasPermission(a.getUuid(), "hanselvanish.canseevanished"))).forEach(targetPlayer -> {
                            targetPlayer.getPacketHandler().write(packet);
                        });
                    });
                });

                MultipleHUD.getInstance().setCustomHud(playerEntity, player, "HanselVanish_VanishStatus", new VanishStatus(player));
                player.sendMessage(TinyMsg.parse("<c:green>You are still invisible."));
            }

            if(playerEntity.hasPermission("hanselvanish.canseevanished")) {
                return;
            }


            HiddenPlayersManager hpm = player.getHiddenPlayersManager();
            instance.vanishedPlayers.stream().filter(a -> !Objects.equals(a, playerUUID)).forEach(targetUUID -> {
                hpm.hidePlayer(targetUUID);
                player.getPacketHandler().write(new RemoveFromServerPlayerList(new UUID[]{targetUUID}));
            });



            playerEntity.getWorldMapTracker().setPlayerMapFilter(who -> instance.vanishedPlayers.contains(who.getUuid()));
        });
    }
}
