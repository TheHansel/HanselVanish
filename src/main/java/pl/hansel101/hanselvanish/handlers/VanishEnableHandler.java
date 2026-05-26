package pl.hansel101.hanselvanish.handlers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.RemoveFromServerPlayerList;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fi.sulku.hytale.TinyMsg;
import pl.hansel101.hanselvanish.HanselVanish;
import pl.hansel101.hanselvanish.components.PlayerVanished;
import pl.hansel101.hanselvanish.events.VanishEnableEvent;
import pl.hansel101.hanselvanish.ui.VanishStatus;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static pl.hansel101.hanselvanish.HanselVanish.LOG;
import static pl.hansel101.hanselvanish.HanselVanish.PERMISSION_CANSEEVANISHED;

public class VanishEnableHandler implements Consumer<VanishEnableEvent> {
    private final HanselVanish instance;

    public VanishEnableHandler(HanselVanish instance) {
        this.instance = instance;
    }

    @Override
    public void accept(VanishEnableEvent event) {
        Ref<EntityStore> ref = event.ref();
        if (!ref.isValid()) {
            LOG.atSevere().log("VanishEnableHandler got invalid reference!");
            return;
        }

        Store<EntityStore> store = ref.getStore();
        if (store.getComponent(ref, PlayerVanished.getComponentType()) != null) {
            return;
        }

        PlayerRef player = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null) {
            LOG.atSevere().log("VanishEnableHandler got reference to non-player entity!");
            return;
        }

        UUID playerUUID = player.getUuid();
        World world = Universe.get().getWorld(Objects.requireNonNull(player.getWorldUuid()));
        if (world == null) {
            LOG.atSevere().log("VanishEnableHandler: World is null!");
            return;
        }


        final RemoveFromServerPlayerList packet = new RemoveFromServerPlayerList(new UUID[]{playerUUID});
        final String username = player.getUsername(), worldName = world.getName();
        Universe.get().getWorlds().forEach((iterWorldName, iterWorld) -> {
            iterWorld.execute(() -> {
                final boolean isSameWorld = iterWorldName.equals(worldName);
                iterWorld.getPlayerRefs().stream().filter(target -> {
                    if (Objects.equals(target, player)) {
                        return false;
                    }
                    if (PermissionsModule.get().hasPermission(target.getUuid(), PERMISSION_CANSEEVANISHED)) {
                        target.sendMessage(TinyMsg.parse("<c:#CCCCCC><i>" + username + " <c:#C2C2C2>vanished."));
                        return false;
                    }

                    return true;
                }).forEach(target -> {
                    target.getHiddenPlayersManager().hidePlayer(playerUUID);

                    target.getPacketHandler().write(packet);

                    Ref<EntityStore> targetRef = target.getReference();
                    if (targetRef != null) {
                        Player targetEntity = iterWorld.getEntityStore().getStore().getComponent(targetRef, Player.getComponentType());
                        assert targetEntity != null;
                        targetEntity.getWorldMapTracker().setPlayerMapFilter(who -> instance.vanishedPlayers.contains(who.getUuid()));
                    }

                    if (isSameWorld) {
                        instance.sendFakeLeaveMessage(target, username, iterWorldName);
                    }
                });
            });
        });


        Player playerEntity = store.getComponent(ref, Player.getComponentType());
        if (playerEntity != null) {
            playerEntity.getHudManager().addCustomHud(player, new VanishStatus(player));
        } else {
            LOG.atWarning().log("Failed to get Player object. Vanish status hud won't be displayed!");
        }

        store.addComponent(ref, PlayerVanished.getComponentType());
        instance.vanishedPlayers.add(playerUUID);
    }
}
