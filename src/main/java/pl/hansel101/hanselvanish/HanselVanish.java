package pl.hansel101.hanselvanish;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
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
import pl.hansel101.hanselvanish.Commands.VanishCommand;
import pl.hansel101.hanselvanish.Components.PlayerVanishStatus;
import pl.hansel101.hanselvanish.Systems.PlayerJoinSystem;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HanselVanish extends JavaPlugin {
    public static HytaleLogger LOG;
    public Set<UUID> vanishedPlayers = ConcurrentHashMap.newKeySet();
    private final Config<VanishConfig> configStore = this.withConfig(VanishConfig.CODEC);

    public HanselVanish(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOG = HytaleLogger.forEnclosingClass();
        LOG.atInfo().log("Loading HanselVanish!");

        configStore.save();

        ComponentRegistryProxy<EntityStore> registry = this.getEntityStoreRegistry();

        // registering components
        ComponentType<EntityStore, PlayerVanishStatus> componentType =
                registry.registerComponent(PlayerVanishStatus.class, "HanselVanish_VanishStatus",PlayerVanishStatus.CODEC);
        PlayerVanishStatus.setComponentType(componentType);

        // registering systems
        registry.registerSystem(new PlayerJoinSystem(this));

        // registering commands
        CommandRegistry commandRegistry = this.getCommandRegistry();
        commandRegistry.registerCommand(new VanishCommand(this, "vanish", "Toggles vanish for player", false));
    }

    public void sendFakeJoinMessage(PlayerRef target, String playerName, String worldName) {
        VanishConfig config = configStore.get();
        if(!config.isFakeJoinAndLeaveMessagesEnabled()) {
            return;
        }

        if(!config.useCustomFakeMessages()) {
            target.sendMessage(Message.translation("server.general.playerJoinedWorld").param("username", playerName).param("world", worldName));
        } else {
            target.sendMessage(TinyMsg.parse(config.getCustomFakeJoinMessage().replace("{username}", playerName).replace("{world}", worldName)));
        }
    }


    public void sendFakeLeaveMessage(PlayerRef target, String playerName, String worldName) {
        VanishConfig config = configStore.get();
        if(!config.isFakeJoinAndLeaveMessagesEnabled()) {
            return;
        }

        if(!config.useCustomFakeMessages()) {
            target.sendMessage(Message.translation("server.general.playerLeftWorld").param("username", playerName).param("world", worldName));
        } else {
            target.sendMessage(TinyMsg.parse(config.getCustomFakeLeaveMessage().replace("{username}", playerName).replace("{world}", worldName)));
        }
    }
}
