package com.oe.rendering;

import java.nio.ByteBuffer;

import com.oe.general.Color;
import com.oe.math.Vector2;
import com.oe.math.Vector3;

public class VertexAttribute
{
	public enum Preset {
		NONE		(Type.FLOAT,		1, 4),
		POSITION	(Type.FLOAT,		3, 0),
		NORMAL		(Type.FLOAT,		3, 1),
		TEXCOORD	(Type.FLOAT,		2, 2),
		COLOR		(Type.UNSIGNED_BYTE,4, 3);
		
		public final Type mType;
		public final int mNumComponents;
		public final int mIndex;
		
		private Preset(Type type, int numComponents, int index) {
			mType = type;
			mNumComponents = numComponents;
			mIndex = index;
		}
	}
	public enum Type {
		FLOAT(4),
		INT(4),
		UNSIGNED_BYTE(1);
		
		public final int mDataSize;
		
		private Type(int dataSize) {
			mDataSize = dataSize;
		}
	}
	
	public static final VertexAttribute POSITION = new VertexAttribute(Preset.POSITION);
	public static final VertexAttribute NORMAL = new VertexAttribute(Preset.NORMAL);
	public static final VertexAttribute TEXCOORD = new VertexAttribute(Preset.TEXCOORD);
	public static final VertexAttribute COLOR = new VertexAttribute(Preset.COLOR);
	
	private final Preset mPreset;
	private final Type mType;
	private final int mNumComponents;
	private final int mCustomIndex;
	
	public VertexAttribute(Preset preset) {
		mPreset = preset;
		mType = mPreset.mType;
		mNumComponents = mPreset.mNumComponents;
		mCustomIndex = 0;
	}
	public VertexAttribute(int numComponents, Type type, int customIndex) {
		mPreset = Preset.NONE;
		mNumComponents = numComponents;
		mType = type;
		mCustomIndex = customIndex;
	}

	public Preset getPreset() {
		return mPreset;
	}
	public Type getType() {
		return mType;
	}
	public int getNumComponents() {
		return mNumComponents;
	}
	public int getDataSize() {
		return mNumComponents * mType.mDataSize;
	}
	public int getCustomIndex() {
		return mCustomIndex;
	}
	
	public static void putVec2(ByteBuffer buffer, Vector2 v) {
		buffer.putFloat(v.x);
		buffer.putFloat(v.y);
	}
	public static void putVec3(ByteBuffer buffer, Vector3 v) {
		buffer.putFloat(v.x);
		buffer.putFloat(v.y);
		buffer.putFloat(v.z);
	}
	public static void putColor(ByteBuffer buffer, Color c) {
		buffer.put((byte)Math.round(c.r * 255.0));
		buffer.put((byte)Math.round(c.g * 255.0));
		buffer.put((byte)Math.round(c.b * 255.0));
		buffer.put((byte)Math.round(c.a * 255.0));
	}
}
