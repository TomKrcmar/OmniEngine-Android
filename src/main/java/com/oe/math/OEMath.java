package com.oe.math;

import com.oe.general.Color;

public class OEMath
{
	public static final float PI = 3.14159265358979323846264338327950f;
	public static final float PI_BY_90 = PI / 90.0f;
	public static final float PI_BY_180 = PI / 180.0f;
	public static final float PI_BY_360 = PI / 360.0f;
	public static final float TWO_PI = PI * 2.0f;

	public static final float DEG_TO_RAD = PI / 180.0f;
	public static final float RAD_TO_DEG = 180.0f / PI;
	
	public static float linInterp(float min, float max, float x) {
		return min + (max - min) * x;
	}
	public static float linInterpInv(float min, float max, float value) {
		return (value - min) / (max - min);
	}
	public static float clamp(float x, float min, float max) {
		return (x < min ? min : (x > max ? max : x));
	}
	
	public static Color linInterp(Color min, Color max, float x) {
		return new Color(
			min.r + (max.r - min.r) * x,
			min.g + (max.g - min.g) * x,
			min.b + (max.b - min.b) * x,
			min.a + (max.a - min.a) * x);
	}
}
