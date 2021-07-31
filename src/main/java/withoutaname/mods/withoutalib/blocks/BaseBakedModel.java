package withoutaname.mods.withoutalib.blocks;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import withoutaname.mods.withoutalib.tools.VectorUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseBakedModel implements IDynamicBakedModel {
	
	/**
	 * @return new Vector3f(x, y, z)
	 */
	protected static Vector3f v(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}
	
	protected void putVertex(BakedQuadBuilder builder, Vector3f normal,
							 double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
		putVertex(builder, normal, x, y, z, u, v, sprite, 1.0f, 1.0f, 1.0f);
	}
	
	protected void putVertex(@Nonnull BakedQuadBuilder builder, Vector3f normal,
							 double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {
		
		ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
		for (int j = 0; j < elements.size(); j++) {
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
						case 0 -> {
							float iu = sprite.getU(u);
							float iv = sprite.getV(v);
							builder.put(j, iu, iv);
						}
						case 2 -> builder.put(j, (short) 0, (short) 0);
						default -> builder.put(j);
					}
					break;
				case NORMAL:
					builder.put(j, normal.x(), normal.y(), normal.z());
					break;
				default:
					builder.put(j);
					break;
			}
		}
	}
	
	protected BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, TextureAtlasSprite sprite) {
		return createQuad(v1, v2, v3, v4, sprite, false);
	}
	
	protected BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, @Nonnull TextureAtlasSprite sprite, boolean reversed) {
		int u = sprite.getWidth();
		int v = sprite.getHeight();
		
		return createQuad(v1, v2, v3, v4, 0, 0, u, v, sprite, reversed);
	}
	
	protected BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, float uFrom, float vFrom, float uTo, float vTo, TextureAtlasSprite sprite) {
		return createQuad(v1, v2, v3, v4, uFrom, vFrom, uTo, vTo, sprite, false);
	}
	
	protected BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, float uFrom, float uTo, float vFrom, float vTo, TextureAtlasSprite sprite, boolean reversed) {
		Vector3f normal = reversed ?
				VectorUtils.cross(VectorUtils.subtract(v3, v1), VectorUtils.subtract(v2, v1)) :
				VectorUtils.cross(VectorUtils.subtract(v3, v2), VectorUtils.subtract(v1, v2));
		
		if (reversed) {
			Vector3f v0;
			v0 = v1;
			v1 = v4;
			v4 = v0;
			v0 = v2;
			v2 = v3;
			v3 = v0;
		}
		
		BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
		builder.setQuadOrientation(Direction.getNearest(normal.x(), normal.y(), normal.z()));
		putVertex(builder, normal, v1.x(), v1.y(), v1.z(), reversed ? uTo : uFrom, vFrom, sprite);
		putVertex(builder, normal, v2.x(), v2.y(), v2.z(), reversed ? uTo : uFrom, vTo, sprite);
		putVertex(builder, normal, v3.x(), v3.y(), v3.z(), reversed ? uFrom : uTo, vTo, sprite);
		putVertex(builder, normal, v4.x(), v4.y(), v4.z(), reversed ? uFrom : uTo, vFrom, sprite);
		
		return builder.build();
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from      first corner
	 * @param to        second corner
	 * @param allFaces  texture for all faces
	 * @param dynamicUV if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(Vector3f from, Vector3f to, TextureAtlasSprite allFaces, boolean dynamicUV) {
		return createCube(from, to, allFaces, dynamicUV, false);
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from         first corner
	 * @param to           second corner
	 * @param allFaces     texture for all faces
	 * @param dynamicUV    if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @param withReversed whether the sides also should be rendered from the inside of the cube
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(Vector3f from, Vector3f to, TextureAtlasSprite allFaces, boolean dynamicUV, boolean withReversed) {
		return createCube(from, to, allFaces, allFaces, allFaces, allFaces, allFaces, allFaces, dynamicUV, withReversed);
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from      first corner
	 * @param to        second corner
	 * @param up        texture for the top of the cube
	 * @param down      texture for the bottom of the cube
	 * @param north     texture for the north side of the cube
	 * @param south     texture for the south side of the cube
	 * @param east      texture for the east side of the cube
	 * @param west      texture for the west side of the cube
	 * @param dynamicUV if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(Vector3f from, Vector3f to, TextureAtlasSprite up, TextureAtlasSprite down, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite east, TextureAtlasSprite west, boolean dynamicUV) {
		return createCube(from, to, up, down, north, south, east, west, dynamicUV, false);
	}
	
	/**
	 * creates quads for a full cube
	 *
	 * @param from         first corner
	 * @param to           second corner
	 * @param up        texture for the top of the cube
	 * @param down      texture for the bottom of the cube
	 * @param north     texture for the north side of the cube
	 * @param south     texture for the south side of the cube
	 * @param east      texture for the east side of the cube
	 * @param west      texture for the west side of the cube
	 * @param dynamicUV    if true the uv is calculated from the position of the cube, otherwise the complete texture is always rendered
	 * @param withReversed whether the sides also should be rendered from the inside of the cube
	 * @return List of the quads of this cube
	 */
	protected List<BakedQuad> createCube(@Nonnull Vector3f from, @Nonnull Vector3f to, TextureAtlasSprite up, TextureAtlasSprite down, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite east, TextureAtlasSprite west, boolean dynamicUV, boolean withReversed) {
		List<BakedQuad> quads = new ArrayList<>();
		
		float fx = from.x();
		float fy = from.y();
		float fz = from.z();
		float tx = to.x();
		float ty = to.y();
		float tz = to.z();
		
		quads.add(createQuad(v(fx, ty, fz), v(fx, ty, tz), v(tx, ty, tz), v(tx, ty, fz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), up));
		quads.add(createQuad(v(tx, fy, fz), v(tx, fy, tz), v(fx, fy, tz), v(fx, fy, fz), getUFrom(dynamicUV, 1f - tx), getUTo(dynamicUV, 1f - fx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), down));
		quads.add(createQuad(v(tx, ty, fz), v(tx, fy, fz), v(fx, fy, fz), v(fx, ty, fz), getUFrom(dynamicUV, 1f - tx), getUTo(dynamicUV, 1f - fx), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), north));
		quads.add(createQuad(v(fx, ty, tz), v(fx, fy, tz), v(tx, fy, tz), v(tx, ty, tz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), south));
		quads.add(createQuad(v(tx, ty, tz), v(tx, fy, tz), v(tx, fy, fz), v(tx, ty, fz), getUFrom(dynamicUV, 1f - tz), getUTo(dynamicUV, 1f - fz), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), east));
		quads.add(createQuad(v(fx, ty, fz), v(fx, fy, fz), v(fx, fy, tz), v(fx, ty, tz), getUFrom(dynamicUV, fz), getUTo(dynamicUV, tz), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), west));
		
		if (withReversed) {
			
			quads.add(createQuad(v(fx, ty, fz), v(fx, ty, tz), v(tx, ty, tz), v(tx, ty, fz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), up, true));
			quads.add(createQuad(v(tx, fy, fz), v(tx, fy, tz), v(fx, fy, tz), v(fx, fy, fz), getUFrom(dynamicUV, 1f - tx), getUTo(dynamicUV, 1f - fx), getVFrom(dynamicUV, fz), getVTo(dynamicUV, tz), down, true));
			quads.add(createQuad(v(tx, ty, fz), v(tx, fy, fz), v(fx, fy, fz), v(fx, ty, fz), getUFrom(dynamicUV, 1f - tx), getUTo(dynamicUV, 1f - fx), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), north, true));
			quads.add(createQuad(v(fx, ty, tz), v(fx, fy, tz), v(tx, fy, tz), v(tx, ty, tz), getUFrom(dynamicUV, fx), getUTo(dynamicUV, tx), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), south, true));
			quads.add(createQuad(v(tx, ty, tz), v(tx, fy, tz), v(tx, fy, fz), v(tx, ty, fz), getUFrom(dynamicUV, 1f - tz), getUTo(dynamicUV, 1f - fz), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), east, true));
			quads.add(createQuad(v(fx, ty, fz), v(fx, fy, fz), v(fx, fy, tz), v(fx, ty, tz), getUFrom(dynamicUV, fz), getUTo(dynamicUV, tz), getVFrom(dynamicUV, 1f - ty), getVTo(dynamicUV, 1f - fy), west, true));
			
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
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}
	
}
