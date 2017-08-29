package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.WidgetGLWorld;
import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetType;

public class Quad3D extends Triangle3D{
	public Quad3D() {}

	@Override
	public WidgetType getType() {
		return WidgetType.QUAD3D;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderQuad3D();
	}
	
	@SideOnly(Side.CLIENT)
	class RenderQuad3D extends RenderableGLWidget{

		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			this.setupDepthTest();						
			GL11.glTranslatef(x, y, z);
			this.applyRotation();
			GL11.glColor4f(r,g,b,alpha);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_QUADS);			
			GL11.glVertex3f(x, y, z);
			GL11.glVertex3f(x+width, y, z);
			GL11.glVertex3f(x, y+height, z);
			GL11.glVertex3f(x+width, y+height, z);
			GL11.glEnd();
		}
	}
}
