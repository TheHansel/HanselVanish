package pl.hansel101.hanselvanish.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public record VanishDisableEvent(@NonNullDecl Ref<EntityStore> ref) implements IEvent<Void> {
    public static void dispatch(Ref<EntityStore> ref) {
        IEventDispatcher<VanishDisableEvent, VanishDisableEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(VanishDisableEvent.class);
        if(dispatcher.hasListener()) {
            dispatcher.dispatch(new VanishDisableEvent(ref));
        }
    }
}
