package com.bymarcin.openglasses.surface.widgets.component.world;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.bymarcin.openglasses.surface.ClientSurface;
import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLWorld;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.utils.OGUtils;


public class Cube3D extends WidgetGLWorld {
	public Cube3D() {}
	
	@Override
	public void writeData(ByteBuf buff) {
		writeDataXYZ(buff);
		writeDataROTATION(buff);
		writeDataRGBA(buff);		
		buff.writeBoolean(isThroughVisibility);
		buff.writeInt(distance);
		buff.writeInt(lookAtX);
		buff.writeInt(lookAtY);
		buff.writeInt(lookAtZ);
		buff.writeBoolean(isLookingAtEnable);
		buff.writeFloat(scale);
	}

	@Override
	public void readData(ByteBuf buff) {
		readDataXYZ(buff);
		readDataROTATION(buff);
		readDataRGBA(buff);		
		isThroughVisibility = buff.readBoolean();
		distance = buff.readInt();
		lookAtX = buff.readInt();
		lookAtY = buff.readInt();
		lookAtZ = buff.readInt();
		isLookingAtEnable = buff.readBoolean();
		scale = buff.readFloat();
	}

	@Override
	public WidgetType getType() {
		return WidgetType.CUBE3D;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderCube3D();
	}
	
	@SideOnly(Side.CLIENT)
	class RenderCube3D extends RenderableGLWidget{
		float tr = (1-scale)/2f;
		
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			if(OGUtils.inRange(playerX, playerY, playerZ, x, y, z, distance)){
				RayTraceResult pos = ClientSurface.getBlockCoordsLookingAt(player);
				if(isLookingAtEnable && (pos == null || pos.getBlockPos().getX() != lookAtX || pos.getBlockPos().getY() != lookAtY || pos.getBlockPos().getZ() != lookAtZ) )
						return;
				drawCube3D(x, y, z, alpha);				
			}
		}
		
		public void drawCube3D(float posX, float posY, float posZ, float alpha){
			if(isThroughVisibility){
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}else{
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			GL11.glPushMatrix();
			GL11.glTranslated(posX, posY, posZ);
			GL11.glTranslatef(tr,tr,tr);
			GL11.glRotatef(rotationX, rotationY, rotationZ, 0);			
			GL11.glScalef(scale, scale, scale);
			GL11.glBegin(GL11.GL_QUADS);        // Draw The Cube Using quads			    
			 	GL11.glColor4f(r,g,b,alpha);    // Color Blue
			 	GL11.glVertex3f( 1.0f, 1.0f,0f);    // Top Right Of The Quad (Top)
			 	GL11.glVertex3f(0f, 1.0f,0f);    // Top Left Of The Quad (Top)
			 	GL11.glVertex3f(0f, 1.0f, 1.0f);    // Bottom Left Of The Quad (Top)
			 	GL11.glVertex3f( 1.0f, 1.0f, 1.0f);    // Bottom Right Of The Quad (Top)

			 	GL11.glVertex3f( 1.0f,0f, 1.0f);    // Top Right Of The Quad (Bottom)
			 	GL11.glVertex3f(0f,0f, 1.0f);    // Top Left Of The Quad (Bottom)
			 	GL11.glVertex3f(0f,0f,0f);    // Bottom Left Of The Quad (Bottom)
			 	GL11.glVertex3f( 1.0f,0f,0f);    // Bottom Right Of The Quad (Bottom)
  
			 	GL11.glVertex3f( 1.0f, 1.0f, 1.0f);    // Top Right Of The Quad (Front)
			 	GL11.glVertex3f(0f, 1.0f, 1.0f);    // Top Left Of The Quad (Front)
			 	GL11.glVertex3f(0f,0f, 1.0f);    // Bottom Left Of The Quad (Front)
			 	GL11.glVertex3f( 1.0f,0f, 1.0f);    // Bottom Right Of The Quad (Front)

			    GL11.glVertex3f( 1.0f,0f,0f);    // Top Right Of The Quad (Back)
			    GL11.glVertex3f(0f,0f,0f);    // Top Left Of The Quad (Back)
			    GL11.glVertex3f(0f, 1.0f,0f);    // Bottom Left Of The Quad (Back)
			    GL11.glVertex3f( 1.0f, 1.0f,0f);    // Bottom Right Of The Quad (Back)

			    GL11.glVertex3f(0f, 1.0f, 1.0f);    // Top Right Of The Quad (Left)
			    GL11.glVertex3f(0f, 1.0f,0f);    // Top Left Of The Quad (Left)
			    GL11.glVertex3f(0f,0f,0f);    // Bottom Left Of The Quad (Left)
			    GL11.glVertex3f(0f,0f, 1.0f);    // Bottom Right Of The Quad (Left)

			    GL11.glVertex3f( 1.0f, 1.0f,0f);    // Top Right Of The Quad (Right)
			    GL11.glVertex3f( 1.0f, 1.0f, 1.0f);    // Top Left Of The Quad (Right)
			    GL11.glVertex3f( 1.0f,0f, 1.0f);    // Bottom Left Of The Quad (Right)
			    GL11.glVertex3f( 1.0f,0f,0f);    // Bottom Right Of The Quad (Right)			
		    GL11.glEnd();            // End Drawing The Cube
		    GL11.glPopMatrix();
		    GL11.glEnable(GL11.GL_TEXTURE_2D);
		    GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}
}
