package com.oe.materials;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import android.content.res.AssetManager;

import com.oe.rendering.BlendMode;
import com.oe.resources.Resource;
import com.oe.resources.ShaderManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class Material implements Resource
{
	public static class ParamDefs
	{
		public ArrayList<Texture> mTextures;
		public ArrayList<Shader.Uniform> mUniforms;
		public BlendMode mBlendMode;
		
		public ParamDefs() {
			mTextures = new ArrayList<Texture>();
			mUniforms = new ArrayList<Shader.Uniform>();
			mBlendMode = BlendMode.MODULATE;
		}
	}
	
	private ArrayList<Pass> mPasses;
	
	public Material() {
		mPasses = new ArrayList<Pass>();
	}

	public Pass createPass() {
		Pass pass = new Pass();
		mPasses.add(pass);
		return pass;
	}
	public ArrayList<Pass> getPasses() {
		return mPasses;
	}

	@Override
	public boolean loadResource(AssetManager am, String filePath) {
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(am.open(filePath), null);

			final int OUTSIDE_MATERIAL = 0;
			final int READING_PASSES = 1;
			final int INSIDE_PASS = 2;
			int state = OUTSIDE_MATERIAL;

			Pass currentPass = null;

			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					String name = parser.getName();
					switch (state) {
						case OUTSIDE_MATERIAL: {
							if (name.equals("Material"))
								state = READING_PASSES;
							break;
						}
						case READING_PASSES: {
							if (name.equals("Pass")) {
								currentPass = createPass();
								state = INSIDE_PASS;
							}
							break;
						}
						case INSIDE_PASS: {
							if (name.equals("Shader")) {
								int num = parser.getAttributeCount();
								if (num == 1) {
									String key = parser.getAttributeName(0);
									if (key.equals("name")) {
										String value = parser.getAttributeValue(0);
										ShaderManager mgr = ShaderManager.getInstance();
										currentPass.mShader = mgr.getLoaded(value);
									}
								}
							}
							break;
						}
					}
				}
				else if (event == XmlPullParser.END_TAG) {
					String name = parser.getName();
					switch (state) {
						case READING_PASSES: {
							if (name.equals("Material"))
								state = OUTSIDE_MATERIAL;
							break;
						}
						case INSIDE_PASS: {
							if (name.equals("Pass")) {
								currentPass = null;
								state = READING_PASSES;
							}
							break;
						}
					}
				}
				event = parser.next();
			}

			if (state != OUTSIDE_MATERIAL) {
				mPasses.clear();
				return false;
			}

			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public boolean unloadResource() {
		mPasses.clear();
		return true;
	}
}
