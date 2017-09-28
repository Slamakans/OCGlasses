package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.widgets.core.modifiers.*;
import com.bymarcin.openglasses.utils.Location;
import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;

import com.bymarcin.openglasses.utils.OGUtils;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class WidgetModifiers {
	public ArrayList<WidgetModifier> modifiers = new ArrayList<WidgetModifier>();
	public long lastConditionStates;
	private Location lastOffset = new Location();
	
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
	
	public int getCurrentColor(long conditionStates, int index){
		float[] col = getCurrentColorFloat(conditionStates, index);
		return OGUtils.getIntFromColor(col[0], col[1], col[2], col[3]);
	}

	public float[] getCurrentColorFloat(int index){
		return getCurrentColorFloat(this.lastConditionStates, index);
	}

	public float[] getCurrentColorFloat(long conditionStates, int index){
		for(int i=this.modifiers.size() - 1; i >= 0; i--){
			if(this.modifiers.get(i).getType() == WidgetModifierType.COLOR &&
				this.modifiers.get(i).shouldApplyModifier(conditionStates) == true){					
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

	public float[] getCurrentScaleFloat(long conditionStates){
		float scaleX = 1, scaleY = 1, scaleZ = 1;
		for(int i=0; i < this.modifiers.size(); i++){
			if(this.modifiers.get(i).getType() == WidgetModifierType.SCALE && this.modifiers.get(i).shouldApplyModifier(conditionStates) == true){
				Object[] scale = this.modifiers.get(i).getValues();
				scaleX *= (float) scale[0];
				scaleY *= (float) scale[1];
				scaleZ *= (float) scale[2];
			}
		}
		return new float[]{ scaleX, scaleY, scaleZ };
	}

	public void apply(long conditionStates){
		this.lastConditionStates = conditionStates;
		for(int i=0, count = this.modifiers.size(); i < count; i++) 
			this.modifiers.get(i).apply(conditionStates);
	}

	public BlockPos getRenderPosition(long conditionStates, Location offset){
		Vector4f renderPosition = this.generateGlMatrix(conditionStates);
		this.lastOffset = offset;
		renderPosition.x += offset.x;
		renderPosition.y += offset.y;
		renderPosition.z += offset.z;

		return new BlockPos(renderPosition.x, renderPosition.y, renderPosition.z);
	}

	public BlockPos getRenderPosition(String ForPlayerName){
		EntityPlayer player;

		if(Minecraft.getMinecraft().world.isRemote)
			player = Minecraft.getMinecraft().player;
		else
			player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(ForPlayerName);

		long conditions = ClientSurface.instances.getConditionStates(player);
		return this.getRenderPosition(conditions, this.lastOffset);
	}

	public Vector4f generateGlMatrix(long conditionStates){
		Matrix4f m = new Matrix4f();
		Object[] b;
		for(int i=0, count = this.modifiers.size(); i < count; i++)
			if(this.modifiers.get(i).shouldApplyModifier(conditionStates)) switch(this.modifiers.get(i).getType()){
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
