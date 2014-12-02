package com.oe.math;

public class Vector3 {
	public float x, y, z;

	public static final Vector3 ONE		= new Vector3(1.0f);
	public static final Vector3 ZERO	= new Vector3(0.0f);
	public static final Vector3 RIGHT	= new Vector3( 1.0f,  0.0f,  0.0f);
	public static final Vector3 LEFT	= new Vector3(-1.0f,  0.0f,  0.0f);
	public static final Vector3 UP		= new Vector3( 0.0f,  1.0f,  0.0f);
	public static final Vector3 DOWN	= new Vector3( 0.0f, -1.0f,  0.0f);
	public static final Vector3 BACK	= new Vector3( 0.0f,  0.0f,  1.0f);
	public static final Vector3 FORWARD	= new Vector3( 0.0f,  0.0f, -1.0f);
	
	public Vector3() {
		x = y = z = 0.0f;
	}
	public Vector3(Vector3 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	public Vector3(float value) {
		x = y = z = value;
	}
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void set(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public float length() {
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	public float length2() {
		return (x*x+y*y+z*z);
	}
	public void normalize() {
		float len = length();
		if (len > 0.0) {
			x /= len;
			y /= len;
			z /= len;
		}
	}
	public Vector3 getNormal() {
		float len = length();
		if (len > 0.0)
			return new Vector3(x / len, y / len, z / len);
		return Vector3.ZERO;
	}
	public float distance(Vector3 pos) {
		float dx = x - pos.x;
		float dy = y - pos.y;
		float dz = z - pos.z;
		float d2 = dx*dx + dy*dy + dz*dz;
		return (float) Math.sqrt(d2);
	}
	public float distance2(Vector3 pos) {
		float dx = x - pos.x;
		float dy = y - pos.y;
		float dz = z - pos.z;
		float d2 = dx*dx + dy*dy + dz*dz;
		return d2;
	}

	public Vector3 add(Vector3 v) {return new Vector3(x + v.x, y + v.y, z + v.z);}
	public Vector3 sub(Vector3 v) {return new Vector3(x - v.x, y - v.y, z - v.z);}
	public Vector3 mul(Vector3 v) {return new Vector3(x * v.x, y * v.y, z * v.z);}
	public Vector3 div(Vector3 v) {return new Vector3(x / v.x, y / v.y, z / v.z);}
	public Vector3 add(float v) {return new Vector3(x + v, y + v, z + v);}
	public Vector3 sub(float v) {return new Vector3(x - v, y - v, z - v);}
	public Vector3 mul(float v) {return new Vector3(x * v, y * v, z * v);}
	public Vector3 div(float v) {return new Vector3(x / v, y / v, z / v);}
	
	public void addBy(Vector3 v) {x += v.x; y += v.y; z += v.z;}
	public void subBy(Vector3 v) {x -= v.x; y -= v.y; z -= v.z;}
	public void mulBy(Vector3 v) {x *= v.x; y *= v.y; z *= v.z;}
	public void addBy(float v) {x += v; y += v; z += v;}
	public void mulBy(float v) {x *= v; y *= v; z *= v;}
	
	/*public void add(Vector3 v) {x += v.x; y += v.y; z += v.z;}
	public void sub(Vector3 v) {x -= v.x; y -= v.y; z -= v.z;}
	public void mul(Vector3 v) {x *= v.x; y *= v.y; z *= v.z;}
	public void div(Vector3 v) {x /= v.x; y /= v.y; z /= v.z;}
	public void add(double d) {x += d; y += d; z += d;}
	public void sub(double d) {x -= d; y -= d; z -= d;}
	public void mul(double d) {x *= d; y *= d; z *= d;}
	public void div(double d) {x /= d; y /= d; z /= d;}*/
}
