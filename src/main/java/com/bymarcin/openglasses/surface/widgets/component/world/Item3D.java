package com.bymarcin.openglasses.surface.widgets.component.face;

import com.bymarcin.openglasses.surface.IRenderableWidget;
import com.bymarcin.openglasses.surface.WidgetGLWorld;
import com.bymarcin.openglasses.surface.WidgetType;
import com.bymarcin.openglasses.surface.widgets.core.attribute.IItem;
import com.bymarcin.openglasses.utils.Location;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.awt.Color;

public class Item3D extends WidgetGLWorld implements IItem{
	ItemStack itmStack = null;
	
	@Override
	public void writeData(ByteBuf buff) {
		super.writeData(buff);
		
		int itemid = 0;
		if(itmStack != null)
			itemid = Item.getIdFromItem(itmStack.getItem());
				
		buff.writeInt(itemid);	
	}

	@Override
	public void readData(ByteBuf buff) {
		super.readData(buff);

		int itemid = buff.readInt();
		if(itemid > 0)
			setItem(new ItemStack(new Item().getItemById(itemid)));
	}

	@Override
	public WidgetType getType() {
		return WidgetType.ITEM3D;
	}

	@SideOnly(Side.CLIENT)
	public IRenderableWidget getRenderable() {
		return new RenderableItem3D();
	}
	
	class RenderableItem3D extends RenderableGLWidget{
		int alphaColor;
		Tessellator tessellator;
		VertexBuffer vertexbuffer;
		
		@Override
		public void render(EntityPlayer player, Location glassesTerminalLocation, long conditionStates) {
			if(itmStack == null) return;
			IBakedModel ibakedmodel = null;
			
			Minecraft mc = Minecraft.getMinecraft();
			TextureManager tm = mc.getTextureManager();
			
			ibakedmodel = mc.getRenderItem().getItemModelMesher().getItemModel(itmStack);
			
			tm.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			tm.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);       
			
			alphaColor = this.applyModifiers(player, glassesTerminalLocation, conditionStates);		
				
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotated(180.0D, 0.0D, 0.0D, 1.0D);
			this.addPlayerRotation(player);
			GL11.glRotated(180.0D, 0.0D, 0.0D, 1.0D);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
						
						
			tessellator = Tessellator.getInstance();
			vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
			EnumFacing[] var6 = EnumFacing.values();			
						
			for (int var8 = 0, var7 = var6.length; var8 < var7; ++var8) 
				renderQuads(vertexbuffer, ibakedmodel.getQuads(null, var6[var8], 0L), alphaColor, itmStack);
			
			renderQuads(vertexbuffer, ibakedmodel.getQuads(null, null, 0L), alphaColor, itmStack);
			tessellator.draw();
			
			
			
			this.revokeModifiers();			
		}

		private  void renderQuads(VertexBuffer renderer, List<BakedQuad> quads, int color, ItemStack stack) {
			for (int j = quads.size(), i = 0; i < j; ++i)
				LightUtil.renderQuadColor(renderer, quads.get(i), color);
		}
	}
	
	@Override
	public void setItem(ItemStack newItem) {
		if(newItem == null)	return;				
		this.itmStack = newItem;
	}	
	
	@Override
	public void setItem(String newItem, int meta) {
		setItem(new ItemStack(new Item().getByNameOrId(newItem), 1, meta));
	}
	
	@Override
	public void setItem(String newItem) {
		setItem(new ItemStack(new Item().getByNameOrId(newItem)));
	}

	@Override
	public Item getItem() {
		return this.itmStack.getItem();
	}
}

