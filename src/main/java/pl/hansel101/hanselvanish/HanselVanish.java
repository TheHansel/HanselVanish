package pl.hansel101.hanselvanish;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import pl.hansel101.hanselvanish.Commands.VanishCommand;
import pl.hansel101.hanselvanish.Events.PlayerReady;

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

        // registering commands
        this.getCommandRegistry().registerCommand(new VanishCommand(this, "vanish", "Toggles vanish for player", false));

        // registering events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, new PlayerReady(this)::handle);
    }
}
