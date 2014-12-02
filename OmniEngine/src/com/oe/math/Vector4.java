package com.oe.math;

public class Vector4 {
	public float x, y, z, w;

	public static final Vector4 ONE		= new Vector4(1.0f);
	public static final Vector4 ZERO	= new Vector4(0.0f);
	
	public Vector4() {
		x = y = z = w = 0.0f;
	}
	public Vector4(Vector4 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}
	public Vector4(float value) {
		x = y = z = w = value;
	}
	public Vector4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public float length() {
		return (float) Math.sqrt(x*x+y*y+z*z+w*w);
	}
	public float length2() {
		return (x*x+y*y+z*z+w*w);
	}
	public void normalize() {
		float len = length();
		if (len > 0.0) {
			x /= len;
			y /= len;
			z /= len;
			w /= len;
		}
	}
	public Vector4 getNormal() {
		float len = length();
		if (len > 0.0)
			return new Vector4(x / len, y / len, z / len, w / len);
		return Vector4.ZERO;
	}

	public Vector4 add(Vector4 v) {return new Vector4(x + v.x, y + v.y, z + v.z, w + v.w);}
	public Vector4 sub(Vector4 v) {return new Vector4(x - v.x, y - v.y, z - v.z, w + v.w);}
	public Vector4 mul(Vector4 v) {return new Vector4(x * v.x, y * v.y, z * v.z, w + v.w);}
	public Vector4 div(Vector4 v) {return new Vector4(x / v.x, y / v.y, z / v.z, w + v.w);}
	public Vector4 add(float v) {return new Vector4(x + v, y + v, z + v, w + v);}
	public Vector4 sub(float v) {return new Vector4(x - v, y - v, z - v, w + v);}
	public Vector4 mul(float v) {return new Vector4(x * v, y * v, z * v, w + v);}
	public Vector4 div(float v) {return new Vector4(x / v, y / v, z / v, w + v);}
	
	/*public void add(Vector3 v) {x += v.x; y += v.y; z += v.z;}
	public void sub(Vector3 v) {x -= v.x; y -= v.y; z -= v.z;}
	public void mul(Vector3 v) {x *= v.x; y *= v.y; z *= v.z;}
	public void div(Vector3 v) {x /= v.x; y /= v.y; z /= v.z;}
	public void add(double d) {x += d; y += d; z += d;}
	public void sub(double d) {x -= d; y -= d; z -= d;}
	public void mul(double d) {x *= d; y *= d; z *= d;}
	public void div(double d) {x /= d; y /= d; z /= d;}*/
}
