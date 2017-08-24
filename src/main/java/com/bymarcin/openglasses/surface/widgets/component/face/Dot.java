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
	public void writeData(ByteBuf buff) {
		writeDataROTATION(buff);
		writeDataXYZ(buff);
		writeDataRGBA(buff);
		writeDataSIZE(buff);	
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataROTATION(buff);
		readDataXYZ(buff);
		readDataRGBA(buff);
		readDataSIZE(buff);	
	}
	
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
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTranslated(x, y, z);
			GL11.glRotatef(rotationX, rotationY, rotationZ, 1);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4f(r, g, b, alpha);
			GL11.glVertex3f(x, y, 0);
			GL11.glVertex3f(x, y+size, 0);
			GL11.glVertex3f(x+size, y+size, 0);
			GL11.glVertex3f(x+size, y, 0);
			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
	}	
}
