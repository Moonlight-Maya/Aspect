package io.github.moonlightmaya.util;

import net.minecraft.util.math.MathHelper;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.util.HashMap;
import java.util.Map;

public class ColorUtils {
    private static final Map<String, Vector3f> FUN_COLORS = new HashMap<>() {{
        try {
            putAliased(this, parseColor("#FF02AD"), "fran", "francielly", "bunny");
            putAliased(this, parseColor("#A672EF"), "chloe", "space");
            putAliased(this, parseColor("#00F0FF"), "maya", "limits");
            putAliased(this, parseColor("#99BBEE"), "skye", "sky", "skylar");
            putAliased(this, parseColor("#FF2400"), "lily", "foxes", "fox");
            putAliased(this, parseColor("#FFECD2"), "kiri");
            putAliased(this, parseColor("#A155DA"), "luna", "moff", "moth");
        } catch (Exception impossible) {impossible.printStackTrace(); throw new IllegalStateException("impossible");}
    }};

    private static <K, V> void putAliased(Map<K, V> map, V v, K... ks) {
        for (K k : ks) map.put(k, v);
    }

    public static Vector3f parseColor(String color) throws IllegalArgumentException {
        if (color == null) return new Vector3f(1,1,1);
        if (color.startsWith("#")) {
            if (color.length() == 4) {
                int r = Integer.parseInt(color.substring(1,2), 16);
                int g = Integer.parseInt(color.substring(2,3), 16);
                int b = Integer.parseInt(color.substring(3,4), 16);
                return new Vector3f(r / 15f, g / 15f, b / 15f);
            }
            if (color.length() == 7) {
                int r = Integer.parseInt(color.substring(1,3), 16);
                int g = Integer.parseInt(color.substring(3,5), 16);
                int b = Integer.parseInt(color.substring(5,7), 16);
                return new Vector3f(r / 255f, g / 255f, b / 255f);
            }
            throw new IllegalArgumentException("Invalid hex string: expected #RGB or #RRGGBB");
        }
        for (String key : FUN_COLORS.keySet()) {
            if (key.equalsIgnoreCase(color)) return new Vector3f(FUN_COLORS.get(key));
        }
        return new Vector3f(1, 1, 1);
    }

    //Conversion with vectors/doubles and ints
    //All vectors are RGBA or RGB
    //Minecraft int colors are (generally) stored as ARGB in ints

    /**
     * ARGB Functions, most minecraft ints are in this form
     */
    public static Vector4d intARGBToVec(int colorARGB) {
        return new Vector4d(
                ((colorARGB >> 16) & 0xff) / 255.0,
                ((colorARGB >> 8) & 0xff) / 255.0,
                ((colorARGB >> 0) & 0xff) / 255.0,
                ((colorARGB >> 24) & 0xff) / 255.0
        );
    }
    public static int VecToIntARGB(double r, double g, double b, double a) {
        return
                MathHelper.clamp((int) (r * 255), 0, 255) << 16 |
                MathHelper.clamp((int) (g * 255), 0, 255) << 8 |
                MathHelper.clamp((int) (b * 255), 0, 255) << 0 |
                MathHelper.clamp((int) (a * 255), 0, 255) << 24;
    }
    public static int VecToIntARGB(Vector3d rgb) {
        return VecToIntARGB(rgb.x, rgb.y, rgb.z, 0);
    }

    public static int VecToIntARGB(Vector4d rgba) {
        return VecToIntARGB(rgba.x, rgba.y, rgba.z, rgba.w);
    }

    /**
     * ABGR functions, this is useful for textures
     */
    public static Vector4d intABGRToVec(int colorABGR) {
        return new Vector4d(
                ((colorABGR >> 0) & 0xff) / 255.0,
                ((colorABGR >> 8) & 0xff) / 255.0,
                ((colorABGR >> 16) & 0xff) / 255.0,
                ((colorABGR >> 24) & 0xff) / 255.0
        );
    }
    public static Vector4d intABGRToVec(int colorABGR, Vector4d out) {
        out.set(
                ((colorABGR >> 0) & 0xff) / 255.0,
                ((colorABGR >> 8) & 0xff) / 255.0,
                ((colorABGR >> 16) & 0xff) / 255.0,
                ((colorABGR >> 24) & 0xff) / 255.0
        );
        return out;
    }
    public static int VecToIntABGR(double r, double g, double b, double a) {
        return
                MathHelper.clamp((int) (r * 255), 0, 255) << 0 |
                MathHelper.clamp((int) (g * 255), 0, 255) << 8 |
                MathHelper.clamp((int) (b * 255), 0, 255) << 16 |
                MathHelper.clamp((int) (a * 255), 0, 255) << 24;
    }
    public static int VecToIntABGR(Vector4d rgba) {
        return VecToIntABGR(rgba.x, rgba.y, rgba.z, rgba.w);
    }




}
