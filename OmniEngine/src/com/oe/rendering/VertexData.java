package com.oe.rendering;

import java.util.ArrayList;

public class VertexData
{
	private ArrayList<VertexAttribute> mAttributes;
	private int mVertexSize;
	private int mNumVertices;
	private GpuBuffer mBuffer;
	
	public VertexData() {
		mAttributes = new ArrayList<VertexAttribute>();
		mVertexSize = 0;
		mNumVertices = 0;
		mBuffer = null;
	}
	
	public void clear() {
		mAttributes.clear();
		mVertexSize = 0;
		mNumVertices = 0;
		mBuffer = null;
	}
	
	public int getNumAttributes() {
		return mAttributes.size();
	}
	public VertexAttribute getAttribute(int index) {
		return mAttributes.get(index);
	}
	public void addAttribute(VertexAttribute attribute) {
		mAttributes.add(attribute);
		mVertexSize += attribute.getDataSize();
	}
	
	public int getVertexSize() {
		return mVertexSize;
	}
	public int getNumVertices() {
		return mNumVertices;
	}
	public GpuBuffer getBuffer() {
		return mBuffer;
	}
	
	public void setBuffer(GpuBuffer buffer) {
		mBuffer = buffer;
	}
	public void setNumVertices(int numVertices) {
		mNumVertices = numVertices;
	}
}
