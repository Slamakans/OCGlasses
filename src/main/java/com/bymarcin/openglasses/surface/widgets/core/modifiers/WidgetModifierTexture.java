package com.bymarcin.openglasses.surface.widgets.core.modifiers;

import com.bymarcin.openglasses.surface.WidgetModifier;
import com.bymarcin.openglasses.surface.WidgetModifierType;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import io.netty.buffer.ByteBuf;

import net.minecraft.util.ResourceLocation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.common.network.ByteBufUtils;


public class WidgetModifierTexture extends WidgetModifier {
	ResourceLocation textureLocation;
	String textureName = "";
	TextureAtlasSprite tex;
	public WidgetModifierTexture(String texloc){
		setupTexture(texloc);	
	}
	
	
	public void setupTexture(String texloc){
		if(texloc == null) return;
		
		//this.textureName = texloc;
		//this.textureLocation = ResourceUtil.getModelTexture(texloc);
		//this.tex = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(this.textureName);		
		//this.textureLocation = Minecraft.getMinecraft().getTextureManager().getResource(texloc);	
	}
		
	public void apply(EntityPlayer player, boolean overlayActive){	
		if(!shouldApplyModifier(player, overlayActive)) return;
		
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture(imgFile));
		
		Minecraft mc = Minecraft.getMinecraft();
		TextureAtlasSprite tex = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry("minecraft:blocks/stone");		
		//Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(tex.getIconName()));
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		//mc.getTextureManager().bindTexture(textureLocation);		
	}
	
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		ByteBufUtils.writeUTF8String(buff, this.textureName);
	}
	
	public void readData(ByteBuf buff) {
		super.readData(buff);
		setupTexture(ByteBufUtils.readUTF8String(buff));
	}
	
	public short getType(){
		return WidgetModifierType.TEXTURE;
	}
	
	public Object[] getValues(){
		return new Object[]{ this.textureName };
	}
}
