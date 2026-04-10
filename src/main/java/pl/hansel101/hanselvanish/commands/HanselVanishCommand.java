package pl.hansel101.hanselvanish.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import fi.sulku.hytale.TinyMsg;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import pl.hansel101.hanselvanish.HanselVanish;

import java.util.concurrent.CompletableFuture;

public class HanselVanishCommand extends AbstractCommandCollection {
    final HanselVanish instance;

    public HanselVanishCommand(HanselVanish instance, @NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);
        this.instance = instance;

        addSubCommand(new VersionCommand("version", "Shows plugin version", false));
        addSubCommand(new ReloadCommand("reload", "Reloads plugin config", false));
    }

    private class VersionCommand extends AbstractAsyncCommand {
        public VersionCommand(@NonNullDecl String name, @NonNullDecl String description, boolean requiresConfirmation) {
            super(name, description, requiresConfirmation);
            addAliases("ver");
        }

        @NonNullDecl
        @Override
        protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext ctx) {
            ctx.sendMessage(TinyMsg.parse("<c:green>HanselVanish <c:gold>v" + instance.getManifest().getVersion().toString()));
            ctx.sendMessage(TinyMsg.parse("<gray>Github repo: <white><u><link:https://github.com/TheHansel/HanselVanish>github.com/TheHansel/HanselVanish</link>"));
            ctx.sendMessage(TinyMsg.parse("<gray>CurseForge page: <white><u><link:https://www.curseforge.com/hytale/mods/hansel-vanish>curseforge.com/hytale/mods/hansel-vanish</link>"));

            return CompletableFuture.completedFuture(null);
        }
    }

    private class ReloadCommand extends AbstractAsyncCommand {
        public ReloadCommand(@NonNullDecl String name, @NonNullDecl String description, boolean requiresConfirmation) {
            super(name, description, requiresConfirmation);
            requirePermission("hanselvanish.reload");
        }

        @NonNullDecl
        @Override
        protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext ctx) {
            ctx.sendMessage(TinyMsg.parse("<c:gold>Reloading HanselVanish config..."));
            instance.reloadConfig().thenAccept(time -> ctx.sendMessage(TinyMsg.parse("<c:green>Reloaded HanselVanish config in <gold>" + time / 1_000_000.0D + "ms")));
            
            return CompletableFuture.completedFuture(null);
        }
    }
}
