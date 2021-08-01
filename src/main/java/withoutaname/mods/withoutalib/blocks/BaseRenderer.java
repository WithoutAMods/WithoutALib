package withoutaname.mods.withoutalib.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public abstract class BaseRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	
	protected static final float[] WHITE = {1.0f, 1.0f, 1.0f, 1.0f};
	
	/**
	 * @return new Vector3f(x, y, z)
	 */
	protected static Vector3f v(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}
	
	protected void add(VertexConsumer builder, PoseStack stack,
					   float x, float y, float z, float u, float v) {
		add(builder, stack, x, y, z, u, v, WHITE);
	}
	
	protected void add(@Nonnull VertexConsumer builder, @Nonnull PoseStack stack,
					   float x, float y, float z, float u, float v, @Nonnull float[] color) {
		builder.vertex(stack.last().pose(), x, y, z)
				.color(color[0], color[1], color[2], color.length > 3 ? color[3] : 1.0f)
				.uv(u, v)
				.uv2(0, 240)
				.normal(0, 1, 0)
				.endVertex();
	}
	
	protected void addFace(@Nonnull VertexConsumer builder, @Nonnull PoseStack stack,
						   @Nonnull Vector3f v1, @Nonnull Vector3f v2, @Nonnull Vector3f v3, @Nonnull Vector3f v4,
						   @Nonnull TextureAtlasSprite sprite) {
		addFace(builder, stack, v1, v2, v3, v4, sprite, WHITE);
	}
	
	protected void addFace(@Nonnull VertexConsumer builder, @Nonnull PoseStack stack,
						   @Nonnull Vector3f v1, @Nonnull Vector3f v2, @Nonnull Vector3f v3, @Nonnull Vector3f v4,
						   @Nonnull TextureAtlasSprite sprite, @Nonnull float[] color) {
		addFace(builder, stack, v1, v2, v3, v4, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), color);
	}
	
	protected void addFace(@Nonnull VertexConsumer builder, @Nonnull PoseStack stack,
						   @Nonnull Vector3f v1, @Nonnull Vector3f v2, @Nonnull Vector3f v3, @Nonnull Vector3f v4,
						   float uFrom, float uTo, float vFrom, float vTo) {
		addFace(builder, stack, v1, v2, v3, v4, uFrom, uTo, vFrom, vTo, WHITE);
	}
	
	protected void addFace(@Nonnull VertexConsumer builder, @Nonnull PoseStack stack,
						   @Nonnull Vector3f v1, @Nonnull Vector3f v2, @Nonnull Vector3f v3, @Nonnull Vector3f v4,
						   float uFrom, float uTo, float vFrom, float vTo, @Nonnull float[] color) {
		add(builder, stack, v1.x(), v1.y(), v1.z(), uFrom, vFrom, color);
		add(builder, stack, v2.x(), v2.y(), v2.z(), uFrom, vTo, color);
		add(builder, stack, v3.x(), v3.y(), v3.z(), uTo, vTo, color);
		add(builder, stack, v4.x(), v4.y(), v4.z(), uTo, vFrom, color);
	}
}
