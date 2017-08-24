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
		writeDataXYZ(buff);
		writeDataVERTICES(buff);
		writeDataRGBA(buff);
		buff.writeBoolean(isThroughVisibility);
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataXYZ(buff);
		readDataVERTICES(buff);
		readDataRGBA(buff);
		isThroughVisibility = buff.readBoolean();
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
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			GL11.glPushMatrix();
			if(isThroughVisibility){
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}else{
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			GL11.glTranslated(x, y, z);
			GL11.glRotatef(rotationX, rotationY, rotationZ, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			GL11.glColor4f(r, g, b, alpha);
			GL11.glVertex3f(vertices[0][0], vertices[0][1], vertices[0][2]);
			GL11.glVertex3f(vertices[1][0], vertices[1][1], vertices[1][2]);
			GL11.glVertex3f(vertices[2][0], vertices[2][1], vertices[2][2]);
			GL11.glVertex3f(vertices[0][0], vertices[0][1], vertices[0][2]);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}	
	}
}
