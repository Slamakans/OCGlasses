package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.WidgetModifier;
import com.bymarcin.openglasses.surface.WidgetModifierType;

import com.bymarcin.openglasses.surface.widgets.core.modifiers.*;
import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bymarcin.openglasses.utils.OGUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class WidgetModifiers {
	public ArrayList<WidgetModifier> modifiers = new ArrayList<WidgetModifier>();
	
	
	public void setCondition(int modifierIndex, short conditionIndex, boolean state, short lightLevel){
		switch(conditionIndex){
		  case 1: this.modifiers.get(modifierIndex).condition_lightlevel_min = lightLevel; break;
		  case 2: this.modifiers.get(modifierIndex).condition_lightlevel_max = lightLevel; break;
		}
		this.modifiers.get(modifierIndex).configureCondition(conditionIndex, state);
	}
	
	public void setCondition(int modifierIndex, short conditionIndex, boolean state){
		this.modifiers.get(modifierIndex).configureCondition(conditionIndex, state);
	}
	
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
	
	public void addTexture(String texloc) {
		this.modifiers.add(new WidgetModifierTexture(texloc));
	}
	
	public void remove(int element){
		this.modifiers.remove(element);		
	}
		
	public short getType(int element){
		return this.modifiers.get(element).getType();	
	}
	
	public int getCurrentColor(EntityPlayer player, boolean overlayActive, int index){
		float[] col = getCurrentColorFloat(player, overlayActive, index);
		return OGUtils.getIntFromColor(col[0], col[1], col[2], col[3]);
	}
	
	public float[] getCurrentColorFloat(EntityPlayer player, boolean overlayActive, int index){
		for(int i=this.modifiers.size() - 1; i >= 0; i--){
			if(this.modifiers.get(i).getType() == WidgetModifierType.COLOR &&
				this.modifiers.get(i).shouldApplyModifier(player, overlayActive) == true){					
				if(index > 0){
					index--;
				}
				else {
					Object[] color = this.modifiers.get(i).getValues();
					return new float[]{ (float) color[0], (float) color[1], (float) color[2], (float) color[3] };
				}
			}
		}
		return new float[]{ 1, 1, 1, 1 };
	}	
	
	public void apply(EntityPlayer player, boolean overlayActive){
		for(int i=0, count = this.modifiers.size(); i < count; i++) 
			this.modifiers.get(i).apply(player, overlayActive);
	}
	
	public Vector4f calcPosition(EntityPlayer player, boolean overlayActive){
		Matrix4f m = new Matrix4f();
		Object[] b;
		for(int i=0, count = this.modifiers.size(); i < count; i++)
			if(this.modifiers.get(i).shouldApplyModifier(player, overlayActive)) switch(this.modifiers.get(i).getType()){
				case WidgetModifierType.TRANSLATE: 
					b = this.modifiers.get(i).getValues();
					m.translate(new Vector3f((float) b[0], (float) b[1], (float) b[2])); 
					break;
				case WidgetModifierType.SCALE: 
					b = this.modifiers.get(i).getValues();
					m.scale(new Vector3f((float) b[0], (float) b[1], (float) b[2])); 
					break;
				case WidgetModifierType.ROTATE: 
					b = this.modifiers.get(i).getValues();
					m.rotate((float) b[0], new Vector3f((float) b[1], (float) b[2], (float) b[3])); 					
					break;
		}
		
		return m.transform(m, new Vector4f(0F, 0F, 0F, 1F), null);
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
		ArrayList<WidgetModifier> modifiersNew = new ArrayList<WidgetModifier>();
		for(int i = 0, modifierCount = buff.readInt(); i < modifierCount; i++){
			switch(buff.readShort()){
				case WidgetModifierType.TRANSLATE: modifiersNew.add(new WidgetModifierTranslate(0F, 0F, 0F)); break;
				case WidgetModifierType.COLOR: modifiersNew.add(new WidgetModifierColor(0F, 0F, 0F, 0F)); break;
				case WidgetModifierType.SCALE: modifiersNew.add(new WidgetModifierScale(0F, 0F, 0F)); break;
				case WidgetModifierType.ROTATE: modifiersNew.add(new WidgetModifierRotate(0F, 0F, 0F, 0F)); break;
				case WidgetModifierType.TEXTURE: modifiersNew.add(new WidgetModifierTexture(null)); break;
				default: modifiersNew.remove(i); return; //remove modifier if we get bs
			}
			modifiersNew.get(i).readData(buff);
		}
		
		this.modifiers = modifiersNew;
	}		
}
