package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.WidgetGLOverlay;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bymarcin.openglasses.surface.widgets.core.attribute.I3DPositionable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.I3DVertex;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IThroughVisibility;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IViewDistance;
import com.bymarcin.openglasses.surface.widgets.core.attribute.ILookable;

import io.netty.buffer.ByteBuf;

public abstract class WidgetGLWorld extends WidgetGLOverlay implements I3DPositionable, IThroughVisibility, IViewDistance, ILookable{
	public boolean isThroughVisibility = false;
	public boolean isLookingAtEnable = false;
	
	public int lookAtX=0, lookAtY=0, lookAtZ=0;
	
	public int distance=64, viewDistance=32;
		
	public WidgetGLWorld(){
		this.rendertype = RenderType.WorldLocated; 
	}	
	
	public void writeDataWORLD(ByteBuf buff){
		buff.writeBoolean(isThroughVisibility);
		buff.writeInt(distance);
		buff.writeInt(lookAtX);
		buff.writeInt(lookAtY);
		buff.writeInt(lookAtZ);
		buff.writeBoolean(isLookingAtEnable);
	}
	
	public void readDataWORLD(ByteBuf buff){
		isThroughVisibility = buff.readBoolean();
		distance = buff.readInt();
		lookAtX = buff.readInt();
		lookAtY = buff.readInt();
		lookAtZ = buff.readInt();
		isLookingAtEnable = buff.readBoolean();
	}
	
	public boolean isVisibleThroughObjects() {
		return isThroughVisibility; }

	public void setVisibleThroughObjects(boolean visible) {
		isThroughVisibility = visible; }

	public int getDistanceView() {
		return distance; }

	public void setDistanceView(int distance) {
		this.distance = distance; }

	public void setLookingAt(int x, int y, int z) {
		lookAtX = x;
		lookAtY = y;
		lookAtZ = z; 
	}

	public boolean isLookingAtEnable() {
		return isLookingAtEnable; }

	public void setLookingAtEnable(boolean enable) {
		isLookingAtEnable = enable; }

	public int getLookingAtX() {
		return lookAtX; }

	public int getLookingAtY() {
		return lookAtY; }

	public int getLookingAtZ() {
		return lookAtZ;	}
}
