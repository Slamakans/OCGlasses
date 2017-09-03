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
	public Quad3D() {
		width = 1;
		height = 1;
	}

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
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, boolean overlayActive) {
			this.applyModifiers(player, overlayActive);
			GL11.glBegin(GL11.GL_QUADS);			
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(0, height, 0);
			GL11.glVertex3f(width, height, 0);
			GL11.glVertex3f(width, 0, 0);
			GL11.glEnd();
			this.revokeModifiers();
		}
	}
}
