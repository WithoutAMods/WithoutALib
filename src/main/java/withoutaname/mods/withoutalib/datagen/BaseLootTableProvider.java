package withoutaname.mods.withoutalib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
	
	/**
	 * @param blockItem item which should drop if the corresponding block is destroyed
	 */
	protected void createStandardTable(BlockItem blockItem) {
		createStandardTable(blockItem.getBlock(), blockItem);
	}
	
	/**
	 * @param block block which should drop the given loot
	 * @param loot  loot of the block
	 */
	protected void createStandardTable(Block block, ItemLike loot) {
		lootTables.put(block, getStandardLootTable(getStandardLootPool(block.getRegistryName().toString(), getStandardItemLootEntry(loot))));
	}
	
	protected LootTable.Builder getStandardLootTable(LootPool.Builder lootPoolIn) {
		return LootTable.lootTable().withPool(lootPoolIn);
	}
	
	protected LootPool.Builder getStandardLootPool(String name, LootPoolEntryContainer.Builder<?> entriesBuilder) {
		return LootPool.lootPool()
				.name(name)
				.setRolls(ConstantValue.exactly(1))
				.add(entriesBuilder)
				.when(ExplosionCondition.survivesExplosion());
	}
	
	protected LootPoolSingletonContainer.Builder<?> getStandardItemLootEntry(ItemLike itemIn) {
		return LootItem.lootTableItem(itemIn);
	}
	
	@Override
	public void run(@Nonnull HashCache cache) {
		addTables();
		
		Map<ResourceLocation, LootTable> tables = new HashMap<>();
		for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
		}
		writeTables(cache, tables);
	}
	
	private void writeTables(HashCache cache, @Nonnull Map<ResourceLocation, LootTable> tables) {
		Path outputFolder = this.generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try {
				DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
			} catch (IOException e) {
				LOGGER.error("Couldn't write loot table {}", path, e);
			}
		});
	}
	
	@Nonnull
	@Override
	public String getName() {
		return "LootTables";
	}
	
}
