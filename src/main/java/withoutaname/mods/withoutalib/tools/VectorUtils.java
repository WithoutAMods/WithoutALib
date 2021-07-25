package withoutaname.mods.withoutalib.tools;

import com.mojang.math.Vector3f;

import javax.annotation.Nonnull;

public class VectorUtils {
	
	@Nonnull
	public static Vector3f subtract(@Nonnull Vector3f v0, @Nonnull Vector3f v1) {
		return new Vector3f(v0.x() - v1.x(), v0.y() - v1.y(), v0.z() - v1.z());
	}
	
	@Nonnull
	public static Vector3f cross(@Nonnull Vector3f v0, @Nonnull Vector3f v1) {
		float x0 = v0.x();
		float y0 = v0.y();
		float z0 = v0.z();
		float x1 = v1.x();
		float y1 = v1.y();
		float z1 = v1.z();
		return new Vector3f(y0 * z1 - z0 * y1, z0 * x1 - x0 * z1, x0 * y1 - y0 * x1);
	}
}
