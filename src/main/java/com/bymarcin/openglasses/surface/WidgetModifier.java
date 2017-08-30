package com.bymarcin.openglasses.surface;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import com.bymarcin.openglasses.surface.WidgetModifierType;

public interface WidgetModifier{
	// rotate, translate, color, scale, texture, 
	 	
	public void apply(EntityPlayer player, boolean overlayActive);
	public void writeData(ByteBuf buff);
	public void readData(ByteBuf buff);
	public short getType();
	public float[] getValues();
}
