package pl.hansel101.hanselvanish.Commands;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.AddToServerPlayerList;
import com.hypixel.hytale.protocol.packets.interface_.RemoveFromServerPlayerList;
import com.hypixel.hytale.protocol.packets.interface_.ServerPlayerListPlayer;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fi.sulku.hytale.TinyMsg;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import pl.hansel101.hanselvanish.HanselVanish;
import pl.hansel101.hanselvanish.UI.Empty;
import pl.hansel101.hanselvanish.UI.VanishStatus;

import java.util.Objects;
import java.util.UUID;

public class VanishCommand extends AbstractTargetPlayerCommand {
    private final HanselVanish instance;

    public VanishCommand(HanselVanish instance, @NonNullDecl String name, @NonNullDecl String description, boolean requiresConfirmation) {
        super(name, description, requiresConfirmation);
        this.instance = instance;
        requirePermission("hanselvanish.command.vanish");
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

        if(!instance.vanishedPlayers.contains(playerUUID)) {
            final RemoveFromServerPlayerList packet = new RemoveFromServerPlayerList(new UUID[]{playerUUID});

            Universe.get().getWorlds().forEach((_, iterWorld) -> {
                iterWorld.execute(() -> {
                    iterWorld.getPlayerRefs().stream()
                            .filter(target -> !(Objects.equals(target, player) || PermissionsModule.get().hasPermission(target.getUuid(), "hanselvanish.canseevanished")))
                            .forEach(target -> {
                                target.getHiddenPlayersManager().hidePlayer(playerUUID);

                                target.getPacketHandler().write(packet);

                                Ref<EntityStore> targetRef = target.getReference();
                                if(targetRef != null) {
                                    Player targetEntity = iterWorld.getEntityStore().getStore().getComponent(targetRef, Player.getComponentType());
                                    assert targetEntity != null;
                                    targetEntity.getWorldMapTracker().setPlayerMapFilter(who -> instance.vanishedPlayers.contains(who.getUuid()));
                                }
                            });
                });
            });

            Player playerEntity = store.getComponent(ref, Player.getComponentType());
            if(playerEntity != null) {
                MultipleHUD.getInstance().setCustomHud(playerEntity, player, "HanselVanish_VanishStatus", new VanishStatus(player));
            } else {
                LOGGER.atWarning().log("Failed to get Player object. Vanish status hud won't be displayed!");
            }


            instance.vanishedPlayers.add(playerUUID);
            if(Objects.equals(playerUUID, ctx.sender().getUuid())) {
                ctx.sendMessage(TinyMsg.parse("<c:green>You are now invisible."));
            } else {
                player.sendMessage(TinyMsg.parse("<c:green>You are now invisible."));
                ctx.sendMessage(TinyMsg.parse("<c:green>Enabled vanish for " + player.getUsername()));
            }
        } else {
            final AddToServerPlayerList packet = new AddToServerPlayerList(new ServerPlayerListPlayer[]{new ServerPlayerListPlayer(playerUUID, player.getUsername(), player.getWorldUuid(), 0)});

             Universe.get().getWorlds().forEach((_, iterWorld) -> {
                iterWorld.execute(() -> {
                    iterWorld.getPlayerRefs().forEach(target -> {
                        target.getHiddenPlayersManager().showPlayer(playerUUID);
                        target.getPacketHandler().write(packet);
                    });
                });
            });

            Player playerEntity = store.getComponent(ref, Player.getComponentType());
            if(playerEntity != null) {
                MultipleHUD.getInstance().setCustomHud(playerEntity, player, "HanselVanish_VanishStatus", new Empty(player));
            } else {
                LOGGER.atWarning().log("Failed to get Player object. Vanish status hud won't be displayed!");
            }

            instance.vanishedPlayers.remove(playerUUID);
            if(Objects.equals(playerUUID, ctx.sender().getUuid())) {
                ctx.sendMessage(TinyMsg.parse("<c:red>You are no longer invisible."));
            } else {
                player.sendMessage(TinyMsg.parse("<c:red>You are no longer invisible."));
                ctx.sendMessage(TinyMsg.parse("<c:red>Disabled vanish for " + player.getUsername()));
            }
        }

    }


}
