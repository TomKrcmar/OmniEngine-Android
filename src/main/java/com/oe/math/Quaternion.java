package com.oe.math;

public class Quaternion
{
	private static final float ALMOST_ZERO = 0.00000001f;

	public static final Quaternion IDENTITY = new Quaternion();
	
	public float x, y, z, w;
	public Quaternion() {
		x = y = z = 0.0f;
		w = 1.0f;
	}
	public Quaternion(Quaternion q) {
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
	}
	public Quaternion(float fx, float fy, float fz, float fw) {
		x = fx; y = fy; z = fz; w = fw;
	}
	public Quaternion(float angle, Vector3 axis)
	{
		fromAxisAngle(angle, axis);
	}
	public Quaternion(Vector3 euler)
	{
		fromEuler(euler);
	}
	public void set(Quaternion q) {
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
	}
	public void set(float fx, float fy, float fz, float fw) {
		x = fx; y = fy; z = fz; w = fw;
	}
	
	public Quaternion getConjugate() {
		return new Quaternion(-x, -y, -z, w);
	}
	public void normalize() {
		float len2 = w*w + x*x + y*y + z*z;
		if (len2 > ALMOST_ZERO) {
			float len = (float) Math.sqrt(len2);
			x /= len;
			y /= len;
			z /= len;
			w /= len;
		}
	}
	public Quaternion getNormal() {
		float len2 = w*w + x*x + y*y + z*z;
		if (len2 > ALMOST_ZERO) {
			float len = (float) Math.sqrt(len2);
			return new Quaternion(
				x / len,
				y / len,
				z / len,
				w / len
			);
		}
		return new Quaternion(this);
	}
	
	public void fromAxisAngle(float angle, Vector3 axis) {
		float sinAngle;
		angle *= OEMath.PI_BY_360;
		sinAngle = (float) Math.sin(angle);
		x = (axis.x * sinAngle);
		y = (axis.y * sinAngle);
		z = (axis.z * sinAngle);
		w = (float) Math.cos(angle);
	}
	public void fromEuler(Vector3 euler) {
		float ep = euler.x * OEMath.PI_BY_90;
		float ey = euler.y * OEMath.PI_BY_90;
		float er = euler.z * OEMath.PI_BY_90;
		float sinp = (float) Math.sin(ep);
		float siny = (float) Math.sin(ey);
		float sinr = (float) Math.sin(er);
		float cosp = (float) Math.cos(ep);
		float cosy = (float) Math.cos(ey);
		float cosr = (float) Math.cos(er);
		x = sinr * cosp * cosy - cosr * sinp * siny;
		y = cosr * sinp * cosy + sinr * cosp * siny;
		z = cosr * cosp * siny - sinr * sinp * cosy;
		w = cosr * cosp * cosy + sinr * sinp * siny;
		normalize();
	}
	
	public void getAxisAngle(Vector4 axisAngle) {
		normalize();
		float scale = (float) Math.sqrt(x*x + y*y + z*z);
		if (scale > ALMOST_ZERO && Math.abs(w) <= 1.0) {
			axisAngle.x = x / scale;
			axisAngle.y = y / scale;
			axisAngle.z = z / scale;
			axisAngle.w = (float) Math.acos(w) * 2.0f * OEMath.RAD_TO_DEG;
		}
		else {
			axisAngle.x = 1.0f;
			axisAngle.y = 0.0f;
			axisAngle.z = 0.0f;
			axisAngle.w = 0.0f;
		}
	}
	public float[] getMatrix() {
		float x2 = x * x;
		float y2 = y * y;
		float z2 = z * z;
		float xy = x * y;
		float xz = x * z;
		float yz = y * z;
		float wx = w * x;
		float wy = w * y;
		float wz = w * z;
		return new float[] {
			1.0f - 2.0f * (y2 + z2),	2.0f * (xy - wz),			2.0f * (xz + wy),			0.0f,
			2.0f * (xy + wz),			1.0f - 2.0f * (x2 + z2),	2.0f * (yz - wx),			0.0f,
			2.0f * (xz - wy),			2.0f * (yz + wx),			1.0f - 2.0f * (x2 + y2),	0.0f,
			0.0f,						0.0f,						0.0f,						1.0f
		};
	}
	public Vector3 getForward() {
		float x2 = x * x;
		float y2 = y * y;
		float xz = x * z;
		float yz = y * z;
		float wx = w * x;
		float wy = w * y;
		return new Vector3(
			2.0f * (xz - wy),
			2.0f * (yz + wx),
			1.0f - 2.0f * (x2 + y2)
		);
	}
	public Vector3 getUp() {
		float x2 = x * x;
		float z2 = z * z;
		float xy = x * y;
		float yz = y * z;
		float wx = w * x;
		float wz = w * z;
		return new Vector3(
			2.0f * (xy + wz),
			1.0f - 2.0f * (x2 + z2),
			2.0f * (yz - wx)
		);
	}
	public Vector3 getRight() {
		float y2 = y * y;
		float z2 = z * z;
		float xy = x * y;
		float xz = x * z;
		float wy = w * y;
		float wz = w * z;
		return new Vector3(
			1.0f - 2.0f * (y2 + z2),
			2.0f * (xy - wz),
			2.0f * (xz + wy)
		);
	}
	
	public float dot(Quaternion q) {
		return x*q.x + y*q.y + z*q.z + w*q.w;
	}
	
	public Quaternion lerp(Quaternion q, float t) {
		return (this.add((q.sub(this)).mul(t))).getNormal();
	}
	public Quaternion slerp(Quaternion q, float t) {
		Quaternion q3;
		float d = dot(q);

		if (d < 0) {
			d = -d;
			q3 = q.getConjugate();
		}
		else {
			q3 = q;
		}
		
		if (d < 0.95f) {
			float angle = (float) Math.acos(d);
			return (this.mul((float) Math.sin(angle*(1.0f-t))).add(
					q3.mul((float) Math.sin(angle*t))))
					.div((float) Math.sin(angle));
		}
		else {
			return lerp(q3, t);
		}
	}
	
	public Quaternion add(float r) {return new Quaternion(x+r, y+r, z+r, w+r);}
	public Quaternion sub(float r) {return new Quaternion(x-r, y-r, z-r, w-r);}
	public Quaternion mul(float r) {return new Quaternion(x*r, y*r, z*r, w*r);}
	public Quaternion div(float r) {return new Quaternion(x/r, y/r, z/r, w/r);}
	public Quaternion add(Quaternion q) {return new Quaternion(x+q.x, y+q.y, z+q.z, w+q.w);}
	public Quaternion sub(Quaternion q) {return new Quaternion(x-q.x, y-q.y, z-q.z, w-q.w);}
	public Quaternion mul(Quaternion q) {
		return new Quaternion(
			w*q.x + x*q.w + y*q.z - z*q.y,
			w*q.y + y*q.w + z*q.x - x*q.z,
			w*q.z + z*q.w + x*q.y - y*q.x,
			w*q.w - x*q.x - y*q.y - z*q.z
		);
	}
	public static void mul(Quaternion result, Quaternion a, Quaternion b) {
		result.set(
			a.w*b.x + a.x*b.w + a.y*b.z - a.z*b.y,
			a.w*b.y + a.y*b.w + a.z*b.x - a.x*b.z,
			a.w*b.z + a.z*b.w + a.x*b.y - a.y*b.x,
			a.w*b.w - a.x*b.x - a.y*b.y - a.z*b.z
		);
	}
	public Vector3 mul(Vector3 v) {
		Quaternion vq = new Quaternion(v.x, v.y, v.z, 0.0f);
		Quaternion q = vq.mul(this);
		q = getConjugate().mul(q);
		return (new Vector3(q.x, q.y, q.z));
	}
}
