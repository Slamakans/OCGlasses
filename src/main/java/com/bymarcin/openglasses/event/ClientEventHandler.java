package com.bymarcin.openglasses.event;

import com.bymarcin.openglasses.OpenGlasses;
import com.bymarcin.openglasses.gui.InteractGui;
import com.bymarcin.openglasses.item.OpenGlassesItem;
import com.bymarcin.openglasses.network.NetworkRegistry;
import com.bymarcin.openglasses.network.packet.GlassesEventPacket;
import com.bymarcin.openglasses.network.packet.GlassesEventPacket.EventType;
import com.bymarcin.openglasses.surface.ClientSurface;
import com.bymarcin.openglasses.utils.Location;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import org.lwjgl.input.Keyboard;

public class ClientEventHandler {
	public static KeyBinding interactGUIKey = new KeyBinding("key.interact", Keyboard.KEY_C, "key.categories." + OpenGlasses.MODID.toLowerCase());
	int tick = 0;

	public ClientEventHandler() {
		ClientRegistry.registerKeyBinding(interactGUIKey);
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e){
		if(e.player != Minecraft.getMinecraft().player) return;
		tick++;

		if(tick%20 != 0) return;

		tick = 0;		
		checkGlasses(e);		
	}
	
	public boolean checkGlasses(PlayerTickEvent e) {
		ItemStack glassesStack = OpenGlasses.getGlassesStack(e.player);

		if(glassesStack != null){
			if (glassesStack == ClientSurface.instances.glassesStack)
				return true;
			else if (ClientSurface.instances.glassesStack == null) {
				equiped(e, glassesStack);
				return true;
			}
		}
		else if(ClientSurface.instances.glassesStack != null) {
			unEquiped(e);
		}

		return false;
	}
	
	@SubscribeEvent
	public void onJoin(EntityJoinWorldEvent e){
		if ((e.getEntity() == Minecraft.getMinecraft().player) && (e.getWorld().isRemote)){
			ClientSurface.instances.removeAllWidgets();
			ClientSurface.instances.glasses = null;
			ClientSurface.instances.glassesStack = null;
		}
	}

	@SubscribeEvent
	public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty e){
		onInteractEvent(EventType.INTERACT_WORLD_LEFT, e);
	}

	@SubscribeEvent
	public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e){
		onInteractEvent(EventType.INTERACT_WORLD_LEFT, e);
	}

	@SubscribeEvent
	public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty e){
		onInteractEvent(EventType.INTERACT_WORLD_RIGHT, e);
	}

	@SubscribeEvent
	public void onRightClickItem(PlayerInteractEvent.RightClickItem e){
		onInteractEvent(EventType.INTERACT_WORLD_RIGHT, e);
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock e){
		onInteractEvent(EventType.INTERACT_WORLD_RIGHT, e);
	}

	private void onInteractEvent(EventType type, PlayerInteractEvent event){
		if(event.getSide().isClient() && event.getHand() == EnumHand.MAIN_HAND && ClientSurface.instances.glasses != null) {
			NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(EventType.INTERACT_WORLD_RIGHT, ClientSurface.instances.lastBind, event.getEntityPlayer()));
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(ClientSurface.instances.glasses != null && interactGUIKey.isPressed()){
			ClientSurface.instances.OverlayActive = true;
			Minecraft.getMinecraft().displayGuiScreen(new InteractGui());
		}
	}

	private void unEquiped(PlayerTickEvent e){
		ClientSurface.instances.glasses = null;
		ClientSurface.instances.glassesStack = null;
		ClientSurface.instances.glassesHaveSensorWater = false;
		ClientSurface.instances.glassesHaveSensorRain = false;
		ClientSurface.instances.glassesHaveSensorSneaking = false;
		ClientSurface.instances.glassesHaveSensorLight = false;
		ClientSurface.instances.removeAllWidgets();
		NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(EventType.UNEQUIPED_GLASSES, ClientSurface.instances.lastBind, e.player));
		ClientSurface.instances.lastBind = null;
	}
	
	private void equiped(PlayerTickEvent e, ItemStack glassesStack){
		ClientSurface.instances.glassesStack = glassesStack;
		ClientSurface.instances.glasses = (OpenGlassesItem) glassesStack.getItem();

		Location uuid  = ClientSurface.instances.glasses.getUUID(glassesStack);

		ClientSurface.instances.lastBind = uuid;

		NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(EventType.EQUIPED_GLASSES, uuid, e.player));

		NBTTagCompound glassesTags = glassesStack.getTagCompound();

		ClientSurface.instances.glassesHaveSensorWater = glassesTags.getBoolean("geolyzer");
		ClientSurface.instances.glassesHaveSensorRain = glassesTags.getBoolean("tankUpgrade");
		ClientSurface.instances.glassesHaveSensorSneaking = glassesTags.getBoolean("motionsensor");
		ClientSurface.instances.glassesHaveSensorLight = glassesTags.getBoolean("daylightDetector");
	}

	@SubscribeEvent
	public void handleAnvilEvent(AnvilUpdateEvent evt) {
		boolean validUpgrade = false;

		if(evt.getLeft() == null || evt.getRight() == null) return;

		if(!(evt.getLeft().getItem() instanceof OpenGlassesItem)) return;

		ItemStack res = new ItemStack(evt.getLeft().getItem());

		NBTTagCompound tag = evt.getLeft().getTagCompound().copy();

		Item itm = evt.getRight().getItem();

		if(itm == Item.getItemFromBlock(Blocks.DAYLIGHT_DETECTOR)) {
			tag.setBoolean("daylightDetector", true);
			evt.setCost(20);
			validUpgrade = true;
		}
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "upgrade")) && evt.getRight().getMetadata() == 23) { //oc tankUpgrade
			tag.setBoolean("tankUpgrade", true);
			evt.setCost(20);
			validUpgrade = true;
		}
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "motionSensor"))) {
			tag.setBoolean("motionsensor", true);
			evt.setCost(20);
			validUpgrade = true;
		}
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "geolyzer"))) {
			tag.setBoolean("geolyzer", true);
			evt.setCost(20);
			validUpgrade = true;
		}
		/*
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "upgrade")) && evt.getRight().getMetadata() == 1) { //battery upgrade T1
		}
		*/

		if(validUpgrade) {
			res.setTagCompound(tag);
			evt.setOutput(res);
		}

		return;
	}
}
