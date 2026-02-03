package pl.hansel101.hanselvanish;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import pl.hansel101.hanselvanish.Commands.VanishCommand;
import pl.hansel101.hanselvanish.Components.PlayerVanishStatus;
import pl.hansel101.hanselvanish.Systems.PlayerJoinSystem;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HanselVanish extends JavaPlugin {
    public static HytaleLogger LOG;
    public Set<UUID> vanishedPlayers = ConcurrentHashMap.newKeySet();

    public HanselVanish(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOG = HytaleLogger.forEnclosingClass();
        LOG.atInfo().log("Loading HanselVanish!");

        ComponentRegistryProxy<EntityStore> registry = this.getEntityStoreRegistry();

        // registering components
        ComponentType<EntityStore, PlayerVanishStatus> componentType = registry.
                registerComponent(PlayerVanishStatus.class, "HanselVanish_VanishStatus",PlayerVanishStatus.CODEC);
        PlayerVanishStatus.setComponentType(componentType);

        // registering systems
        registry.registerSystem(new PlayerJoinSystem(this));

        // registering commands
        this.getCommandRegistry().registerCommand(new VanishCommand(this, "vanish", "Toggles vanish for player", false));
    }
}
