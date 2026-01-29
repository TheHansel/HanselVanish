package pl.hansel101.hanselvanish.Components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerVanishStatus implements Component<EntityStore> {
    private static ComponentType<EntityStore, PlayerVanishStatus> TYPE;

    public static void setComponentType(ComponentType<EntityStore, PlayerVanishStatus> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, PlayerVanishStatus> getComponentType() {
        return TYPE;
    }

    public static final BuilderCodec<PlayerVanishStatus> CODEC = BuilderCodec
            .builder(PlayerVanishStatus.class, PlayerVanishStatus::new).append(
                    new KeyedCodec<>("VanishStatus", Codec.BOOLEAN),
                    (component, value) -> component.vanishStatus = value, component -> component.vanishStatus)
            .add().build();


    private boolean vanishStatus = false;

    public PlayerVanishStatus() {}

    public PlayerVanishStatus(boolean vanishStatus) {
        this.vanishStatus = vanishStatus;
    }

    @NullableDecl
    @Override
    public PlayerVanishStatus clone() {
        return new PlayerVanishStatus(this.vanishStatus);
    }

    public boolean isVanished() {
        return vanishStatus;
    }

    public void vanishOn() {
        vanishStatus = true;
    }

    public void vanishOff() {
        vanishStatus = false;
    }
}
