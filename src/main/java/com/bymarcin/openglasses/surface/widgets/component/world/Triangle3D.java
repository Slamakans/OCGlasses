package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.surface.WidgetGLWorld;
import com.bymarcin.openglasses.utils.Location;
public class Triangle3D extends WidgetGLWorld {
	public Triangle3D() {
		width = 1;
		height = 1;
	}
	
	@Override
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		writeDataSIZE(buff);
		writeDataWORLD(buff);
	}

	@Override
	public void readData(ByteBuf buff) {
		super.readData(buff);
		readDataSIZE(buff);
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
		public void render(EntityPlayer player, Location glassesTerminalLocation, long conditionStates) {
			this.applyModifiers(player, glassesTerminalLocation, conditionStates);
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(0, height, 0);
			GL11.glVertex3f(width, height, 0);
			GL11.glEnd();
			this.revokeModifiers();
		}	
	}
}
