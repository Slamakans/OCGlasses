package com.bymarcin.openglasses.surface.widgets.core.modifiers;

import com.bymarcin.openglasses.surface.WidgetModifier;
import com.bymarcin.openglasses.surface.WidgetModifierType;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import io.netty.buffer.ByteBuf;

public class WidgetModifierTranslate extends WidgetModifier {
	float x, y, z;
		
	public WidgetModifierTranslate(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
		
	public void apply(long conditionStates){	
		if(!shouldApplyModifier(conditionStates)) return;
		GL11.glTranslatef(this.x, this.y, this.z);
	}
	
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		buff.writeFloat(this.x);
		buff.writeFloat(this.y);
		buff.writeFloat(this.z);
	}
	
	public void readData(ByteBuf buff) {
		super.readData(buff);
		this.x = buff.readFloat();
		this.y = buff.readFloat();
		this.z = buff.readFloat();		
	}	
	
	public short getType(){
		return WidgetModifierType.TRANSLATE;
	}
	
	public Object[] getValues(){
		return new Object[]{ this.x, this.y, this.z };
	}
}
