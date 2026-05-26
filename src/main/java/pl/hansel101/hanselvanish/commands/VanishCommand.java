package pl.hansel101.hanselvanish.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fi.sulku.hytale.TinyMsg;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import pl.hansel101.hanselvanish.HanselVanish;
import pl.hansel101.hanselvanish.components.PlayerVanished;
import pl.hansel101.hanselvanish.events.VanishDisableEvent;
import pl.hansel101.hanselvanish.events.VanishEnableEvent;

import java.util.Objects;
import java.util.UUID;

import static pl.hansel101.hanselvanish.HanselVanish.PERMISSION_COMMAND_VANISH;

public class VanishCommand extends AbstractTargetPlayerCommand {
    private final HanselVanish instance;

    public VanishCommand(HanselVanish instance, @NonNullDecl String name, @NonNullDecl String description, boolean requiresConfirmation) {
        super(name, description, requiresConfirmation);
        this.instance = instance;
        requirePermission(PERMISSION_COMMAND_VANISH);
        addAliases("v");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext ctx,
                           @NullableDecl Ref<EntityStore> execRef,
                           @NonNullDecl Ref<EntityStore> ref,
                           @NonNullDecl PlayerRef player,
                           @NonNullDecl World world,
                           @NonNullDecl Store<EntityStore> store) {
        UUID playerUUID = player.getUuid();

        PlayerVanished vanishStatus = store.getComponent(ref, PlayerVanished.getComponentType());

        if (vanishStatus == null) {
            VanishEnableEvent.dispatch(ref);

            if (Objects.equals(playerUUID, ctx.sender().getUuid())) {
                ctx.sendMessage(TinyMsg.parse("<c:green>You are now invisible."));
            } else {
                player.sendMessage(TinyMsg.parse("<c:green>You are now invisible."));
                ctx.sendMessage(TinyMsg.parse("<c:green>Enabled vanish for " + player.getUsername()));
            }
        } else {
            VanishDisableEvent.dispatch(ref);

            if (Objects.equals(playerUUID, ctx.sender().getUuid())) {
                ctx.sendMessage(TinyMsg.parse("<c:red>You are no longer invisible."));
            } else {
                player.sendMessage(TinyMsg.parse("<c:red>You are no longer invisible."));

                ctx.sendMessage(TinyMsg.parse("<c:red>Disabled vanish for " + player.getUsername()));
            }
        }

    }
}
