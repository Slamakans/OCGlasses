package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

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

public class FloatingText extends WidgetGLWorld implements ITextable{
	String text ="";
	
	public FloatingText() {}
	
	@Override
	public void writeData(ByteBuf buff) {
		writeDataXYZ(buff);
		writeDataRGBA(buff);
		ByteBufUtils.writeUTF8String(buff, text);
		
		buff.writeBoolean(isThroughVisibility);
		buff.writeInt(lookAtX);
		buff.writeInt(lookAtY);
		buff.writeInt(lookAtZ);
		buff.writeBoolean(isLookingAtEnable);
		buff.writeInt(viewDistance);
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataXYZ(buff);
		readDataRGBA(buff);
		text = ByteBufUtils.readUTF8String(buff);
		isThroughVisibility = buff.readBoolean();
		lookAtX = buff.readInt();
		lookAtY = buff.readInt();
		lookAtZ = buff.readInt();
		isLookingAtEnable = buff.readBoolean();
		viewDistance = buff.readInt();
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
		FontRenderer fontRender = Minecraft.getMinecraft().fontRendererObj;
		double offsetX = fontRender.getStringWidth(text)/2D;
		double offsetY = fontRender.FONT_HEIGHT/2D;
		int color = OGUtils.getIntFromColor(r, g, b, alpha);
		
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			if(!OGUtils.inRange(playerX, playerY, playerZ, x, y, z, viewDistance)){
				return;
			}
			if(isLookingAtEnable){
				RayTraceResult pos = ClientSurface.getBlockCoordsLookingAt(player);
				if(pos == null || pos.getBlockPos().getX() != lookAtX || pos.getBlockPos().getY() != lookAtY || pos.getBlockPos().getZ() != lookAtZ)
					return;
			}
			GL11.glPushMatrix();
			if(isThroughVisibility){
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}else{
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			GL11.glTranslated(x, y, z);
			GL11.glRotated(rotationX, rotationY, rotationZ, 0);
			GL11.glScaled(scale, scale, scale);
			GL11.glTranslated(offsetX, offsetY, 0);
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 1);
			
			GL11.glTranslated(offsetX, offsetY , 0);
			
			GL11.glRotated(player.rotationYaw,0,1,0);
			GL11.glRotated(-player.rotationPitch,1,0,0);

			GL11.glTranslated(-offsetX, -fontRender.FONT_HEIGHT/2D , 0);
			
			fontRender.drawString(text, 0, 0, color);
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
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
