package withoutaname.mods.withoutalib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseLootTableProvider extends LootTableProvider {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;

	public BaseLootTableProvider(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
		this.generator = dataGeneratorIn;
	}

	protected abstract void addTables();

	protected void createStandardTable(BlockItem blockItem) {
		createStandardTable(blockItem.getBlock(), blockItem);
	}

	protected void createStandardTable(Block block, IItemProvider loot) {
		lootTables.put(block, getStandardLootTable(getStandardLootPool(block.getRegistryName().toString(), getStandardItemLootEntry(loot))));
	}

	protected LootTable.Builder getStandardLootTable(LootPool.Builder lootPoolIn) {
		return LootTable.lootTable().withPool(lootPoolIn);
	}

	protected LootPool.Builder getStandardLootPool(String name, LootEntry.Builder<?> entriesBuilder) {
		return LootPool.lootPool()
				.name(name)
				.setRolls(ConstantRange.exactly(1))
				.add(entriesBuilder)
				.when(SurvivesExplosion.survivesExplosion());
	}

	protected StandaloneLootEntry.Builder<?> getStandardItemLootEntry(IItemProvider itemIn) {
		return ItemLootEntry.lootTableItem(itemIn);
	}

	@Override
	public void run(DirectoryCache cache) {
		addTables();

		Map<ResourceLocation, LootTable> tables = new HashMap<>();
		for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootParameterSets.BLOCK).build());
		}
		writeTables(cache, tables);
	}

	private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
		Path outputFolder = this.generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try {
				IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
			} catch (IOException e) {
				LOGGER.error("Couldn't write loot table {}", path, e);
			}
		});
	}

	@Override
	public String getName() {
		return "LootTables";
	}

}
