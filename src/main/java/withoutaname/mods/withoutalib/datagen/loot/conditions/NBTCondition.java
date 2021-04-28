package withoutaname.mods.withoutalib.datagen.loot.conditions;

import java.util.function.Function;
import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import withoutaname.mods.withoutalib.WithoutALib;

public class NBTCondition implements ILootCondition {
	
	public static final LootConditionType LOOT_CONDITION_TYPE = register("nbt_condition", new Serializer());
	
	private static final Function<Entity, INBT> NBT_FROM_ENTITY = NBTPredicate::getEntityTagToCompare;
	private static final Function<TileEntity, INBT> NBT_FROM_TILE_ENTITY = TileEntity::serializeNBT;
	
	private final Source source;
	private final NBTPredicate nbtPredicate;
	
	private NBTCondition(Source source, NBTPredicate nbtPredicate) {
		this.source = source;
		this.nbtPredicate = nbtPredicate;
	}
	
	@Nonnull
	private static LootConditionType register(String registryName, ILootSerializer<? extends ILootCondition> serializer) {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(WithoutALib.MODID, registryName), new LootConditionType(serializer));
	}
	
	public static void init() {
	}
	
	public static NBTCondition.Builder builder(Source source) {
		return new Builder(source);
	}
	
	@Nonnull
	@Override
	public LootConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}
	
	@Override
	public boolean test(LootContext lootContext) {
		INBT nbt = this.source.nbtFunction.apply(lootContext);
		return this.nbtPredicate.matches(nbt);
	}
	
	public enum Source {
		THIS("this", LootParameters.THIS_ENTITY, NBT_FROM_ENTITY),
		KILLER("killer", LootParameters.KILLER_ENTITY, NBT_FROM_ENTITY),
		KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER, NBT_FROM_ENTITY),
		BLOCK_ENTITY("block_entity", LootParameters.BLOCK_ENTITY, NBT_FROM_TILE_ENTITY);
		
		public final String sourceName;
		public final LootParameter<?> lootParam;
		public final Function<LootContext, INBT> nbtFunction;
		
		<T> Source(String sourceName, LootParameter<T> lootParam, Function<? super T, INBT> nbtFunction) {
			this.sourceName = sourceName;
			this.lootParam = lootParam;
			this.nbtFunction = (lootContext) -> {
				T t = lootContext.getParamOrNull(lootParam);
				return t != null ? nbtFunction.apply(t) : null;
			};
		}
		
		@Nonnull
		public static Source getByName(String sourceName) {
			for (Source source : values()) {
				if (source.sourceName.equals(sourceName)) {
					return source;
				}
			}
			
			throw new IllegalArgumentException("Invalid tag source " + sourceName);
		}
	}
	
	public static class Builder implements ILootCondition.IBuilder {
		
		private final Source source;
		private NBTPredicate nbtPredicate = NBTPredicate.ANY;
		
		public Builder(Source source) {
			this.source = source;
		}
		
		public NBTCondition.Builder fromPredicate(NBTPredicate nbtPredicate) {
			this.nbtPredicate = nbtPredicate;
			return this;
		}
		
		@Nonnull
		@Override
		public ILootCondition build() {
			return new NBTCondition(this.source, this.nbtPredicate);
		}
		
	}
	
	public static class Serializer implements ILootSerializer<NBTCondition> {
		
		@Override
		public void serialize(@Nonnull JsonObject jsonObject, @Nonnull NBTCondition nbtCondition, @Nonnull JsonSerializationContext serializationContext) {
			jsonObject.addProperty("source", nbtCondition.source.sourceName);
			jsonObject.add("nbt_predicate", nbtCondition.nbtPredicate.serializeToJson());
		}
		
		@Nonnull
		@Override
		public NBTCondition deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext deserializationContext) {
			Source source = Source.getByName(JSONUtils.getAsString(jsonObject, "source"));
			NBTPredicate nbtPredicate = NBTPredicate.fromJson(jsonObject.get("nbt_predicate"));
			return new NBTCondition(source, nbtPredicate);
		}
		
	}
	
}
