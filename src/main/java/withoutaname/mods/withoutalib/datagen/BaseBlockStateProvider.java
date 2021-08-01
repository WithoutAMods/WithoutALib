package withoutaname.mods.withoutalib.datagen;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Function;

public abstract class BaseBlockStateProvider extends BlockStateProvider {
	
	public BaseBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
	}
	
	/**
	 * @return new Vector3f(x, y, z)
	 */
	protected static Vector3f v(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}
	
	protected void addCube(@Nonnull ModelBuilder<?> builder, @Nonnull Vector3f from, @Nonnull Vector3f to, Function<Direction, String> textures) {
		ModelBuilder<?>.ElementBuilder elementBuilder = builder
				.element()
				.from(from.x(), from.y(), from.z())
				.to(to.x(), to.y(), to.z());
		
		for (Direction dir : Direction.values()) {
			final String texture = textures.apply(dir);
			if (texture != null) {
				final Vector3f v = dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? from : to;
				
				elementBuilder.face(dir)
						.texture(texture)
						.cullface(switch (dir.getAxis()) {
							case X -> v.x();
							case Y -> v.y();
							case Z -> v.z();
						} == (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 0 : 16) ? dir : null);
			}
		}
	}
	
}
