package com.bymarcin.openglasses.surface.widgets.core.modifiers;

import com.bymarcin.openglasses.surface.WidgetModifier;
import com.bymarcin.openglasses.surface.WidgetModifierType;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import io.netty.buffer.ByteBuf;

public class WidgetModifierRotate extends WidgetModifier {
	float deg, x, y, z;
		
	public WidgetModifierRotate(float deg, float x, float y, float z){
		this.deg = deg;
		this.x = x;
		this.y = y;
		this.z = z;
	}
		
	public void apply(EntityPlayer player, boolean overlayActive){	
		if(!shouldApplyModifier(player, overlayActive)) return;
		GL11.glRotatef(this.deg, this.x, this.y, this.z);
	}
	
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		buff.writeFloat(this.deg);
		buff.writeFloat(this.x);
		buff.writeFloat(this.y);
		buff.writeFloat(this.z);		
	}
	
	public void readData(ByteBuf buff) {
		super.readData(buff);
		this.deg = buff.readFloat();
		this.x = buff.readFloat();
		this.y = buff.readFloat();
		this.z = buff.readFloat();		
	}
	
	public short getType(){
		return WidgetModifierType.ROTATE;
	}	
	
	public Object[] getValues(){
		return new Object[]{ this.deg, this.x, this.y, this.z };
	}
}
