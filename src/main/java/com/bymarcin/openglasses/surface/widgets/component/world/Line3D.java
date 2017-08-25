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
		writeDataRGBA(buff);
		writeDataSCALE(buff);
		writeDataSIZE(buff);
		writeDataWORLD(buff);
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataRGBA(buff);
		readDataSCALE(buff);
		readDataSIZE(buff);
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
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			GL11.glTranslated(x, y, z);
			GL11.glRotatef(rotationX, rotationY, rotationZ, 1);
			
			if(isThroughVisibility)
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			else
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glLineWidth(size);
			GL11.glColor4f(r,g,b,alpha);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(x, y, z);
			GL11.glVertex3f(x+width, y+height, z);
			GL11.glEnd();			
		}
	}
}
