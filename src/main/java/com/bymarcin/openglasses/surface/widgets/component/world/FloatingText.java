package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.ClientSurface;
import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLWorld;
import com.bymarcin.openglasses.surface.widgets.core.attribute.ITextable;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.utils.OGUtils;

import net.minecraft.client.renderer.GlStateManager;

public class FloatingText extends WidgetGLWorld implements ITextable{
	String text ="";
	
	public FloatingText() {
		scale = 0.1F;
	}
	
	@Override
	public void writeData(ByteBuf buff) {
		writeDataXYZ(buff);
		writeDataSCALE(buff);
		writeDataRGBA(buff);
		writeDataWORLD(buff);
		ByteBufUtils.writeUTF8String(buff, text);		
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataXYZ(buff);
		readDataSCALE(buff);
		readDataRGBA(buff);
		readDataWORLD(buff);
		text = ByteBufUtils.readUTF8String(buff);
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.FLOATINGTEXT;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderableFloatingText();
	}
	
	@SideOnly(Side.CLIENT)
	class RenderableFloatingText extends RenderableGLWidget{
		
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			if(!OGUtils.inRange(playerX, playerY, playerZ, x, y, z, viewDistance))
				return;
			
			if(isLookingAtEnable && !OGUtils.isLookingAt(ClientSurface.getBlockCoordsLookingAt(player), new float[]{lookAtX, lookAtY, lookAtZ})){
				return;				
			}
			
			if(text.length() < 1) return;
			
			FontRenderer fontRender = Minecraft.getMinecraft().fontRendererObj;			
		
			double offsetX = fontRender.getStringWidth(text)/2D;
			double offsetY = fontRender.FONT_HEIGHT/2D;
			
			if(isThroughVisibility)
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			else
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			GL11.glTranslatef(x, y, z);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glScalef(scale, scale, scale);			
			
			//align and rotate text facing the player
			GL11.glTranslated(offsetX, offsetY, 0.0D);
			GL11.glRotated(180.0D, 0.0D, 0.0D, 1.0D);
			GL11.glTranslated(offsetX, offsetY, 0.0D);
			GL11.glRotated(player.rotationYaw,0.0D,1.0D,0.0D);
			GL11.glRotated(-player.rotationPitch,1.0D,0.0D,0.0D);
			GL11.glTranslated(-offsetX, -offsetY, 0.0D);
			
			fontRender.drawString(text, 0, 0, OGUtils.getIntFromColor(r, g, b, alpha));
			GlStateManager.disableAlpha();
		}
	}

	@Override
	public void setText(String text) {
		this.text = text;	
	}
	
	@Override
	public String getText() {
		return text;
	}
}
