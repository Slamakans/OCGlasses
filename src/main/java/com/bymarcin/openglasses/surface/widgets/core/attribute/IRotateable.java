package com.bymarcin.openglasses.surface.widgets.core.attribute;

public interface IRotateable extends IAttribute{
	public void setRotation(float rx, float ry, float rz);
	public float getRotationX();
	public float getRotationY();
	public float getRotationZ();
}
