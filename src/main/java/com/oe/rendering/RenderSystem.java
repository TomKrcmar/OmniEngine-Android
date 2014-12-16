package com.oe.rendering;

import java.util.ArrayList;

import com.oe.general.Color;
import com.oe.materials.Material;
import com.oe.materials.Shader;
import com.oe.materials.Texture;
import com.oe.math.Rectangle;
import com.oe.scene.Viewport;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class RenderSystem
{
	private ArrayList<OESurfaceView> mRenderTargets;
	private Viewport mActiveViewport;
	
	private Material.Pass mActivePass;
	private Shader mActiveShader;
	
	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mModelViewMatrix = new float[16];
	private float[] mModelViewProjectionMatrix = new float[16];
	
	public RenderSystem() {
		mRenderTargets = new ArrayList<OESurfaceView>();
		
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.setIdentityM(mViewMatrix, 0);
		Matrix.setIdentityM(mProjectionMatrix, 0);
		Matrix.setIdentityM(mModelViewMatrix, 0);
		Matrix.setIdentityM(mModelViewProjectionMatrix, 0);
	}
	
	public OESurfaceView createSurfaceView(Context context) {
		OESurfaceView surfaceView = new OESurfaceView(context);
		mRenderTargets.add(surfaceView);
		return surfaceView;
	}
	
	/* We don't actually need this - Android repaints
	 * all OpenGL surface views by default.
	 public void renderToTargets() {
		int size = mRenderTargets.size();
		for (int i = 0; i < size; i++) {
			mRenderTargets.get(i).render();
		}
	}*/
	
	public void activateViewport(Viewport viewport) {
		if (mActiveViewport != viewport) {
			mActiveViewport = viewport;
			Rectangle rect = viewport.getScreenRect();
			GLES20.glViewport(	(int)rect.x,		(int)rect.y,
								(int)rect.width,	(int)rect.height);
			
			Color clear = viewport.getClearColor();
			GLES20.glClearColor(clear.r, clear.g,
								clear.b, clear.a);
			GLES20.glClearDepthf(1.0f);
		}
		clearBuffer(true, true);
	}
	
	public void clearBuffer(boolean color, boolean depth) {
		if (color && depth) {
			GLES20.glDepthMask(true);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		}
		else if (color && !depth) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		}
		else if (!color && depth) {
			GLES20.glDepthMask(true);
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
		}
	}
	
	protected void updateModelViewMatrix() {
		/*Matrix.multiplyMM(mModelViewMatrix, 0,
				mModelMatrix, 0,
				mViewMatrix, 0);*/
		Matrix.multiplyMM(mModelViewMatrix, 0,
				mViewMatrix, 0,
				mModelMatrix, 0);
		updateMVPMatrix();
	}
	protected void updateMVPMatrix() {
		/*Matrix.multiplyMM(mModelViewProjectionMatrix, 0,
				mModelViewMatrix, 0,
				mProjectionMatrix, 0);*/
		Matrix.multiplyMM(mModelViewProjectionMatrix, 0,
				mProjectionMatrix, 0,
				mModelViewMatrix, 0);
	}
	public void setModelMatrix(float[] matrix) {
		// System.arraycopy(matrix, 0, mModelMatrix, 0, 16);
		mModelMatrix = matrix;
		updateModelViewMatrix();
	}
	public void setViewMatrix(float[] matrix) {
		// System.arraycopy(matrix, 0, mViewMatrix, 0, 16);
		mViewMatrix = matrix;
		updateModelViewMatrix();
	}
	public void setProjectionMatrix(float[] matrix) {
		// System.arraycopy(matrix, 0, mProjectionMatrix, 0, 16);
		mProjectionMatrix = matrix;
		updateMVPMatrix();
	}
	
	public void setActivePass(Material.Pass pass) {
		if (mActivePass != pass) {
			mActivePass = pass;
			Shader shader = pass.mShader;
			if (shader != null) {
				if (shader != mActiveShader) {
					mActiveShader = shader;
					GLES20.glUseProgram(shader.getProgramBinding());
					setStaticUniforms(shader.getUniforms());
				}
				
				/*if (pass.mParamDefs != null) {
					setTextures(pass.mParamDefs);
					setStaticUniforms(pass.mParamDefs.mUniforms);
				}*/
			}
		}
		if (pass.mParamDefs != null)
			setRenderParams(pass.mParamDefs);
	}
	
	public void setRenderParams(Material.ParamDefs params) {
		if (params != null) {
			setTextures(params);
			setBlendMode(params.mBlendMode);
			setStaticUniforms(params.mUniforms);
		}
	}

	public void setTextures(Material.ParamDefs params) {
		int size = params.mTextures.size();
		for (int i = 0; i < size; i++) {
			Texture t = params.mTextures.get(i);
			if (t != null) {
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.getBinding());
			}
		}
	}
	
	public void setBlendMode(BlendMode bm) {
		int src = 0;
		int dst = 0;
		switch (bm.mSrc) {
		case ZERO:					src = GLES20.GL_ZERO; break;
		case ONE:					src = GLES20.GL_ONE; break;
		case DST_ALPHA:				src = GLES20.GL_DST_ALPHA; break;
		case DST_COLOR:				src = GLES20.GL_DST_COLOR; break;
		case SRC_ALPHA:				src = GLES20.GL_SRC_ALPHA; break;
		case SRC_COLOR:				src = GLES20.GL_SRC_COLOR; break;
		case ONE_MINUS_DST_ALPHA:	src = GLES20.GL_ONE_MINUS_DST_ALPHA; break;
		case ONE_MINUS_DST_COLOR:	src = GLES20.GL_ONE_MINUS_DST_COLOR; break;
		case ONE_MINUS_SRC_ALPHA:	src = GLES20.GL_ONE_MINUS_SRC_ALPHA; break;
		case ONE_MINUS_SRC_COLOR:	src = GLES20.GL_ONE_MINUS_SRC_COLOR; break;
		}
		switch (bm.mDst) {
		case ZERO:					dst = GLES20.GL_ZERO; break;
		case ONE:					dst = GLES20.GL_ONE; break;
		case DST_ALPHA:				dst = GLES20.GL_DST_ALPHA; break;
		case DST_COLOR:				dst = GLES20.GL_DST_COLOR; break;
		case SRC_ALPHA:				dst = GLES20.GL_SRC_ALPHA; break;
		case SRC_COLOR:				dst = GLES20.GL_SRC_COLOR; break;
		case ONE_MINUS_DST_ALPHA:	dst = GLES20.GL_ONE_MINUS_DST_ALPHA; break;
		case ONE_MINUS_DST_COLOR:	dst = GLES20.GL_ONE_MINUS_DST_COLOR; break;
		case ONE_MINUS_SRC_ALPHA:	dst = GLES20.GL_ONE_MINUS_SRC_ALPHA; break;
		case ONE_MINUS_SRC_COLOR:	dst = GLES20.GL_ONE_MINUS_SRC_COLOR; break;
		}
		GLES20.glBlendFunc(src, dst);
	}
	
	public void setStaticUniforms(ArrayList<Shader.Uniform> uniforms) {
		int size = uniforms.size();
		for (int i = 0; i < size; i++) {
			Shader.Uniform u = uniforms.get(i);
			if (u.mPreset == Shader.Uniform.Preset.NONE) {
				int index = u.mIndex;
				switch (u.mType) {
				case INT: {
					GLES20.glUniform1i(index, (Integer) u.mValue);
					break;
				}
				case INT2: {
					int[] val = (int[]) u.mValue;
					GLES20.glUniform2i(index, val[0], val[1]);
					break;
				}
				case INT3: {
					int[] val = (int[]) u.mValue;
					GLES20.glUniform3i(index, val[0], val[1], val[3]);
					break;
				}
				case INT4: {
					int[] val = (int[]) u.mValue;
					GLES20.glUniform4i(index, val[0], val[1], val[3], val[4]);
					break;
				}
				case FLOAT: {
					GLES20.glUniform1f(index, (Float) u.mValue);
					break;
				}
				case VEC2: {
					float[] val = (float[]) u.mValue;
					GLES20.glUniform2f(index, val[0], val[1]);
					break;
				}
				case VEC3: {
					float[] val = (float[]) u.mValue;
					GLES20.glUniform3f(index, val[0], val[1], val[3]);
					break;
				}
				case VEC4: {
					float[] val = (float[]) u.mValue;
					GLES20.glUniform4f(index, val[0], val[1], val[3], val[4]);
					break;
				}
				case MAT3: {
					float[] val = (float[]) u.mValue;
					GLES20.glUniformMatrix3fv(index, 1, false, val, 0);
					break;
				}
				case MAT4: {
					float[] val = (float[]) u.mValue;
					GLES20.glUniformMatrix4fv(index, 1, false, val, 0);
					break;
				}
				case BUFFER: {
					//Unsupported
					//GLES20...
					break;
				}
				default:
					break;
				}
			}
		}
	}
	public void setDynamicUniforms(ArrayList<Shader.Uniform> uniforms) {
		int size = uniforms.size();
		for (int i = 0; i < size; i++) {
			Shader.Uniform u = uniforms.get(i);
			int index = u.mIndex;
			
			switch (u.mPreset) {
			case M_MATRIX:
				GLES20.glUniformMatrix4fv(index, 1, false, mModelMatrix, 0);
				break;
			case V_MATRIX:
				GLES20.glUniformMatrix4fv(index, 1, false, mViewMatrix, 0);
				break;
			case P_MATRIX:
				GLES20.glUniformMatrix4fv(index, 1, false, mProjectionMatrix, 0);
				break;
			case MV_MATRIX:
				GLES20.glUniformMatrix4fv(index, 1, false, mModelViewMatrix, 0);
				break;
			case MVP_MATRIX:
				GLES20.glUniformMatrix4fv(index, 1, false, mModelViewProjectionMatrix, 0);
				break;
			case N_MATRIX:
				// Do nothing - Unsupported
				// GLES20.glUniformMatrix3fv(index, 1, false, mNormalMatrix, 0);
				break;
			case CAMERA_POS:
				// Do nothing - Unsupported
				// GLES20.glUniform3f(index, x, y, z);
				break;
			default:
				break;
			}
		}
	}
	
	public void setAttributes(VertexData vertexData, Shader shader) {
		int type = GLES20.GL_FLOAT;
		int size = 1;
		int stride = vertexData.getVertexSize();
		int attribSize = 4;
		int offset = 0;
		boolean normalized = true;
		
		int numAttribs = vertexData.getNumAttributes();
		for (int i = 0; i < numAttribs; i++) {
			VertexAttribute attrib = vertexData.getAttribute(i);
			
			size = attrib.getNumComponents();
			attribSize = attrib.getDataSize();
			
			switch (attrib.getType()) {
			case FLOAT:			type = GLES20.GL_FLOAT; break;
			case INT:			type = GLES20.GL_INT; break;
			case UNSIGNED_BYTE:	type = GLES20.GL_UNSIGNED_BYTE; break;
			}
			
			int index = shader.getAttributeIndex(attrib);
			if (index >= 0) {
				GLES20.glEnableVertexAttribArray(index);
				GLES20.glVertexAttribPointer(index, size, type, normalized, stride, offset);
			}
			offset += attribSize;
		}
	}
	
	public void render(RenderOperation op) {
		VertexData vertexData = op.mVertexData;
		Material material = op.mMaterial;
		if (vertexData != null && material != null) {
			GpuBuffer buffer = vertexData.getBuffer();
			if (buffer != null) {
				GpuBuffer.bindBuffer(buffer);
				
				if (op.mModelMatrix != null)
					this.setModelMatrix(op.mModelMatrix);
				
				int startVertex = 0;
				int numVertices = vertexData.getNumVertices();
				
				int geomType = GLES20.GL_POINTS;
				switch (op.mType) {
					case POINTS:			geomType = GLES20.GL_POINTS;		break;
					case LINES:				geomType = GLES20.GL_LINES;			break;
					case TRIANGLES:			geomType = GLES20.GL_TRIANGLES;		break;
					case TRIANGLE_STRIP:	geomType = GLES20.GL_TRIANGLE_STRIP;break;
				}
				
				ArrayList<Material.Pass> passes = material.getPasses();
				int numPasses =  passes.size();
				for (int i = 0; i < numPasses; i++) {
					Material.Pass pass = passes.get(i);
					Shader shader = pass.mShader;
					
					setActivePass(pass);
					setAttributes(vertexData, shader);
					
					if (op.mParamDefs != null) {
						setRenderParams(op.mParamDefs);
					}
					
					setDynamicUniforms(shader.getUniforms());
					
					GLES20.glDrawArrays(geomType, startVertex, numVertices);
				}
				
				GpuBuffer.bindBuffer(null);
			}
		}
	}
}
