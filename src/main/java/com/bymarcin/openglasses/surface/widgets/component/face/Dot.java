package com.bymarcin.openglasses.surface.widgets.component.face;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLOverlay;
import com.bymarcin.openglasses.surface.WidgetType;

public class Dot extends WidgetGLOverlay {
	public Dot() {}
	
	@Override
	public WidgetType getType() {
		return WidgetType.DOT;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderableDot();
	}
	
	@SideOnly(Side.CLIENT)
	public class RenderableDot extends RenderableGLWidget{
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, boolean overlayActive) {
			this.applyModifiers(player, overlayActive);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(0, 1, 0);
			GL11.glVertex3f(1, 1, 0);
			GL11.glVertex3f(1, 0, 0);
			GL11.glEnd();
			this.revokeModifiers();
		}
	}	
}
