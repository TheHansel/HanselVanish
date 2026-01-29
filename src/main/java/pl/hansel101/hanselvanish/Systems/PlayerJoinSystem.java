package pl.hansel101.hanselvanish.Systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import pl.hansel101.hanselvanish.Components.PlayerVanishStatus;
import pl.hansel101.hanselvanish.HanselVanish;

import java.util.Objects;
import java.util.UUID;


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
