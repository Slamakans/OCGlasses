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
import com.bymarcin.openglasses.utils.OGUtils;

public class Dot3D extends WidgetGLWorld  {
	public Dot3D() {}
	
	@Override
	public void writeData(ByteBuf buff) {
		writeDataVERTICES(buff);
		writeDataRGBA(buff);
		writeDataXYZ(buff);
		buff.writeBoolean(isThroughVisibility);
		buff.writeInt(distance);
		buff.writeFloat(size);
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataVERTICES(buff);
		readDataRGBA(buff);
		readDataXYZ(buff);
		isThroughVisibility = buff.readBoolean();
		distance = buff.readInt();
		size = buff.readFloat();
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
		final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			
			if(!OGUtils.inRange(playerX, playerY, playerZ, x, y, z, distance)){
				return;
			}
			
			GL11.glPushMatrix();
			if(isThroughVisibility){
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}else{
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glTranslated(-x, -y, -z);
			GL11.glRotatef(-rotationX, -rotationY, -rotationZ, 1);

			
			GL11.glRotated(-player.rotationYaw,0,1,0);
			GL11.glRotated(player.rotationPitch,1,0,0);
			
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4f(r,g,b,alpha);
			
			GL11.glVertex3f(size/2, size/2, 0);
			GL11.glVertex3f(size/2, -size/2, 0);
			GL11.glVertex3f(-size/2, -size/2, 0);
			GL11.glVertex3f(-size/2, size/2, 0);
	
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			

		}
	}
}
