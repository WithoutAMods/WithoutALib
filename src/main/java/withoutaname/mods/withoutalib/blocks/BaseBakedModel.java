package withoutaname.mods.withoutalib.blocks;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

public abstract class BaseBakedModel implements IDynamicBakedModel {

	protected void putVertex(BakedQuadBuilder builder, Vector3d normal,
						   double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
		putVertex(builder, normal, x, y, z, u, v, sprite, 1.0f, 1.0f, 1.0f);
	}

	protected void putVertex(@Nonnull BakedQuadBuilder builder, Vector3d normal,
							 double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {

		ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
		for (int j = 0 ; j < elements.size() ; j++) {
			VertexFormatElement e = elements.get(j);
			switch (e.getUsage()) {
				case POSITION:
					builder.put(j, (float) x, (float) y, (float) z, 1.0f);
					break;
				case COLOR:
					builder.put(j, r, g, b, 1.0f);
					break;
				case UV:
					switch (e.getIndex()) {
						case 0:
							float iu = sprite.getU(u);
							float iv = sprite.getV(v);
							builder.put(j, iu, iv);
							break;
						case 2:
							builder.put(j, (short) 0, (short) 0);
							break;
						default:
							builder.put(j);
							break;
					}
					break;
				case NORMAL:
					builder.put(j, (float) normal.x, (float) normal.y, (float) normal.z);
					break;
				default:
					builder.put(j);
					break;
			}
		}
	}
	
	/**
	 * just as short form of "new Vector3d(x, y, z)
	 *
	 * @return new Vector3d(x, y, z)
	 */
	protected static Vector3d v(double x, double y, double z) {
		return new Vector3d(x, y, z);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, TextureAtlasSprite sprite) {
		return createQuad(v1, v2, v3, v4, sprite, false);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, @Nonnull TextureAtlasSprite sprite, boolean reversed) {
		int u = sprite.getWidth();
		int v = sprite.getHeight();

		return createQuad(v1, v2, v3, v4, 0, 0, u, v, sprite, reversed);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, float uFrom, float vFrom, float uTo, float vTo, TextureAtlasSprite sprite) {
		return createQuad(v1, v2, v3, v4, uFrom, vFrom, uTo, vTo, sprite, false);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, float uFrom, float uTo, float vFrom, float vTo, TextureAtlasSprite sprite, boolean reversed) {
		Vector3d normal = reversed ?
				v3.subtract(v1).cross(v2.subtract(v1)).normalize() :
				v3.subtract(v2).cross(v1.subtract(v2)).normalize();

		if (reversed) {
			Vector3d v0;
			v0 = v1;
			v1 = v4;
			v4 = v0;
			v0 = v2;
			v2 = v3;
			v3 = v0;
		}

		BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
		builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
		putVertex(builder, normal, v1.x, v1.y, v1.z, reversed ? uTo : uFrom, vFrom, sprite);
		putVertex(builder, normal, v2.x, v2.y, v2.z, reversed ? uTo : uFrom, vTo, sprite);
		putVertex(builder, normal, v3.x, v3.y, v3.z, reversed ? uFrom : uTo, vTo, sprite);
		putVertex(builder, normal, v4.x, v4.y, v4.z, reversed ? uFrom : uTo, vFrom, sprite);

		return builder.build();
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from first corner
	 * @param to second corner
	 * @param allFaces texture for all faces
	 * @param dynamicUV if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(Vector3d from, Vector3d to, TextureAtlasSprite allFaces, boolean dynamicUV) {
		return createCube(from, to, allFaces, dynamicUV, false);
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from first corner
	 * @param to second corner
	 * @param allFaces texture for all faces
	 * @param dynamicUV if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @param withReversed whether the sides also should be rendered from the inside of the cube
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(Vector3d from, Vector3d to, TextureAtlasSprite allFaces, boolean dynamicUV, boolean withReversed) {
		return createCube(from, to, allFaces, allFaces, allFaces, allFaces, allFaces, allFaces, dynamicUV, withReversed);
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from first corner
	 * @param to second corner
	 * @param up texture for the upper side of the cube
	 * @param down texture for the upper side of the cube
	 * @param north texture for the upper side of the cube
	 * @param south texture for the upper side of the cube
	 * @param east texture for the upper side of the cube
	 * @param west texture for the upper side of the cube
	 * @param dynamicUV if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(Vector3d from, Vector3d to, TextureAtlasSprite up, TextureAtlasSprite down, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite east, TextureAtlasSprite west, boolean dynamicUV) {
		return createCube(from, to, up, down, north, south, east, west, dynamicUV, false);
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from first corner
	 * @param to second corner
	 * @param up texture for the upper side of the cube
	 * @param down texture for the upper side of the cube
	 * @param north texture for the upper side of the cube
	 * @param south texture for the upper side of the cube
	 * @param east texture for the upper side of the cube
	 * @param west texture for the upper side of the cube
	 * @param dynamicUV if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @param withReversed whether the sides also should be rendered from the inside of the cube
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(@Nonnull Vector3d from, @Nonnull Vector3d to, TextureAtlasSprite up, TextureAtlasSprite down, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite east, TextureAtlasSprite west, boolean dynamicUV, boolean withReversed) {
		List<BakedQuad> quads = new ArrayList<>();

		double fx = from.x();
		double fy = from.y();
		double fz = from.z();
		double tx = to.x();
		double ty = to.y();
		double tz = to.z();

		quads.add(createQuad(v(fx, ty, fz), v(fx, ty, tz), v(tx, ty, tz), v(tx, ty, fz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), up));
		quads.add(createQuad(v(tx, fy, fz), v(tx, fy, tz), v(fx, fy, tz), v(fx, fy, fz), getUFrom(dynamicUV, 1f-tx), getUTo(dynamicUV, 1f-fx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), down));
		quads.add(createQuad(v(tx, ty, fz), v(tx, fy, fz), v(fx, fy, fz), v(fx, ty, fz), getUFrom(dynamicUV, 1f-tx), getUTo(dynamicUV, 1f-fx), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), north));
		quads.add(createQuad(v(fx, ty, tz), v(fx, fy, tz), v(tx, fy, tz), v(tx, ty, tz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), south));
		quads.add(createQuad(v(tx, ty, tz), v(tx, fy, tz), v(tx, fy, fz), v(tx, ty, fz), getUFrom(dynamicUV, 1f-tz), getUTo(dynamicUV, 1f-fz), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), east));
		quads.add(createQuad(v(fx, ty, fz), v(fx, fy, fz), v(fx, fy, tz), v(fx, ty, tz), getUFrom(dynamicUV, fz), getUTo(dynamicUV, tz), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), west));

		if (withReversed) {

			quads.add(createQuad(v(fx, ty, fz), v(fx, ty, tz), v(tx, ty, tz), v(tx, ty, fz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), up, true));
			quads.add(createQuad(v(tx, fy, fz), v(tx, fy, tz), v(fx, fy, tz), v(fx, fy, fz), getUFrom(dynamicUV, 1f-tx), getUTo(dynamicUV, 1f-fx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), down, true));
			quads.add(createQuad(v(tx, ty, fz), v(tx, fy, fz), v(fx, fy, fz), v(fx, ty, fz), getUFrom(dynamicUV, 1f-tx), getUTo(dynamicUV, 1f-fx), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), north, true));
			quads.add(createQuad(v(fx, ty, tz), v(fx, fy, tz), v(tx, fy, tz), v(tx, ty, tz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), south, true));
			quads.add(createQuad(v(tx, ty, tz), v(tx, fy, tz), v(tx, fy, fz), v(tx, ty, fz), getUFrom(dynamicUV, 1f-tz), getUTo(dynamicUV, 1f-fz), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), east, true));
			quads.add(createQuad(v(fx, ty, fz), v(fx, fy, fz), v(fx, fy, tz), v(fx, ty, tz), getUFrom(dynamicUV, fz), getUTo(dynamicUV, tz), getVFrom(dynamicUV, 1f-ty), getVTo(dynamicUV, 1f-fy), west, true));

		}
		return quads;
	}

	private float getUFrom(boolean dynamicUV, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? 16 * (float) d : 0;
	}

	private float getUTo(boolean dynamicUV, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? 16 * (float) d : 16;
	}

	private float getVFrom(boolean dynamicUV, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? 16 * (float) d : 0;
	}

	private float getVTo(boolean dynamicUV, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? 16 * (float) d : 16;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.EMPTY;
	}

}
