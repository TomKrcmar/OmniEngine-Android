package com.oe.materials;

import java.util.ArrayList;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.oe.general.LogSystem;
import com.oe.rendering.VertexAttribute;
import com.oe.resources.Resource;

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
			INT, INT2, INT3, INT4,
			FLOAT, VEC2, VEC3, VEC4,
			MAT3, MAT4,
			BUFFER
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
			}
			if (mFragBinding > 0) {
				GLES20.glDetachShader(mProgramBinding, mFragBinding);
				GLES20.glDeleteShader(mFragBinding);
			}
			GLES20.glDeleteProgram(mProgramBinding);
		}
	}
	
	public void generateAndAttach() {
		if (mProgramBinding > 0)
			destroy();
		
		mProgramBinding = GLES20.glCreateProgram();
		
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
	/*	try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(am.open(filePath), null);
			parser.nextTag();
			
			parser.require(XmlPullParser.START_TAG, null, "Shader");
			String vertPath = "";
			String fragPath = "";
			
			String vertSrc = "";
			Scanner scanner = new Scanner(am.open(vertPath));
			while (scanner.hasNextLine())
				vertSrc += scanner.nextLine()+"\n";
			scanner.close();
			compileSource(Shader.Type.VERTEX, vertSrc);
			
			String fragSrc = "";
			scanner = new Scanner(am.open(fragPath));
			while (scanner.hasNextLine())
				fragSrc += scanner.nextLine()+"\n";
			scanner.close();
			compileSource(Shader.Type.FRAGMENT, fragSrc);
			
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				
				if (name.equals("Attribute")) {
					
				}
				else if (name.equals("Uniform")) {
					
				}
				else {
				//	skip(parser);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		generateAndAttach();
		
		mapAttribute("vertPosition", VertexAttribute.Preset.POSITION);
		mapAttribute("vertTexCoord", VertexAttribute.Preset.TEXCOORD);
		mapAttribute("vertColor", VertexAttribute.Preset.COLOR);
		
		link();
		
		mapUniform("mvpMatrix", Shader.Uniform.Preset.MVP_MATRIX);
		mapUniform("diffuseSampler", Shader.Uniform.Type.INT, Integer.valueOf(0));
		*/
		return true;
	}
	@Override
	public boolean unloadResource() {
		return true;
	}
}
