package com.bymarcin.openglasses.surface.widgets.component.face;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IPrivate;

import org.lwjgl.opengl.GL11;

import java.util.UUID;
import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.RenderType;
import com.bymarcin.openglasses.surface.Widget;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IAlpha;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IColorizable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IPositionable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IResizable;


public class SquareWidget extends Widget implements IPositionable, IResizable, IColorizable, IAlpha, IPrivate{

	float x;
	float y;
	float width;
	float height;
	float alpha = 1;
	float r;
	float g;
	float b;
	
	public SquareWidget() {
	}

	@Override
	public void writeData(ByteBuf buff) {
		buff.writeFloat(x);
		buff.writeFloat(y);
		buff.writeFloat(r);
		buff.writeFloat(g);
		buff.writeFloat(b);
		buff.writeFloat(height);
		buff.writeFloat(width);
		buff.writeFloat(alpha);
	}

	@Override
	public void readData(ByteBuf buff) {
		x = buff.readFloat();
		y = buff.readFloat();
		r = buff.readFloat();
		g = buff.readFloat();
		b = buff.readFloat();
		height = buff.readFloat();
		width = buff.readFloat();
		alpha = buff.readFloat();
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
	public class RenderableSquareWidget implements IRenderableWidget{
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);

				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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
		
		@Override
		public RenderType getRenderType() {
			return RenderType.GameOverlayLocated;
		}

		@Override
		public boolean shouldWidgetBeRendered() {
			return isVisible();
		}
		
		@Override
		public UUID getWidgetOwner() {
			return getOwnerUUID();
		}
	}

	@Override
	public double getPosX() {
		return x;
	}

	@Override
	public double getPosY() {
		return y;
	}

	@Override
	public void setPos(double x, double y) {
		this.x = (float) x;
		this.y = (float) y;
	}

	@Override
	public void setSize(double width, double height) {
		this.height = (float) height;
		this.width = (float) width;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setColor(double r, double g, double b) {
		this.r = (float) r;
		this.g = (float) g;
		this.b = (float) b;
	}

	@Override
	public float getColorR() {
		return r;
	}

	@Override
	public float getColorG() {
		return g;
	}

	@Override
	public float getColorB() {
		return b;
	}

	@Override
	public float getAlpha() {
		return alpha;
	}

	@Override
	public void setAlpha(double alpha) {
		this.alpha = (float) alpha;
	}

}
