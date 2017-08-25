package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.RenderType;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bymarcin.openglasses.surface.widgets.core.attribute.IPositionable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IColorizable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IAlpha;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IScalable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IResizable;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IPrivate;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IRotateable;

import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import java.util.UUID;

public abstract class WidgetGLOverlay extends Widget implements IPositionable, IColorizable, IAlpha, IScalable, IResizable, IPrivate, IRotateable {
	RenderType rendertype;
	
	public float scale = 1;
	public float x = 0, y = 0, z = 0;
	
	public float r = 1, g = 1, b = 1, alpha = 1, alphaHUD = 1;	
	public float width = 0, height = 0;
	public float rotationX = 0, rotationY = 0, rotationZ = 0;
	
	public WidgetGLOverlay(){
		this.rendertype = RenderType.GameOverlayLocated;		
	}
	
	public void writeDataXYZ(ByteBuf buff) {
		buff.writeFloat(this.x);
		buff.writeFloat(this.y);
		buff.writeFloat(this.z);
	}
	
	public void readDataXYZ(ByteBuf buff) {
		this.x = buff.readFloat();
		this.y = buff.readFloat();
		this.z = buff.readFloat();
	}
	
	public void writeDataROTATION(ByteBuf buff) {
		buff.writeFloat(this.rotationX);
		buff.writeFloat(this.rotationY);
		buff.writeFloat(this.rotationZ);
	}
	
	public void readDataROTATION(ByteBuf buff) {
		this.rotationX = buff.readFloat();
		this.rotationY = buff.readFloat();
		this.rotationZ = buff.readFloat();
	}
	
	public void writeDataRGBA(ByteBuf buff) {
		buff.writeFloat(this.alpha);
		buff.writeFloat(this.alphaHUD);
		buff.writeFloat(this.r);
		buff.writeFloat(this.g);
		buff.writeFloat(this.b);		
	}

	public void readDataRGBA(ByteBuf buff) {
		this.alpha = buff.readFloat();
		this.alphaHUD = buff.readFloat();
		this.r = buff.readFloat();
		this.g = buff.readFloat();
		this.b = buff.readFloat();
	}

	public void writeDataSIZE(ByteBuf buff) {
		buff.writeFloat(this.width);
		buff.writeFloat(this.height);
	}
	
	public void readDataSIZE(ByteBuf buff) {
		this.width = buff.readFloat();
		this.height = buff.readFloat();
	}
	
	public void writeDataSCALE(ByteBuf buff) {
		buff.writeFloat(this.scale);
	}
	
	public void readDataSCALE(ByteBuf buff) {
		this.scale = buff.readFloat();
	}
	
	public void setPos(double x, double y) {
		this.x = (float) x;
		this.y = (float) y;
	}
	
	public void setPos(double x, double y, double z) {
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
	}	
	
	public void setSize(double w, double h) {
		this.width = (float) w;
		this.height = (float) h;
	}
	
	public void setColor(double r, double g, double b) {
		this.r = (float) r;
		this.g = (float) g;
		this.b = (float) b;
	}
	
	public void setRotation(float rx, float ry, float rz){
		rotationX = rx;
		rotationY = ry;
		rotationZ = rz;
	}
	
	public double getPosX() {
		return x; }

	public double getPosY() {
		return y; }
		
	public double getPosZ() {
		return z; }

	public float getColorR() {
		return r; }

	public float getColorG() {
		return g; }

	public float getColorB() {
		return b; }

	public float getAlpha() {
		return alpha; }

	public void setAlpha(double alpha) {
		this.alpha = (float) alpha;	}

	public float getAlphaHUD() {
		return this.alphaHUD; }

	public void setAlphaHUD(double alphaHUD) {
		this.alphaHUD = (float) alphaHUD; }

	public void setScale(double scale) {
		this.scale = (float) scale; }

	public double getScale() {
		return this.scale; }
	
	public float getRotationX(){
		return this.rotationX; }
	
	public float getRotationY(){
		return this.rotationY; }
	
	public float getRotationZ(){
		return this.rotationZ; }
	
	public double getWidth() {
		return this.width; }

	public double getHeight() {
		return this.height; }
		
	@SideOnly(Side.CLIENT)	
	public class RenderableGLWidget implements IRenderableWidget {		
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {}
		
		@Override
		public boolean shouldWidgetBeRendered() {
			return isVisible();
		}
		
		@Override
		public UUID getWidgetOwner() {
			return getOwnerUUID();
		}
		
		@Override
		public float getAlpha(boolean HUDactive){
			if(HUDactive)
				return alphaHUD;
			return alpha;
		}
		
		@Override
		public RenderType getRenderType() {
			return rendertype;
		}
	}
}
