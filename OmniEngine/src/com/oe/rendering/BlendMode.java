package com.oe.rendering;

public class BlendMode
{
	public static enum Term {
		ZERO, ONE,
		SRC_ALPHA, ONE_MINUS_SRC_ALPHA, SRC_COLOR, ONE_MINUS_SRC_COLOR,
		DST_ALPHA, ONE_MINUS_DST_ALPHA, DST_COLOR, ONE_MINUS_DST_COLOR;
	}
	
	public static BlendMode ADDITIVE_NO_ALPHA = new BlendMode(Term.ONE, Term.ONE);
	public static BlendMode ADDITIVE_ALPHA = new BlendMode(Term.SRC_ALPHA, Term.ONE);
	public static BlendMode MODULATE = new BlendMode(Term.SRC_ALPHA, Term.ONE_MINUS_SRC_ALPHA);
	
	public Term mSrc;
	public Term mDst;

	public BlendMode() {
		mSrc = Term.SRC_ALPHA;
		mDst = Term.ONE_MINUS_SRC_ALPHA;
	}
	public BlendMode(Term src, Term dst) {
		mSrc = src;
		mDst = dst;
	}
}
