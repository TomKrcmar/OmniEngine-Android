package com.oe.general;

public class Color
{
	public float r, g, b, a;

	public static final Color RED		= new Color(1   , 0   , 0   );
	public static final Color YELLOW	= new Color(1   , 1   , 0   );
	public static final Color GREEN		= new Color(0   , 1   , 0   );
	public static final Color CYAN		= new Color(0   , 1   , 1   );
	public static final Color BLUE		= new Color(0   , 0   , 1   );
	public static final Color MAGENTA	= new Color(1   , 0   , 1   );
	
	public static final Color ORANGE		= new Color(1   , 0.5f, 0   );
	public static final Color YELLOW_GREEN	= new Color(0.5f, 1   , 0   );
	public static final Color GREEN_CYAN	= new Color(0   , 1   , 0.5f);
	public static final Color CYAN_BLUE		= new Color(0   , 0.5f, 1   );
	public static final Color BLUE_MAGENTA	= new Color(0.5f, 0   , 1   );
	public static final Color MAGENTA_RED	= new Color(1   , 0   , 0.5f);
	
	public static final Color BLACK	= new Color(0   );
	public static final Color GREY	= new Color(0.5f);
	public static final Color WHITE	= new Color(1   );
	
	public Color() {
		r = g = b = 0.0f;
		a = 1.0f;
	}
	public Color(float grey) {
		r = g = b = grey;
		a = 1.0f;
	}
	public Color(float grey, float alpha) {
		r = g = b = grey;
		a = alpha;
	}
	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		a = 1.0f;
	}
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}
