package com.game;

import com.oe.general.Color;
import com.oe.math.OEMath;
import com.oe.math.Vector2;
import com.oe.math.Vector3;
import com.oe.objects.Sprite;
import com.oe.scene.GameObject;

public class Cloud extends Sprite
{
	public float mSpeed = 10.0f;
	
	public Cloud(float speed) {
		super(new String[] {"Cloud1", "Cloud2"});
		
		float depth = (float) Math.random();
		float scaleZ = OEMath.linInterp(1.2f, 0.6f, depth);
		mSize = new Vector2(512.0f, 256.0f);
		mSize.x *= (float) (Math.random()*0.4+0.8) * scaleZ;
		mSize.y *= (float) (Math.random()*0.4+0.8) * scaleZ;
		
		float interp = OEMath.linInterp(1.0f, 0.5f, depth);
		setColor(OEMath.linInterp(	MainActivity.BG_COLOR,
									Color.WHITE,
									interp));
		
		mSpeed = speed * interp;
		
		float x = (mSpeed >= 0.0f) ?
					-MainActivity.VIEW_EXTENTS_X - (mSize.x/2.0f) :
					MainActivity.VIEW_EXTENTS_X + (mSize.x/2.0f);
		
		float y = OEMath.linInterp(-MainActivity.VIEW_EXTENTS_Y,
									MainActivity.VIEW_EXTENTS_Y,
									(float) Math.random());
		
		setPos(x, y, 0.25f + 0.5f*depth);
		setDepth(2 + Math.round(depth*1000));
		mUpdateBufferFlag = true;
	}
	@Override public void onAddedToParent(GameObject lastParent) {}
	@Override public void onDestroy() {}
	@Override public void onUpdate(float timeScale) {
		super.onUpdate(timeScale);
		Vector3 pos = getPos();
		pos.x += mSpeed * timeScale;
		setPos(pos);
		
		if (mSpeed >= 0.0f) {
			float limit = MainActivity.VIEW_EXTENTS_X + (mSize.x/2.0f);
			if (pos.x > limit) {
				this.destroy();
			}
		}
		else {
			float limit = -MainActivity.VIEW_EXTENTS_X - (mSize.x/2.0f);
			if (pos.x < limit) {
				this.destroy();
			}
		}
	}
}
