package loqor.ait.tardis.util.desktop.structures;

import loqor.ait.AITMod;
import loqor.ait.core.AITBlocks;
import loqor.ait.tardis.TardisDesktopSchema;
import loqor.ait.tardis.util.Corners;
import loqor.ait.tardis.util.TardisUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class DesktopGenerator {

	private final TardisDesktopSchema schema;

	public DesktopGenerator(TardisDesktopSchema schema) {
		this.schema = schema;
	}

	public BlockPos place(ServerWorld level, Corners corners) {

		if (this.schema == null) return null;

		Optional<StructureTemplate> optional = this.schema.findTemplate();

		if (optional.isPresent()) {
			StructureTemplate template = optional.get();

			template.place(level, BlockPos.ofFloored(corners.getBox().getCenter())/*centreTemplateAtCentre(centreTemplate(template), centreCorners(corners))*/, BlockPos.ofFloored(corners.getBox().getCenter()), new StructurePlacementData(), level.getRandom(), Block.NO_REDRAW);
			return TardisUtil.findBlockInTemplate(template, BlockPos.ofFloored(corners.getBox().getCenter()), Direction.SOUTH, AITBlocks.DOOR_BLOCK);
		}

		AITMod.LOGGER.error("Couldn't find interior structure {}!", this.schema.id());
		return null;
	}

	public static BlockPos centreTemplateAtCentre(BlockPos template, BlockPos centrePos) {
		return new BlockPos(centrePos.getX() - template.getX(), centrePos.getY() - template.getY(), centrePos.getZ() - template.getZ());
	}

	public static BlockPos centreTemplate(StructureTemplate template) {
		int x = template.getSize().getX();
		int y = template.getSize().getY();
		int z = template.getSize().getZ();
		int xDiv = x / 2;
		int yDiv = y / 2; // remember, these 3 are the LENGTH of the vector in that direction divided by 2 to get the centre of the entire vectored template.
		int zDiv = z / 2;

		return new BlockPos(xDiv, yDiv, zDiv);
	}

	public static void clearArea(ServerWorld level, Corners interiorCorners) {
		// @TODO: Just delete the chunks instead of doing this
		for (BlockPos pos : BlockPos.iterate(interiorCorners.getFirst().add(0, -64, 0), interiorCorners.getSecond().add(0, 256, 0))) {
			level.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NO_REDRAW);
		}
//        for (ItemEntity entity : level.getEntitiesByType(EntityType.ITEM/*TardisUtil.getPlayerInsideInterior(interiorCorners)*/, /*interiorCorners.getBox()*/EntityPredicates.EXCEPT_SPECTATOR)) {
//            if (TardisUtil.inBox(interiorCorners.getBox(), entity.getBlockPos())) {
//                entity.kill();
//            }
//        }
	}
}
