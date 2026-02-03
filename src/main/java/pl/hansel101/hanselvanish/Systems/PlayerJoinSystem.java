package pl.hansel101.hanselvanish.Systems;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.protocol.packets.interface_.RemoveFromServerPlayerList;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.HiddenPlayersManager;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fi.sulku.hytale.TinyMsg;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import pl.hansel101.hanselvanish.Components.PlayerVanishStatus;
import pl.hansel101.hanselvanish.HanselVanish;
import pl.hansel101.hanselvanish.UI.VanishStatus;

import java.util.Objects;
import java.util.UUID;

import static pl.hansel101.hanselvanish.HanselVanish.LOG;


public class PlayerJoinSystem extends RefSystem<EntityStore> {
    HanselVanish instance;

    public PlayerJoinSystem(HanselVanish instance) {
        this.instance = instance;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(PlayerRef.getComponentType());
    }

    @Override
    public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref,
                              @NonNullDecl AddReason addReason,
                              @NonNullDecl Store<EntityStore> store,
                              @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        if(addReason != AddReason.LOAD) return;

        Player playerEntity = commandBuffer.getComponent(ref, Player.getComponentType());
        if(playerEntity == null) {
            LOG.atSevere().log("Failed to get Player!");
            return;
        }

        World world = playerEntity.getWorld();
        if(world == null) {
            LOG.atSevere().log("Failed to get world for player %s", playerEntity.getDisplayName());
            return;
        }

        world.execute(() -> {
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            assert playerRef != null;

            PlayerVanishStatus vanishStatus = store.getComponent(ref, PlayerVanishStatus.getComponentType());
            if(vanishStatus == null) {
                commandBuffer.addComponent(ref, PlayerVanishStatus.getComponentType(), new PlayerVanishStatus());
            } else {
                if(vanishStatus.isVanished()) {
                    instance.vanishedPlayers.add(playerRef.getUuid());
                }
            }

            UUID playerUUID = playerRef.getUuid();

            RemoveFromServerPlayerList packet = new RemoveFromServerPlayerList(instance.vanishedPlayers.toArray(new UUID[]{}));
            if(instance.vanishedPlayers.contains(playerUUID)) {
                Universe.get().getWorlds().forEach((_, iterWorld) -> {
                    iterWorld.execute(() -> {
                        iterWorld.getPlayerRefs().stream()
                                .filter(a -> !(Objects.equals(a, playerRef) || PermissionsModule.get().hasPermission(a.getUuid(), "hanselvanish.canseevanished"))).forEach(targetPlayer -> {
                                    targetPlayer.getPacketHandler().write(packet);
                                });
                    });
                });

                MultipleHUD.getInstance().setCustomHud(playerEntity, playerRef, "HanselVanish_VanishStatus", new VanishStatus(playerRef));
                playerRef.sendMessage(TinyMsg.parse("<c:green>You are still invisible."));
            }

            if(playerEntity.hasPermission("hanselvanish.canseevanished")) {
                return;
            }


            HiddenPlayersManager hpm = playerRef.getHiddenPlayersManager();
            instance.vanishedPlayers.stream().filter(a -> !Objects.equals(a, playerUUID)).forEach(targetUUID -> {
                hpm.hidePlayer(targetUUID);
                playerRef.getPacketHandler().write(new RemoveFromServerPlayerList(new UUID[]{targetUUID}));
            });



            playerEntity.getWorldMapTracker().setPlayerMapFilter(who -> instance.vanishedPlayers.contains(who.getUuid()));
        });
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref,
                               @NonNullDecl RemoveReason removeReason,
                               @NonNullDecl Store<EntityStore> store,
                               @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        if(removeReason != RemoveReason.UNLOAD) return;


        UUID playerUUID = Objects.requireNonNull(store.getComponent(ref, UUIDComponent.getComponentType())).getUuid();

        PlayerVanishStatus vanishStatus = store.getComponent(ref, PlayerVanishStatus.getComponentType());
        assert vanishStatus != null;

        if(instance.vanishedPlayers.contains(playerUUID)) {
            vanishStatus.vanishOn();
        } else {
            vanishStatus.vanishOff();
        }
    }
}
