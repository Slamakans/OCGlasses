package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.WidgetModifier;
import com.bymarcin.openglasses.surface.WidgetModifierType;

import com.bymarcin.openglasses.surface.widgets.core.modifiers.*;
import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bymarcin.openglasses.utils.OGUtils;

public class WidgetModifiers {
	public ArrayList<WidgetModifier> modifiers = new ArrayList<WidgetModifier>();
	
	public void addTranslate(float x, float y, float z){
		this.modifiers.add(new WidgetModifierTranslate(x, y, z));		
	}
		
	public void addScale(float x, float y, float z){
		this.modifiers.add(new WidgetModifierScale(x, y, z));			
	}
		
	public void addRotate(float deg, float x, float y, float z){
		this.modifiers.add(new WidgetModifierRotate(deg, x, y, z));		
	}
		
	public void addColor(float r, float g, float b, float alpha){
		this.modifiers.add(new WidgetModifierColor(r, g, b, alpha));	
	}
	
	public void remove(int element){
		this.modifiers.remove(element);		
	}
		
	public short getType(int element){
		return this.modifiers.get(element).getType();	
	}
	
	public int getCurrentColor(){
		for(int i=0, count = this.modifiers.size(); i < count; i++){
			if(this.modifiers.get(i).getType() == WidgetModifierType.COLOR){
				float[] color = this.modifiers.get(i).getValues();
				return OGUtils.getIntFromColor(color[0], color[1], color[2], color[3]);
			}
		}
		return OGUtils.getIntFromColor(1, 1, 1, 1);
	}
		
	public void apply(EntityPlayer player, boolean overlayActive){
		for(int i=0, count = this.modifiers.size(); i < count; i++) 
			this.modifiers.get(i).apply(player, overlayActive);
	}
		
	public void writeData(ByteBuf buff){
		int modifierCount = this.modifiers.size();
		buff.writeInt(modifierCount);
		for(int i=0; i < modifierCount; i++) {
			buff.writeShort(this.modifiers.get(i).getType());
			this.modifiers.get(i).writeData(buff);
		}
	}
		
	public void readData(ByteBuf buff){
		this.modifiers.clear();
		for(int i = 0, modifierCount = buff.readInt(); i < modifierCount; i++){
			switch(buff.readShort()){
				case WidgetModifierType.TRANSLATE: this.addTranslate(0F, 0F, 0F); break;
				case WidgetModifierType.COLOR: this.addColor(0F, 0F, 0F, 0F); break;
				case WidgetModifierType.SCALE: this.addScale(0F, 0F, 0F); break;
				case WidgetModifierType.ROTATE: this.addRotate(0F, 0F, 0F, 0F); break;
				//case WidgetModifierType.TEXTURE: addTexture(); break;
				default: this.remove(i); return; //remove modifier if we get bs
			}
			this.modifiers.get(i).readData(buff);
		}
	}		
}
