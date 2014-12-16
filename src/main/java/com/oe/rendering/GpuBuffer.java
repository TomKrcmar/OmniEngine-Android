package com.oe.rendering;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.opengl.GLES20;

public class GpuBuffer
{
	private static GpuBuffer mBoundBuffer = null;
	public static void bindBuffer(GpuBuffer buffer) {
		if (mBoundBuffer != buffer) {
			if (buffer == null) {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			}
			else {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.mBinding);
			}
			mBoundBuffer = buffer;
		}
	}

	private ByteBuffer mMappedData;
	private int mBinding;
	private int mSize;
	
	public GpuBuffer() {
		mMappedData = null;
		mBinding = 0;
		mSize = 0;
	}
	
	public void generate() {
		if (mMappedData == null) {
			if (mBinding > 0)
				destroy();
			
			int[] buffers = {0};
			GLES20.glGenBuffers(1, buffers, 0);
			mBinding = buffers[0];
		}
	}
	
	public void destroy() {
		if (mMappedData == null) {
			if (mBinding > 0) {
				GLES20.glDeleteBuffers(1, new int[] {mBinding}, 0);
				mBinding = 0;
				mSize = 0;
			}
		}
	}
	
	private void write(Buffer buffer) {
		if (mBinding > 0) {
			GpuBuffer prevBuffer = mBoundBuffer;
			bindBuffer(this);
			
			buffer.position(0);
			mSize = buffer.capacity();
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBinding);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mSize, buffer, GLES20.GL_STATIC_DRAW);
			
			bindBuffer(prevBuffer);
		}
	}
	
	public ByteBuffer map(int size) {
		if (mBinding > 0 && mMappedData == null) {
			mMappedData = ByteBuffer.allocateDirect(size);
			mMappedData.order(ByteOrder.nativeOrder());
			mMappedData.rewind();
			return mMappedData;
		}
		return null;
	}
	public void unmap() {
		if (mBinding > 0 && mMappedData != null) {
			this.write(mMappedData);
			mMappedData = null;
		}
	}
	
	public int getSize() {
		return mSize;
	}
}
