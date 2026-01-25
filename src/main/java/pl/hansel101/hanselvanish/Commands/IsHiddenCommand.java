package pl.hansel101.hanselvanish.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import pl.hansel101.hanselvanish.HanselVanish;

import java.util.UUID;

public class IsHiddenCommand extends AbstractPlayerCommand {
    HanselVanish instance;

    public IsHiddenCommand(HanselVanish instance, @NonNullDecl String name, @NonNullDecl String description, boolean requiresConfirmation) {
        this.instance = instance;
        super(name, description, requiresConfirmation);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext ctx,
                           @NonNullDecl Store<EntityStore> store,
                           @NonNullDecl Ref<EntityStore> ref,
                           @NonNullDecl PlayerRef player,
                           @NonNullDecl World world) {
        UUID playerUUID = player.getUuid();
        if(instance.vanishedPlayers.contains(playerUUID)) {
            player.sendMessage(Message.raw("HashSet: contains"));
        } else {
            player.sendMessage(Message.raw("HashSet: not"));
        }

        if(player.getHiddenPlayersManager().isPlayerHidden(playerUUID)) {
            player.sendMessage(Message.raw("HPM: yes"));
        } else {
            player.sendMessage(Message.raw("HPM: no"));
        }
    }
}
