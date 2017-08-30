package com.bymarcin.openglasses.surface.widgets.component.face;

import com.bymarcin.openglasses.surface.WidgetGLOverlay;
import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.surface.widgets.core.attribute.ITextable;
import com.bymarcin.openglasses.utils.OGUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import com.bymarcin.openglasses.utils.OGUtils;

public class Text extends Dot implements ITextable{
	String text="";

	public Text() {}

	@Override
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		ByteBufUtils.writeUTF8String(buff, this.text);
	}

	@Override
	public void readData(ByteBuf buff) {
		super.readData(buff);
		this.text = ByteBufUtils.readUTF8String(buff);
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
		public void render(EntityPlayer player, double playerX, double playerY, double playerZ, boolean overlayActive) {
			int currentColor = this.applyModifiers(player, overlayActive);
			Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, 0, currentColor);
			GlStateManager.disableAlpha();
			this.revokeModifiers();
		}
	}

	@Override
	public void setText(String text) {
		this.text = text;	
	}

	@Override
	public String getText() {
		return this.text;
	}
}
