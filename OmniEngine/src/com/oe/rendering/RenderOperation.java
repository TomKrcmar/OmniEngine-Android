package com.oe.rendering;

import com.oe.materials.Material;

public class RenderOperation
{
	public enum Type {
		POINTS, LINES, TRIANGLES, TRIANGLE_STRIP
	}
	
	public Type mType = Type.TRIANGLES;
	public VertexData mVertexData = null;
	public Material mMaterial = null;
	public Material.ParamDefs mParamDefs = null;
	public float[] mModelMatrix;
	
	public void reset() {
		mType = Type.TRIANGLES;
		mVertexData = null;
		mMaterial = null;
		mParamDefs = null;
		mModelMatrix = null;
	}
}