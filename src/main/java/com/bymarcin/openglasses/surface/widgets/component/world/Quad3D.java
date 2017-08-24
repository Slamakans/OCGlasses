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
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glRotatef(rotationX, rotationY, rotationZ, 1);

			if(isThroughVisibility){
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}else{
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			GL11.glColor4f(r,g,b,alpha);
			
			GL11.glVertex3f(vertices[0][0], vertices[0][1], vertices[0][2]);
			GL11.glVertex3f(vertices[1][0], vertices[1][1], vertices[1][2]);
			GL11.glVertex3f(vertices[2][0], vertices[2][1], vertices[2][2]);
			GL11.glVertex3f(vertices[3][0], vertices[3][1], vertices[3][2]);			
			GL11.glVertex3f(vertices[0][0], vertices[0][1], vertices[0][2]);
			GL11.glVertex3f(vertices[1][0], vertices[1][1], vertices[1][2]);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}
}
