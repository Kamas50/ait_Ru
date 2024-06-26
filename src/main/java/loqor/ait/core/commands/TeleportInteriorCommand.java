package loqor.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import loqor.ait.AITMod;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.util.TardisUtil;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TeleportInteriorCommand {
	public static final SuggestionProvider<ServerCommandSource> TARDIS_SUGGESTION = (context, builder) -> CommandSource.suggestMatching(ServerTardisManager.getInstance().getLookup().keySet().stream().map(UUID::toString), builder);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal(AITMod.MOD_ID)
				.then(literal("teleport").requires(source -> source.hasPermissionLevel(2))
						.then(literal("interior")
						.then(argument("tardis", UuidArgumentType.uuid()).suggests(TARDIS_SUGGESTION)
							.executes(TeleportInteriorCommand::runCommand)
						.then(argument("players", EntityArgumentType.players())
								.executes(TeleportInteriorCommand::runCommandWithPlayers)))))
		);
	}

	private static int runCommand(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity source = context.getSource().getPlayer();

		return teleportToInterior(UuidArgumentType.getUuid(context, "tardis"), Collections.singleton(source));
	}

	private static int runCommandWithPlayers(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity source = context.getSource().getPlayer();

		Collection<ServerPlayerEntity> players;
		try {
			players = EntityArgumentType.getPlayers(context, "players");
		} catch (CommandSyntaxException e) {
			return 0;
		}

		return teleportToInterior(UuidArgumentType.getUuid(context, "tardis"), source, players);
	}

	private static int teleportToInterior(UUID tardisId, ServerPlayerEntity source, Collection<ServerPlayerEntity> players) {
		Tardis tardis = ServerTardisManager.getInstance().getTardis(tardisId);

		if (tardis == null || source == null) return 0;

		for (ServerPlayerEntity player : players) {
			TardisUtil.teleportInside(tardis, player);
		}
		source.sendMessage(Text.literal("Successful teleport - interior of [" + tardis.getUuid().toString().substring(0, 7) + "]"), true); // testing purposes can be removed if ugly

		return Command.SINGLE_SUCCESS;
	}

	private static int teleportToInterior(UUID tardisId, Collection<ServerPlayerEntity> players) {
		if (players.isEmpty()) return 0;

		return teleportToInterior(tardisId, players.stream().findFirst().get(), players);
	}
}
