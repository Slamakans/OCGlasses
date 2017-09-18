package com.bymarcin.openglasses.surface.widgets.component.face;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLOverlay;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.utils.Location;
public class SquareWidget extends WidgetGLOverlay {
	public SquareWidget() {}

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
		public void render(EntityPlayer player, Location glassesTerminalLocation, long conditionStates) {
			this.applyModifiers(player, glassesTerminalLocation, conditionStates);
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
