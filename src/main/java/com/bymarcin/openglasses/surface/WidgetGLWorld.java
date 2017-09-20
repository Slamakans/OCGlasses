package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.WidgetGLOverlay;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bymarcin.openglasses.surface.widgets.core.attribute.IThroughVisibility;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IViewDistance;
import com.bymarcin.openglasses.surface.widgets.core.attribute.ILookable;

import io.netty.buffer.ByteBuf;

public abstract class WidgetGLWorld extends WidgetGLOverlay implements IThroughVisibility, IViewDistance, ILookable{
	
	int viewDistance=32;
	
	public WidgetGLWorld(){
		this.rendertype = RenderType.WorldLocated; 
	}	
	
	public void writeData(ByteBuf buff){
		super.writeData(buff);
		buff.writeBoolean(isThroughVisibility);	
		buff.writeBoolean(faceWidgetToPlayer);
	}
	
	public void readData(ByteBuf buff){
		super.readData(buff);
		isThroughVisibility = buff.readBoolean();		
		faceWidgetToPlayer = buff.readBoolean();
	}
	
	public boolean isVisibleThroughObjects() {
		return isThroughVisibility; }

	public void setVisibleThroughObjects(boolean visible) {
		isThroughVisibility = visible; }
}
