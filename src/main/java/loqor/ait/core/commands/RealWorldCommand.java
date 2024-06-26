package loqor.ait.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import loqor.ait.core.entities.TardisRealEntity;
import loqor.ait.AITMod;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.TardisTravel;
import loqor.ait.tardis.util.TardisUtil;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static loqor.ait.core.commands.SetFuelCommand.TARDIS_SUGGESTION;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RealWorldCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal(AITMod.MOD_ID)
				.then(literal("real-world").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
								.then(argument("tardis-id", UuidArgumentType.uuid()).suggests(TARDIS_SUGGESTION)
												.executes(RealWorldCommand::runSpawnRealTardisTestCommand))));
	}

	private static int runSpawnRealTardisTestCommand(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity source = context.getSource().getPlayer();
		Tardis tardis = ServerTardisManager.getInstance().getTardis(UuidArgumentType.getUuid(context, "tardis-id"));
		if (tardis == null || tardis.getTravel().getState() != TardisTravel.State.LANDED || source == null) return 0;
		BlockPos spawnBlockPos = tardis.getExterior().getExteriorPos();
		try {
			TardisUtil.teleportOutside(tardis, source);
			source.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, -1, 1, false, false, false));
			TardisRealEntity.spawnFromTardisId(tardis.getExterior().getExteriorPos().getWorld(), tardis.getUuid(), spawnBlockPos, source, tardis.getDoor().getDoorPos());
			Text textResponse = Text.translatable("command.ait.realworld.response").append(Text.literal(" " + spawnBlockPos.getX() + ", " + spawnBlockPos.getY() + ", " + spawnBlockPos.getZ()));
			source.sendMessage(textResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}


		return 1;

	}
}
