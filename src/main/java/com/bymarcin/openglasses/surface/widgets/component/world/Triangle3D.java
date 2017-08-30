package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.surface.WidgetGLWorld;

public class Triangle3D extends WidgetGLWorld {
	public Triangle3D() {}
	
	@Override
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		writeDataWORLD(buff);
	}

	@Override
	public void readData(ByteBuf buff) {
		super.readData(buff);
		readDataWORLD(buff);
	}

	@Override
	public WidgetType getType() {
		return WidgetType.TRIANGLE3D;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderTriangle3D();
	}
	
	@SideOnly(Side.CLIENT)
	class RenderTriangle3D extends RenderableGLWidget{
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, boolean overlayActive) {
			this.applyModifiers(player, overlayActive);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(width, 0, 0);
			GL11.glVertex3f(0, height, 0);
			GL11.glEnd();
			this.revokeModifiers();
		}	
	}
}
