package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLWorld;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.utils.Location;
public class Dot3D extends WidgetGLWorld  {
	public Dot3D() {
		width = 1;
		height = 1;
	}
	
	@Override
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		writeDataSIZE(buff);
	}

	@Override
	public void readData(ByteBuf buff) {
		super.readData(buff);
		readDataSIZE(buff);
	}

	@Override
	public WidgetType getType() {
		return WidgetType.DOT3D;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderDot3D();
	}
	
	@SideOnly(Side.CLIENT)
	class RenderDot3D extends RenderableGLWidget{
		@Override
		public void render(EntityPlayer player, Location glassesTerminalLocation, long conditionStates) {
			this.applyModifiers(player, glassesTerminalLocation, conditionStates);
			GL11.glRotated(-player.rotationYaw,0,1,0);
			GL11.glRotated(player.rotationPitch,1,0,0);
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
