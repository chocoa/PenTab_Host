package com.akigon.java.pentab_pc;

public class Vector2D {
	public float x;
	public float y;

	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public Vector2D(Vector2D v) {
		this.x = v.x;
		this.y = v.y;
	}

	// 平方長
	public float lengthSquared() {
		return this.x*this.x + this.y*this.y;
	}

	// 長さ
	public float length() {
		return (float)Math.sqrt(this.lengthSquared());
	}

	// 正規化ベクトルを取得
	public Vector2D normalize() {
		float l = this.length();
		if(l > 0) l = 1 / l;
		return new Vector2D(this.x*l, this.y*l);
	}

	// 直行ベクトルを取得
	public Vector2D perpendicular() {
		return new Vector2D(-this.y, this.x);
	}
}
