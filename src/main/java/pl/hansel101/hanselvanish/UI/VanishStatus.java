package pl.hansel101.hanselvanish.UI;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;


public class VanishStatus extends CustomUIHud {
    public VanishStatus(@NonNullDecl PlayerRef player) {
        super(player);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder builder) {
        builder.append("Hud/VanishStatus.ui");
    }
}
