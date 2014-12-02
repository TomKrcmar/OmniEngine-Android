package com.oe.math;

public class Vector2 {
	public float x, y;

	public static final Vector2 ONE		= new Vector2(1.0f);
	public static final Vector2 ZERO	= new Vector2(0.0f);
	public static final Vector2 RIGHT	= new Vector2( 1.0f,  0.0f);
	public static final Vector2 LEFT	= new Vector2(-1.0f,  0.0f);
	public static final Vector2 UP		= new Vector2( 0.0f,  1.0f);
	public static final Vector2 DOWN	= new Vector2( 0.0f, -1.0f);
	
	public Vector2() {
		x = y = 0.0f;
	}
	public Vector2(Vector2 v) {
		x = v.x;
		y = v.y;
	}
	public Vector2(float value) {
		x = y = value;
	}
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public void set(Vector2 v) {
		x = v.x;
		y = v.y;
	}
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2 add(Vector2 v) {return new Vector2(x + v.x, y + v.y);}
	public Vector2 sub(Vector2 v) {return new Vector2(x - v.x, y - v.y);}
	public Vector2 mul(Vector2 v) {return new Vector2(x * v.x, y * v.y);}
	public Vector2 div(Vector2 v) {return new Vector2(x / v.x, y / v.y);}
	public Vector2 add(float v) {return new Vector2(x + v, y + v);}
	public Vector2 sub(float v) {return new Vector2(x - v, y - v);}
	public Vector2 mul(float v) {return new Vector2(x * v, y * v);}
	public Vector2 div(float v) {return new Vector2(x / v, y / v);}
	
	public void addBy(Vector2 v) {x += v.x; y += v.y;}
	public void mulBy(Vector2 v) {x *= v.x; y *= v.y;}
	public void addBy(float v) {x += v; y += v;}
	public void mulBy(float v) {x *= v; y *= v;}
}
