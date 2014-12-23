package com.oe.materials;

import java.util.ArrayList;
import java.util.Scanner;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.oe.general.LogSystem;
import com.oe.rendering.VertexAttribute;
import com.oe.resources.Resource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class Shader implements Resource
{
	public static enum Type {
		VERTEX, FRAGMENT;
	}
	public static class Attribute {
		public String mName;
		public int mIndex;
		public Attribute(String name, int index) {
			mName = name;
			mIndex = index;
		}
	}
	public static class Uniform {
		public static enum Type {
			INT(1), INT2(2), INT3(3), INT4(4),
			FLOAT(1), VEC2(2), VEC3(3), VEC4(4),
			MAT3(9), MAT4(16),
			BUFFER(1);

			private int mComponents = 1;

			private Type(int components) {
				mComponents = components;
			}

			public Object getValueByString(String string) {
				Object value = null;
				if (this == INT || this == INT2 ||
					this == INT3 || this == INT4)
				{
					Scanner scanner = new Scanner(string);
					if (mComponents == 1) {
						value = new Integer(scanner.nextInt());
					}
					else {
						int[] array = new int[mComponents];
						for (int i = 0; i < mComponents; i++) {
							array[i] = scanner.nextInt();
						}
						value = array;
					}
				}
				else if (this == FLOAT || this == VEC2 ||
						this == VEC3 || this == VEC4 ||
						this == MAT3 || this == MAT4)
				{
					Scanner scanner = new Scanner(string);
					if (mComponents == 1) {
						value = new Float(scanner.nextFloat());
					}
					else {
						float[] array = new float[mComponents];
						for (int i = 0; i < mComponents; i++) {
							array[i] = scanner.nextFloat();
						}
						value = array;
					}
				}
				else {
					// Unsupported
				}
				return value;
			}
		};
		public static enum Preset {
			NONE(Type.FLOAT),
			M_MATRIX(Type.MAT4),	V_MATRIX(Type.MAT4),	P_MATRIX(Type.MAT4),
			MV_MATRIX(Type.MAT4),	MVP_MATRIX(Type.MAT4),	N_MATRIX(Type.MAT3),
			CAMERA_POS(Type.VEC3);
			
			public Type mType;
			
			private Preset(Type type) {
				mType = type;
			}
		};
		
		public String mName;
		public int mIndex;
		public Preset mPreset;
		public Type mType;
		public Object mValue;

		public Uniform(String name, int index, Preset preset) {
			mName = name;
			mIndex = index;
			mPreset = preset;
			mType = preset.mType;
			mValue = null;
		}
		public Uniform(String name, int index, Type type, Object value) {
			mName = name;
			mIndex = index;
			mPreset = Preset.NONE;
			mType = type;
			mValue = value;
		}
	}
	
	private ArrayList<Attribute> mAttributes = new ArrayList<Attribute>();
	private ArrayList<Uniform> mUniforms = new ArrayList<Uniform>();
	
	private int mProgramBinding;
	private int mVertBinding;
	private int mFragBinding;
	
	public Shader() {
		mProgramBinding = 0;
		mVertBinding = 0;
		mFragBinding = 0;
	}
	
	public void destroy() {
		if (mProgramBinding > 0) {
			if (mVertBinding > 0) {
				GLES20.glDetachShader(mProgramBinding, mVertBinding);
				GLES20.glDeleteShader(mVertBinding);
				mVertBinding = 0;
			}
			if (mFragBinding > 0) {
				GLES20.glDetachShader(mProgramBinding, mFragBinding);
				GLES20.glDeleteShader(mFragBinding);
				mFragBinding = 0;
			}
			GLES20.glDeleteProgram(mProgramBinding);
			mProgramBinding = 0;
		}
	}
	
	public void generate() {
		if (mProgramBinding > 0)
			destroy();
		
		mProgramBinding = GLES20.glCreateProgram();
	}

	public void attach() {
		if (mProgramBinding > 0) {
			if (mVertBinding > 0)
				GLES20.glAttachShader(mProgramBinding, mVertBinding);
			if (mFragBinding > 0)
				GLES20.glAttachShader(mProgramBinding, mFragBinding);
		}
	}

	public void link() {
		if (mProgramBinding > 0) {
			GLES20.glLinkProgram(mProgramBinding);
		}
	}
	
	public void compileSource(Type type, String shaderSource) {
		int unit = 0;
		switch (type) {
		case VERTEX:	unit = GLES20.GL_VERTEX_SHADER;		break;
		case FRAGMENT:	unit = GLES20.GL_FRAGMENT_SHADER;	break;
		}
		
		int binding = GLES20.glCreateShader(unit);
		
		GLES20.glShaderSource(binding, shaderSource);
		GLES20.glCompileShader(binding);
		
		int[] status = {0};
		GLES20.glGetShaderiv(binding, GLES20.GL_COMPILE_STATUS, status, 0);
		
		if (status[0] == GLES20.GL_FALSE) {
			String log = GLES20.glGetShaderInfoLog(binding);
			LogSystem.warning(this, "Compilation error for shader unit "+type.toString()+": "+log);
			GLES20.glDeleteShader(binding);
		}
		else {
			switch (type) {
			case VERTEX:
				mVertBinding = binding;
				break;
			case FRAGMENT:
				mFragBinding = binding;
				break;
			}
		}
	}
	
	public int getNumUniforms() {
		return mUniforms.size();
	}
	public Uniform getUniform(int index) {
		return mUniforms.get(index);
	}
	public ArrayList<Uniform> getUniforms() {
		return mUniforms;
	}
	public void mapUniform(String name, Uniform.Preset preset) {
		if (mProgramBinding > 0) {
			int size = mUniforms.size();
			for (int i = 0; i < size; i++) {
				Uniform u = mUniforms.get(i);
				if (u.mName.equals(name)) {
					u.mPreset = preset;
					u.mType = preset.mType;
					u.mValue = null;
					return;
				}
			}
			
			int index = GLES20.glGetUniformLocation(mProgramBinding, name);
			mUniforms.add(new Uniform(name, index, preset));
		}
	}
	public void mapUniform(String name, Uniform.Type type, Object value) {
		if (mProgramBinding > 0) {
			int size = mUniforms.size();
			for (int i = 0; i < size; i++) {
				Uniform u = mUniforms.get(i);
				if (u.mName.equals(name)) {
					u.mPreset = Uniform.Preset.NONE;
					u.mType = type;
					u.mValue = value;
					return;
				}
			}
			
			int index = GLES20.glGetUniformLocation(mProgramBinding, name);
			mUniforms.add(new Uniform(name, index, type, value));
		}
	}
	
	public void mapAttribute(String name, VertexAttribute.Preset preset) {
		int index = preset.mIndex;
		if (mProgramBinding > 0) {
			int size = mAttributes.size();
			for (int i = 0; i < size; i++) {
				Attribute a = mAttributes.get(i);
				if (a.mName.equals(name)) {
					a.mIndex = index;
					GLES20.glBindAttribLocation(mProgramBinding, a.mIndex, a.mName);
					return;
				}
			}
			mAttributes.add(new Attribute(name, index));
			GLES20.glBindAttribLocation(mProgramBinding, index, name);
		}
		
	}
	public int getAttributeIndex(VertexAttribute attrib) {
		int size = mAttributes.size();
		for (int i = 0; i < size; i++) {
			Attribute a = mAttributes.get(i);
			if (attrib.getPreset() == VertexAttribute.Preset.NONE
			 && attrib.getCustomIndex() == a.mIndex - VertexAttribute.Preset.NONE.mIndex
			) {
				return a.mIndex;
			}
			else if (attrib.getPreset().mIndex == a.mIndex) {
				return a.mIndex;
			}
		}
		return -1;
	}
	
	public int getProgramBinding() {
		return mProgramBinding;
	}
	
	@Override
	public boolean loadResource(AssetManager am, String filePath) {
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(am.open(filePath), null);

			String vertPath = "";
			String fragPath = "";

			final int OUTSIDE_SHADER = 0;
			final int INSIDE_SHADER = 1;
			int state = OUTSIDE_SHADER;
			boolean linked = false;

			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					String tag = parser.getName();
					switch (state) {
						case OUTSIDE_SHADER: {
							if (tag.equals("Shader")) {
								generate();
								linked = false;

								int count = parser.getAttributeCount();
								for (int i = 0; i < count; i++) {
									String key = parser.getAttributeName(i);
									String value = parser.getAttributeValue(i);
									Type type = null;

									if (key.equals("vert"))
										type = Type.VERTEX;
									else if (key.equals("frag"))
										type = Type.FRAGMENT;


									if (type != null) {
										String source = "";
										Scanner scanner = new Scanner(am.open(value));
										while (scanner.hasNextLine())
											source += scanner.nextLine() + "\n";
										scanner.close();
										compileSource(type, source);
									}
								}
								attach();
								state = INSIDE_SHADER;
							}
						}
					//	<Attribute name="vertColor" preset="COLOR" />

					//	<Uniform name="mvpMatrix" preset="MVP_MATRIX" />
					//	<Uniform name="diffuseSampler" type="INT" value="0" />
						case INSIDE_SHADER: {
							if (tag.equals("Attribute")) {
								if (!linked) {
									String name = null, preset = null;
									int count = parser.getAttributeCount();
									for (int i = 0; i < count; i++) {
										String key = parser.getAttributeName(i);
										String value = parser.getAttributeValue(i);
										if (key.equals("name")) name = value;
										if (key.equals("preset")) preset = value;
									}
									if (name != null && preset != null) {
										mapAttribute(name, VertexAttribute.Preset.valueOf(preset));
									}
								}
							}
							else if (tag.equals("Uniform")) {
								if (!linked) {
									link();
									linked = true;
								}
								String name = null, preset = null, stype = null, svalue = null;
								int count = parser.getAttributeCount();
								for (int i = 0; i < count; i++) {
									String key = parser.getAttributeName(i);
									String value = parser.getAttributeValue(i);
									if (key.equals("name")) name = value;
									if (key.equals("preset")) preset = value;
									if (key.equals("type")) stype = value;
									if (key.equals("value")) svalue = value;
								}
								if (name != null && preset != null) {
									mapUniform(name, Uniform.Preset.valueOf(preset));
								}
								else if (name != null && stype != null && svalue != null) {
									Uniform.Type type = Uniform.Type.valueOf(stype);
									Object value = type.getValueByString(svalue);
									mapUniform("diffuseSampler", type, value);
								}
							}
						}
					}
				}
				else if (event == XmlPullParser.END_TAG) {
					String tag = parser.getName();
					switch (state) {
						case INSIDE_SHADER: {
							if (tag.equals("Shader"))
								state = OUTSIDE_SHADER;
						}
					}
				}
				event = parser.next();
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
		destroy();
		mAttributes.clear();
		mUniforms.clear();
		return true;
	}
}
