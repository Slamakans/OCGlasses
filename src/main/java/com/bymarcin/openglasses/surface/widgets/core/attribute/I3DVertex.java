package com.bymarcin.openglasses.surface.widgets.core.attribute;

public interface I3DVertex extends IAttribute{
	public int getVertexCount();
	public void setVertex(int n, double nx, double ny, double nz);
}
