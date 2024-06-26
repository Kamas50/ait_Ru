package loqor.ait.tardis.console.variant.copper;

import loqor.ait.AITMod;
import loqor.ait.tardis.console.type.CopperType;
import loqor.ait.tardis.console.variant.ConsoleVariantSchema;
import net.minecraft.util.Identifier;

public class CopperVariant extends ConsoleVariantSchema {
	public static final Identifier REFERENCE = new Identifier(AITMod.MOD_ID, "console/copper");

	public CopperVariant() {
		super(CopperType.REFERENCE, REFERENCE);
	}
}
