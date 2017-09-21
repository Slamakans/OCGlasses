package com.bymarcin.openglasses.proxy;

import com.bymarcin.openglasses.event.ClientEventHandler;
import com.bymarcin.openglasses.surface.ClientSurface;
import com.bymarcin.openglasses.surface.VBO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;


public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerShader() {
		VBO.instances.compileShader();
	}
	
	@Override
	public void registermodel(Item item, int meta){
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	@Override
	public void init() {
		ClientEventHandler eh = new ClientEventHandler();
	 	MinecraftForge.EVENT_BUS.register(eh);
		MinecraftForge.EVENT_BUS.register(ClientSurface.instances);
		MinecraftForge.EVENT_BUS.register(VBO.instances);
	}

	@Override
	public World getWorld(int dimensionId) {
		if (getCurrentClientDimension() != dimensionId) {
			return null;
		} else
			return Minecraft.getMinecraft().world;
	}

	@Override
	public int getCurrentClientDimension() {
		return Minecraft.getMinecraft().world.provider.getDimension();
	}
}
