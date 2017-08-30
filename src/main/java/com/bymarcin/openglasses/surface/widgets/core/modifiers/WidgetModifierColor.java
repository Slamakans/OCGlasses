package com.bymarcin.openglasses.surface.widgets.core.modifiers;

import com.bymarcin.openglasses.surface.WidgetModifier;
import com.bymarcin.openglasses.surface.WidgetModifierType;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import io.netty.buffer.ByteBuf;

public class WidgetModifierColor implements WidgetModifier {
	float r, g, b, alpha;
	
	public WidgetModifierColor(float r, float g, float b, float alpha){
		this.setColor(r, g, b, alpha);
	}
		
	public void apply(EntityPlayer player, boolean overlayActive){	
		if(this.alpha < 1)
			GL11.glColor4f(this.r, this.g, this.b, this.alpha);
		else
			GL11.glColor3f(this.r, this.g, this.b);
	}
	
	public void writeData(ByteBuf buff) {
		buff.writeFloat(this.r);
		buff.writeFloat(this.g);
		buff.writeFloat(this.b);
		buff.writeFloat(this.alpha);
	}
	
	public void readData(ByteBuf buff) {
		this.setColor(buff.readFloat(), buff.readFloat(), buff.readFloat(), buff.readFloat());
	}
	
	private void setColor(float r, float g, float b, float alpha){
		this.r = r;
		this.g = g;
		this.b = b;
		this.alpha = alpha;
	}
	
	public short getType(){
		return WidgetModifierType.COLOR;
	}
	
	public float[] getValues(){
		return new float[]{ this.r, this.g, this.b, this.alpha };
	}
}
