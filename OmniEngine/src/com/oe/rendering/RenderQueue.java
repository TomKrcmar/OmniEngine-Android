package com.oe.rendering;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class RenderQueue
{
	private class RenderableComparator implements Comparator<Renderable>
	{
		@Override
		public int compare(Renderable a, Renderable b) {
			return b.getRenderOrder() - a.getRenderOrder();
		}
	}
	
	private RenderSystem mRenderSystem;
	private RenderableComparator mComparator;
	private PriorityQueue<Renderable> mQueue;
	
	private RenderOperation op;
	
	public RenderQueue() {
		mComparator = new RenderableComparator();
		mQueue = new PriorityQueue<Renderable>(1, mComparator);
		
		op = new RenderOperation();
	}
	
	public void renderRenderables() {
		Iterator<Renderable> it = mQueue.iterator();
		while (it.hasNext()) {
			Renderable renderable = it.next();
			renderable.getRenderOperation(op);
			mRenderSystem.render(op);
			op.reset();
		}
	}
	public void clearRenderables() {
		mQueue.clear();
	}
	public void queueRenderable(Renderable renderable) {
		mQueue.add(renderable);
	}
	
	public void setRenderSystem(RenderSystem renderSystem) {
		mRenderSystem = renderSystem;
	}
}
