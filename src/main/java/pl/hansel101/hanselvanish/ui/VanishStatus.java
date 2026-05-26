package pl.hansel101.hanselvanish.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import static pl.hansel101.hanselvanish.HanselVanish.HUD_KEY_STATUS;


public class VanishStatus extends CustomUIHud {
    public VanishStatus(PlayerRef playerRef) {
        super(playerRef, HUD_KEY_STATUS);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder builder) {
        builder.append("Hud/VanishStatus.ui");
    }
}
