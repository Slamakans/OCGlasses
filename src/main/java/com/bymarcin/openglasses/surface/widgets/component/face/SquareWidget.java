package com.bymarcin.openglasses.surface.widgets.component.face;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLOverlay;
import com.bymarcin.openglasses.surface.WidgetType;

public class SquareWidget extends WidgetGLOverlay {
	public SquareWidget() {}

	@Override
	public void writeData(ByteBuf buff) {
		writeDataXYZ(buff);
		writeDataRGBA(buff);
		writeDataSIZE(buff);		
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataXYZ(buff);
		readDataRGBA(buff);
		readDataSIZE(buff);
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.SQUARE;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return this.new RenderableSquareWidget();
	}
	
	@SideOnly(Side.CLIENT)
	public class RenderableSquareWidget extends RenderableGLWidget{
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
				GL11.glVertex3f(x, y + height, 0);
				GL11.glVertex3f(x + width, y + height, 0);
				GL11.glVertex3f(x + width, y + 0, 0);
				GL11.glEnd();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
	}
}
