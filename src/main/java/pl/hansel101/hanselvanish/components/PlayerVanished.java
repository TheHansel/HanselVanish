package pl.hansel101.hanselvanish.components;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerVanished  implements Component<EntityStore>  {
    private static ComponentType<EntityStore, PlayerVanished> TYPE;

    public static void setComponentType(ComponentType<EntityStore, PlayerVanished> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, PlayerVanished> getComponentType() {
        return TYPE;
    }

    public static final BuilderCodec<PlayerVanished> CODEC = BuilderCodec
            .builder(PlayerVanished.class, PlayerVanished::new).build();

    public PlayerVanished() {}

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new PlayerVanished();
    }
}
