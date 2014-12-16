package com.oe.objects;

import java.nio.ByteBuffer;

import com.oe.general.Color;
import com.oe.materials.Material;
import com.oe.materials.Texture;
import com.oe.math.Quaternion;
import com.oe.math.Vector2;
import com.oe.math.Vector3;
import com.oe.rendering.GpuBuffer;
import com.oe.rendering.RenderOperation;
import com.oe.rendering.RenderQueue;
import com.oe.rendering.Renderable;
import com.oe.rendering.VertexAttribute;
import com.oe.rendering.VertexData;
import com.oe.scene.GameObject;

public class Sphere extends GameObject implements Renderable
{
	private VertexData mVertexData;
	private GpuBuffer mGpuBuffer;
	protected boolean mUpdateBufferFlag;

	private Material mMaterial;
	private Material.ParamDefs mRenderParams;
	private Texture mTexture;
	protected Color mColor;
	
	private float mRadius;
	private int mStacks;
	private int mSlices;
	
	public Sphere() {
		this(1.0f, 16, 16);
	}
	public Sphere(float radius) {
		this(radius, 16, 16);
	}
	public Sphere(float radius, int segments) {
		this(radius, segments, segments);
	}
	public Sphere(float radius, int stacks, int slices) {
		mVertexData = new VertexData();
		mGpuBuffer = new GpuBuffer();
		mMaterial = null;
		mRenderParams = null;
		mTexture = null;
		mColor = Color.WHITE;
		mUpdateBufferFlag = false;
		
		mRadius = radius;
		mStacks = stacks;
		mSlices = slices;
		
		updateBuffer();
	}
	
	public void setMaterial(Material material) {
		mMaterial = material;
	}
	public void setTexture(Texture texture) {
		mTexture = texture;
		updateRenderParams();
	}
	public void setColor(Color color) {
		mColor = color;
		mUpdateBufferFlag = true;
	}
	
	public void updateRenderParams() {
		if (mTexture != null) {
			mRenderParams = new Material.ParamDefs();
			mRenderParams.mTextures.add(mTexture);
		}
		else {
			mRenderParams = null;
		}
	}
	
	public void clearBuffer() {
		if (mVertexData != null) {
			mVertexData.clear();
		}
		if (mGpuBuffer != null) {
			mGpuBuffer.destroy();
		}
	}
	private void putVert(ByteBuffer buffer, Vector3 p, Vector3 n, Vector2 t, Color c) {
		p.set(p.x, p.y, p.z * 0.0f);
		VertexAttribute.putVec3(buffer, p);
	//	VertexAttribute.putVec3(buffer, n);
		VertexAttribute.putVec2(buffer, t);
		VertexAttribute.putColor(buffer, c);
	}
	public void updateBuffer() {
		clearBuffer();
		
		float r = mRadius;
		int st = mStacks;
		int sl = mSlices;
		int num = st*2*(sl+2);
		
		mVertexData.addAttribute(VertexAttribute.POSITION);
	//	mVertexData.addAttribute(VertexAttribute.NORMAL);
		mVertexData.addAttribute(VertexAttribute.TEXCOORD);
		mVertexData.addAttribute(VertexAttribute.COLOR);
		mVertexData.setBuffer(mGpuBuffer);
		mVertexData.setNumVertices(num);
		mGpuBuffer.generate();
		
		int vertSize = mVertexData.getVertexSize();
		int numVertices = mVertexData.getNumVertices();
		int bufferSize = vertSize * numVertices;
		
		Vector3 n1 = new Vector3(), n2 = new Vector3(),
				p1 = new Vector3(), p2 = new Vector3();
		Vector2 t1 = new Vector2(), t2 = new Vector2();
		Color c = mColor;
		
		ByteBuffer buffer = mGpuBuffer.map(bufferSize);
		for(int ix = 0; ix < st; ix++)
		{
			float fx1 = (float)ix / st;
			float fx2 = (float)(ix+1) / st;
			float ax1 = fx1 * 180.0f - 90.0f;
			float ax2 = fx2 * 180.0f - 90.0f;
			for(int iy = 0; iy <= sl; iy++)
			{
				float fy = (float)iy / sl;
				float ay = fy * 360.0f;
				n1 = new Quaternion(new Vector3(ax1, ay, 0.0f)).getForward();
				n2 = new Quaternion(new Vector3(ax2, ay, 0.0f)).getForward();
				p1 = n1.mul(r);
				p2 = n2.mul(r);
				t1.set(fy, fx1);
				t2.set(fy, fx2);
				
				if (iy == 0) {
					putVert(buffer, p1, n1, t1, c);
				}
				
				putVert(buffer, p1, n1, t1, c);
				putVert(buffer, p2, n2, t2, c);
			}
			putVert(buffer, p2, n2, t2, c);
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
		return 0;
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
