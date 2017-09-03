package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetModifier;

import com.bymarcin.openglasses.surface.RenderType;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bymarcin.openglasses.surface.widgets.component.world.FloatingText;
import com.bymarcin.openglasses.surface.widgets.component.face.Text;
import com.bymarcin.openglasses.surface.widgets.component.face.ItemIcon;

import com.bymarcin.openglasses.surface.widgets.core.attribute.IResizable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IPrivate;

import com.bymarcin.openglasses.surface.WidgetType;

import com.bymarcin.openglasses.surface.WidgetModifierType;
import com.bymarcin.openglasses.surface.WidgetModifiers;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public abstract class WidgetGLOverlay extends Widget implements IResizable, IPrivate {
	RenderType rendertype;
	
	public float width = 0, height = 0;
	
	public boolean isThroughVisibility = false;
	
	public WidgetGLOverlay(){
		this.rendertype = RenderType.GameOverlayLocated;		
	}
	
	public void writeData(ByteBuf buff) {
		WidgetModifierList.writeData(buff);
	}
	
	public void readData(ByteBuf buff) {
		WidgetModifierList.readData(buff);
	}

	public void writeDataSIZE(ByteBuf buff) {
		buff.writeFloat(this.width);
		buff.writeFloat(this.height);
	}
	
	public void readDataSIZE(ByteBuf buff) {
		this.width = buff.readFloat();
		this.height = buff.readFloat();
	}
	
	public void setSize(double w, double h) {
		this.width = (float) w;
		this.height = (float) h;
	}	
	
	public double getWidth() {
		return this.width; }

	public double getHeight() {
		return this.height; }
		
	@SideOnly(Side.CLIENT)	
	public class RenderableGLWidget implements IRenderableWidget {		
		
		boolean depthtest, texture2d, blending, smoothshading, alpha;
		boolean doBlending, doTexture, doSmoothShade, doAlpha;
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, boolean overlayActive) {}
		
		public int applyModifiers(EntityPlayer player, boolean overlayActive){ 
			depthtest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
			texture2d = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
			blending = GL11.glIsEnabled(GL11.GL_BLEND);
			//alpha = GL11.glIsEnabled(GL11.GL_ALPHA);
			
			smoothshading = false;
			doBlending = false;
			doTexture = false;
			doSmoothShade = false;
			
			 
			if(GL11.glGetInteger(GL11.GL_SHADE_MODEL) == GL11.GL_SMOOTH) 
				smoothshading = true;
			
			
			if(isThroughVisibility)
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			else
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			GL11.glDisable(GL11.GL_LIGHTING);
		
			for(int i=0, count = WidgetModifierList.modifiers.size(); i < count; i++){
				switch(WidgetModifierList.modifiers.get(i).getType()){
					case WidgetModifierType.COLOR: if((float) WidgetModifierList.modifiers.get(i).getValues()[3] < 1) doBlending = true; break;
					case WidgetModifierType.TEXTURE: doTexture = true; break;
					default: break;
				}
			}
			
			WidgetType type = getType();	
			if(type == WidgetType.BOX2D){
				doSmoothShade = true;
				doBlending = true;
				doAlpha = true;
				//GL11.glEnable(GL11.GL_ALPHA);
				//doTexture = false;
			}
			else if(type == WidgetType.FLOATINGTEXT || type == WidgetType.TEXT){
				doTexture = true;
				GL11.glDisable(GL11.GL_ALPHA_TEST);
			}
			else if(type == WidgetType.ITEMICON || type == WidgetType.ITEM3D){
				doBlending = true;
				doTexture = true;
			}
			
			/*if(doAlpha)
				GL11.glEnable(GL11.GL_ALPHA);
			else
				GL11.glDisable(GL11.GL_ALPHA);*/
						
			if(doTexture)
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			else
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				
			if(doBlending){
				GL11.glEnable(GL11.GL_BLEND);		//vertex based alpha
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
			else {
				GL11.glDisable(GL11.GL_BLEND);				
			}
			if(doSmoothShade)
				GL11.glShadeModel(GL11.GL_SMOOTH);
			else 
				GL11.glShadeModel(GL11.GL_FLAT);
			
			WidgetModifierList.apply(player, overlayActive);
			
			return WidgetModifierList.getCurrentColor(player, overlayActive, 0);
		}
		
		public float[] getCurrentColorFloat(EntityPlayer player, boolean overlayActive, int index){
			return WidgetModifierList.getCurrentColorFloat(player, overlayActive, index);
		}
		 
		
		public void revokeModifiers(){ 
			if(depthtest) 
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			else
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			if(blending) 
				GL11.glEnable(GL11.GL_BLEND);
			else
				GL11.glDisable(GL11.GL_BLEND);
				
			if(texture2d) 
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			else
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				
			if(smoothshading)
				GL11.glShadeModel(GL11.GL_SMOOTH);
			else 
				GL11.glShadeModel(GL11.GL_FLAT);
			
			/*if(alpha)
				GL11.glEnable(GL11.GL_ALPHA);
			else
				GL11.glDisable(GL11.GL_ALPHA);*/
		}
				
		@Override
		public boolean shouldWidgetBeRendered() {
			return isVisible();
		}
		
		@Override
		public UUID getWidgetOwner() {
			return getOwnerUUID();
		}
		
		@Override
		public RenderType getRenderType() {
			return rendertype;
		}
	}
}
