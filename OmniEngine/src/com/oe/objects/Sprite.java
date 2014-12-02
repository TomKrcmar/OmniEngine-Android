package com.oe.objects;

import java.nio.ByteBuffer;
import java.util.Random;

import com.oe.general.Color;
import com.oe.materials.Material;
import com.oe.materials.Texture;
import com.oe.math.Vector2;
import com.oe.math.Vector3;
import com.oe.rendering.BlendMode;
import com.oe.rendering.GpuBuffer;
import com.oe.rendering.RenderOperation;
import com.oe.rendering.RenderQueue;
import com.oe.rendering.Renderable;
import com.oe.rendering.VertexAttribute;
import com.oe.rendering.VertexData;
import com.oe.resources.MaterialManager;
import com.oe.resources.TextureManager;
import com.oe.scene.GameObject;

public class Sprite extends GameObject implements Renderable
{
	private VertexData mVertexData;
	private GpuBuffer mGpuBuffer;
	protected boolean mUpdateBufferFlag;
	
	private Material mMaterial;
	private Material.ParamDefs mRenderParams;
	private Texture mTexture;
	protected Color mColor;
	private BlendMode mBlendMode;
	protected Vector2 mAnchor;
	protected Vector2 mSize;
	protected int mDepth;
	
	public Sprite() {
		mVertexData = new VertexData();
		mGpuBuffer = new GpuBuffer();
		mMaterial = null;
		mRenderParams = null;
		mTexture = null;
		mBlendMode = BlendMode.MODULATE;
		mAnchor = new Vector2(0.5f);
		mSize = new Vector2(128.0f);
		mColor = Color.WHITE;
		mDepth = 0;
		mUpdateBufferFlag = false;
		updateBuffer();
	}
	public Sprite(String texture) {
		this();
		mMaterial = MaterialManager.getInstance().getLoaded("Sprite");
		mTexture = TextureManager.getInstance().getLoaded(texture);
		mSize.set(mTexture.getWidth(), mTexture.getHeight());
		updateRenderParams();
		updateBuffer();
	}
	public Sprite(String[] textureList) {
		this(textureList[new Random().nextInt(textureList.length)]);
	}

	public Material getMaterial() {
		return mMaterial;
	}
	
	public void setMaterial(Material material) {
		mMaterial = material;
	}
	public void setTexture(Texture texture) {
		mTexture = texture;
		updateRenderParams();
	}
	public void setBlendMode(BlendMode bm) {
		mBlendMode = bm;
		updateRenderParams();
	}
	public void setAnchor(Vector2 anchor) {
		mAnchor.set(anchor);
		mUpdateBufferFlag = true;
	}
	public void setSize(Vector2 size) {
		mSize.set(size);
		mUpdateBufferFlag = true;
	}
	public void setColor(Color color) {
		mColor = color;
		mUpdateBufferFlag = true;
	}
	public void setDepth(int depth) {
		mDepth = depth;
	}

	public Vector2 getSize() {
		return new Vector2(mSize);
	}
	
	public void updateRenderParams() {
		if (mTexture != null || mBlendMode != null) {
			mRenderParams = new Material.ParamDefs();
			if (mTexture != null)
				mRenderParams.mTextures.add(mTexture);
			if (mBlendMode != null)
				mRenderParams.mBlendMode = mBlendMode;
		}
		else {
			mRenderParams = null;
		}
	}
	
	private static Vector2 tempVec2 = new Vector2();
	private static Vector3 tempVec3 = new Vector3();
	
	public void clearBuffer() {
		if (mVertexData != null) {
			mVertexData.clear();
		}
		if (mGpuBuffer != null) {
			mGpuBuffer.destroy();
		}
	}
	public void updateBuffer() {
		clearBuffer();
		
		mVertexData.addAttribute(VertexAttribute.POSITION);
		mVertexData.addAttribute(VertexAttribute.TEXCOORD);
		mVertexData.addAttribute(VertexAttribute.COLOR);
		mVertexData.setBuffer(mGpuBuffer);
		mVertexData.setNumVertices(4);
		mGpuBuffer.generate();
		
		int vertSize = mVertexData.getVertexSize();
		int numVertices = mVertexData.getNumVertices();
		int bufferSize = vertSize * numVertices;
		
		final float[] pos = new float[] {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f
		};
		
		Vector3 p = tempVec3;
		Vector2 t = tempVec2;
		Color c = mColor;
		ByteBuffer buffer = mGpuBuffer.map(bufferSize);
		for (int i = 0; i < numVertices; i++) {
			p.set(	(pos[i*2  ]-mAnchor.x)*mSize.x,
					(pos[i*2+1]-1.0f+mAnchor.y)*mSize.y,
					0.0f);
			t.set(	pos[i*2  ],
					1.0f-pos[i*2+1]);
			
			VertexAttribute.putVec3(buffer, p);
			VertexAttribute.putVec2(buffer, t);
			VertexAttribute.putColor(buffer, c);
		}
		mGpuBuffer.unmap();
	}
	
	@Override
	public void onUpdate(float timeScale) {
		if (mUpdateBufferFlag) {
			mUpdateBufferFlag = false;
			updateBuffer();
		}
	}
	
	@Override
	public void queueRenderables(RenderQueue renderQueue) {
		renderQueue.queueRenderable(this);
	}
	
	@Override
	public int getRenderOrder() {
		return mDepth;
	}
	
	@Override
	public void getRenderOperation(RenderOperation op) {
		op.mType = RenderOperation.Type.TRIANGLE_STRIP;
		op.mVertexData = mVertexData;
		op.mModelMatrix = mWorldTransform.getMatrix();
		op.mMaterial = mMaterial;
		op.mParamDefs = mRenderParams;
	}
}
