package withoutaname.mods.withoutalib.datagen.loot.conditions;

import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import withoutaname.mods.withoutalib.WithoutALib;

public class NbtCondition implements LootItemCondition {
	
	public static final LootItemConditionType LOOT_CONDITION_TYPE = register();
	
	private final NbtProvider nbtProvider;
	private final NbtPredicate nbtPredicate;
	
	private NbtCondition(NbtProvider nbtProvider, NbtPredicate nbtPredicate) {
		this.nbtProvider = nbtProvider;
		this.nbtPredicate = nbtPredicate;
	}
	
	@Nonnull
	private static LootItemConditionType register() {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(WithoutALib.MODID, "nbt_condition"), new LootItemConditionType(new NbtConditionSerializer()));
	}
	
	public static void init() {
	}
	
	public static LootItemCondition.Builder create(NbtProvider nbtProvider, NbtPredicate predicate) {
		return () -> new NbtCondition(nbtProvider, predicate);
	}
	
	public static LootItemCondition.Builder create(LootContext.EntityTarget entityTarget, NbtPredicate predicate) {
		return () -> new NbtCondition(ContextNbtProvider.forContextEntity(entityTarget), predicate);
	}
	
	
	@Nonnull
	@Override
	public LootItemConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}
	
	@Override
	public boolean test(LootContext lootContext) {
		Tag nbt = this.nbtProvider.get(lootContext);
		return this.nbtPredicate.matches(nbt);
	}
	
	public static class NbtConditionSerializer implements Serializer<NbtCondition> {
		
		@Override
		public void serialize(@Nonnull JsonObject jsonObject, @Nonnull NbtCondition nbtCondition, @Nonnull JsonSerializationContext serializationContext) {
			jsonObject.add("source", serializationContext.serialize(nbtCondition.nbtProvider));
			jsonObject.add("nbt_predicate", nbtCondition.nbtPredicate.serializeToJson());
		}
		
		@Nonnull
		@Override
		public NbtCondition deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext deserializationContext) {
			NbtProvider nbtProvider = GsonHelper.getAsObject(jsonObject, "source", deserializationContext, NbtProvider.class);
			NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt_predicate"));
			return new NbtCondition(nbtProvider, nbtPredicate);
		}
		
	}
	
}
