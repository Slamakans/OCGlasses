package com.bymarcin.openglasses.surface.widgets.component.face;

import com.bymarcin.openglasses.surface.WidgetGLOverlay;
import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.surface.widgets.core.attribute.ITextable;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class Text extends Dot implements ITextable{
	String text="";

	public Text() {}

	@Override
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		ByteBufUtils.writeUTF8String(buff, text);
	}

	@Override
	public void readData(ByteBuf buff) {
		super.readData(buff);
		text = ByteBufUtils.readUTF8String(buff);
	}

	@Override
	public WidgetType getType() {
		return WidgetType.TEXT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderableWidget getRenderable() {
		return new RenderText();
	}
	
	class RenderText extends RenderableGLWidget{
		@Override
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glRotated(rotationX, rotationY, rotationZ, 1);
			GL11.glScaled(size, size, 0);
			GL11.glColor4f(r,g,b,alpha);
			Minecraft.getMinecraft().fontRendererObj.drawString(text, (int) x, (int) y, -1);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void setText(String text) {
		this.text = text;	
	}

	@Override
	public String getText() {
		return text;
	}
}
