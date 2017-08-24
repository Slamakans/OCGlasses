package com.bymarcin.openglasses.surface.widgets.component.face;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.surface.WidgetGLOverlay;

public class Quad extends TriangleWidget {
	public Quad() {	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderQuad();
	}
	
	@SideOnly(Side.CLIENT)
	class RenderQuad extends RenderableGLWidget{

		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTranslated(x, y, z);
			GL11.glRotatef(rotationX, rotationY, rotationZ, 1);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4f(r, g, b, alpha);
			GL11.glVertex3f(vertices[0][0], vertices[0][1], 0);
			GL11.glVertex3f(vertices[1][0], vertices[1][1], 0);
			GL11.glVertex3f(vertices[2][0], vertices[2][1], 0);
			GL11.glVertex3f(vertices[3][0], vertices[3][1], 0);
			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);			
		}
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.QUAD;
	}
}
