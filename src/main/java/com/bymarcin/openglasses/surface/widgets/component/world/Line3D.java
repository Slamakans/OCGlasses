package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import java.util.UUID;
import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLWorld;
import com.bymarcin.openglasses.surface.WidgetType;

public class Line3D extends WidgetGLWorld{	
	float size;
	
	public Line3D() {
		size = 8.F;
	}
	
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
		return WidgetType.LINE3D;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderLine3D();
	}
	
	@SideOnly(Side.CLIENT)
	class RenderLine3D extends RenderableGLWidget{

		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, boolean overlayActive) {
			this.applyModifiers(player, overlayActive);
			GL11.glLineWidth(size);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(width, height, 0);
			GL11.glEnd();
			this.revokeModifiers();
		}
	}
}
