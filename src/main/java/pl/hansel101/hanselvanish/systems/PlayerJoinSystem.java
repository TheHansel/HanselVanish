package pl.hansel101.hanselvanish.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.protocol.packets.interface_.RemoveFromServerPlayerList;
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
import pl.hansel101.hanselvanish.HanselVanish;
import pl.hansel101.hanselvanish.components.PlayerVanished;
import pl.hansel101.hanselvanish.ui.VanishStatus;

import java.util.Objects;
import java.util.UUID;

import static pl.hansel101.hanselvanish.HanselVanish.LOG;
import static pl.hansel101.hanselvanish.HanselVanish.PERMISSION_CANSEEVANISHED;


public class PlayerJoinSystem extends RefSystem<EntityStore> {
    final HanselVanish instance;

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
        if (playerEntity == null) {
            LOG.atSevere().log("Failed to get Player!");
            return;
        }


        World world = playerEntity.getWorld();
        if (world == null) {
            LOG.atSevere().log("Failed to get world");
            return;
        }


        world.execute(() -> {
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            assert playerRef != null;

            UUID playerUUID = playerRef.getUuid();

            RemoveFromServerPlayerList packet = new RemoveFromServerPlayerList(instance.vanishedPlayers.toArray(new UUID[]{}));
            if (store.getComponent(ref, PlayerVanished.getComponentType()) != null) {
                Universe.get().getWorlds().forEach((_, iterWorld) -> {
                    iterWorld.execute(() -> {
                        iterWorld.getPlayerRefs().stream().filter(target -> {
                                    if (Objects.equals(target, playerRef)) {
                                        return false;
                                    }
                                    if(PermissionsModule.get().hasPermission(target.getUuid(), PERMISSION_CANSEEVANISHED)) {
                                        target.sendMessage(TinyMsg.parse("<c:#CCCCCC><i>" + playerRef.getUsername() + " <c:#C2C2C2>has joined while vanished."));
                                        return false;
                                    }

                                    return true;
                                }).forEach(targetPlayer -> {
                                    targetPlayer.getPacketHandler().write(packet);
                                });
                    });
                });

                instance.vanishedPlayers.add(playerUUID);
                playerEntity.getHudManager().addCustomHud(playerRef, new VanishStatus(playerRef));
            }

            if (playerRef.hasPermission(PERMISSION_CANSEEVANISHED)) {
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
    }
}
