package withoutaname.mods.withoutalib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import withoutaname.mods.withoutalib.datagen.loot.conditions.NBTCondition;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WithoutALib.MODID)
public class WithoutALib {

	public static final String MODID = "withoutalib";

	public WithoutALib() {
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		NBTCondition.init();
	}

}
