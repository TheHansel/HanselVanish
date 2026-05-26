package pl.hansel101.hanselvanish;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import fi.sulku.hytale.TinyMsg;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import pl.hansel101.hanselvanish.commands.HanselVanishCommand;
import pl.hansel101.hanselvanish.commands.VanishCommand;
import pl.hansel101.hanselvanish.components.PlayerVanished;
import pl.hansel101.hanselvanish.events.VanishDisableEvent;
import pl.hansel101.hanselvanish.events.VanishEnableEvent;
import pl.hansel101.hanselvanish.handlers.VanishDisableHandler;
import pl.hansel101.hanselvanish.handlers.VanishEnableHandler;
import pl.hansel101.hanselvanish.systems.PlayerJoinSystem;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class HanselVanish extends JavaPlugin {
    public static final String PERMISSION_CANSEEVANISHED = "hanselvanish.canseevanished";
    public static final String PERMISSION_COMMAND_VANISH = "hanselvanish.command.vanish";
    public static final String PERMISSION_COMMAND_MANAGE = "hanselvanish.command.manage";

    public static final String HUD_KEY_STATUS = "HanselVanish_VanishStatus";

    public static HytaleLogger LOG;
    public final Set<UUID> vanishedPlayers = ConcurrentHashMap.newKeySet();

    private final Config<VanishConfig> configStore = this.withConfig(VanishConfig.CODEC);
    private final AtomicReference<VanishConfig> configRef = new AtomicReference<>();

    public HanselVanish(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOG = HytaleLogger.forEnclosingClass();
        LOG.atInfo().log("Loading HanselVanish!");

        configStore.save().thenRun(() -> configStore.load().thenAccept(configRef::set));


        // registering events
        EventRegistry eventRegistry =  this.getEventRegistry();
        eventRegistry.register(VanishEnableEvent.class, new VanishEnableHandler(this));
        eventRegistry.register(VanishDisableEvent.class, new VanishDisableHandler(this));

        // registering components
        ComponentRegistryProxy<EntityStore> registry = this.getEntityStoreRegistry();


        ComponentType<EntityStore, PlayerVanished> componentType =
                registry.registerComponent(PlayerVanished.class, "HanselVanish_PlayerVanished", PlayerVanished.CODEC);
        PlayerVanished.setComponentType(componentType);

        // registering systems
        registry.registerSystem(new PlayerJoinSystem(this));

        // registering commands
        CommandRegistry commandRegistry = this.getCommandRegistry();
        commandRegistry.registerCommand(new VanishCommand(this, "vanish", "Toggles vanish for player", false));
        commandRegistry.registerCommand(new HanselVanishCommand(this, "hanselvanish", "Manage HanselVanish plugin"));
    }


    public CompletableFuture<Long> reloadConfig() {
        LOG.atInfo().log("Reloading HanselVanish...");
        final long startTime = System.nanoTime();
        return configStore.load().thenApply(newConfig -> {
            configRef.set(newConfig);

            long reloadTime = System.nanoTime() - startTime;
            LOG.atInfo().log("Successfully reloaded in %d", reloadTime);

            return reloadTime;
        });
    }

    public void sendFakeJoinMessage(PlayerRef target, String playerName, String worldName) {
        VanishConfig config = configRef.get();
        if (!config.isFakeJoinAndLeaveMessagesEnabled()) {
            return;
        }

        if (!config.useCustomFakeMessages()) {
            target.sendMessage(Message.translation("server.general.playerJoinedWorld").param("username", playerName).param("world", worldName));
        } else {
            target.sendMessage(TinyMsg.parse(config.getCustomFakeJoinMessage().replace("{username}", playerName).replace("{world}", worldName)));
        }
    }


    public void sendFakeLeaveMessage(PlayerRef target, String playerName, String worldName) {
        VanishConfig config = configRef.get();
        if (!config.isFakeJoinAndLeaveMessagesEnabled()) {
            return;
        }

        if (!config.useCustomFakeMessages()) {
            target.sendMessage(Message.translation("server.general.playerLeftWorld").param("username", playerName).param("world", worldName));
        } else {
            target.sendMessage(TinyMsg.parse(config.getCustomFakeLeaveMessage().replace("{username}", playerName).replace("{world}", worldName)));
        }
    }
}
