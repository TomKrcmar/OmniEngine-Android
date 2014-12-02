package com.oe.materials;

import java.io.IOException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.oe.resources.Resource;

public class Texture implements Resource
{
	private int mBinding;
	private int mWidth;
	private int mHeight;
	
	@Override
	public boolean loadResource(AssetManager am, String filePath) {
		try {
			Bitmap bmp = BitmapFactory.decodeStream(am.open(filePath));
			
			mWidth = bmp.getWidth();
			mHeight = bmp.getHeight();
			
			int[] textures = new int[1];
			GLES20.glGenTextures(1, textures, 0);
			mBinding = textures[0];
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBinding);
			
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
			
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}
	@Override
	public boolean unloadResource() {
		if (mBinding > 0) {
			GLES20.glDeleteTextures(1, new int[] {mBinding}, 0);
			mBinding = 0;
		}
		return true;
	}
	public int getBinding() {
		return mBinding;
	}
	public int getWidth() {
		return mWidth;
	}
	public int getHeight() {
		return mHeight;
	}
}
