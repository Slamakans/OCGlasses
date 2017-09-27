package com.bymarcin.openglasses.event;

import com.bymarcin.openglasses.OpenGlasses;
import com.bymarcin.openglasses.gui.InteractGui;
import com.bymarcin.openglasses.item.OpenGlassesItem;
import com.bymarcin.openglasses.network.NetworkRegistry;
import com.bymarcin.openglasses.network.packet.GlassesEventPacket;
import com.bymarcin.openglasses.network.packet.GlassesEventPacket.EventType;
import com.bymarcin.openglasses.surface.ClientSurface;
import com.bymarcin.openglasses.utils.Location;

import com.bymarcin.openglasses.utils.OGUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		if(ClientSurface.instances.glasses == null) return;
		if(!event.getSide().isClient()) return;
		if(event.getHand() != EnumHand.MAIN_HAND) return;

		NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(EventType.INTERACT_WORLD_RIGHT, ClientSurface.instances.lastBind, event.getEntityPlayer()));
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(ClientSurface.instances.glasses == null) return;
		if(!interactGUIKey.isPressed()) return;

		ClientSurface.instances.OverlayActive = true;
		Minecraft.getMinecraft().displayGuiScreen(new InteractGui());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onSizeChange(RenderGameOverlayEvent event) {
		if(ClientSurface.instances.glasses == null) return;

		ScaledResolution resolution = event.getResolution();
		boolean resolutionChanged = resolution.getScaledWidth() != ClientSurface.instances.resolution.getScaledWidth()
				|| resolution.getScaledHeight() != ClientSurface.instances.resolution.getScaledHeight()
				|| resolution.getScaleFactor() != ClientSurface.instances.resolution.getScaleFactor();

		if(resolutionChanged) {
			ClientSurface.instances.resolution = resolution;
			sendResolution();
		}
	}

	public void sendResolution(){
		if(ClientSurface.instances.glasses == null) return;
		if(ClientSurface.instances.lastBind == null) return;

		NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(GlassesEventPacket.EventType.GLASSES_SCREEN_SIZE, ClientSurface.instances.lastBind, Minecraft.getMinecraft().player, ClientSurface.instances.resolution.getScaledWidth(), ClientSurface.instances.resolution.getScaledHeight(), ClientSurface.instances.resolution.getScaleFactor()));
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

		Location uuid  = OGUtils.getGlassesTerminalUUID(glassesStack);

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
		if(evt.getLeft() == null || evt.getRight() == null) return;

		if(!OpenGlasses.isGlassesStack(evt.getLeft())) return;

		if(evt.getRight().stackSize > 1) return; //no support for stacked stuff

		ItemStack anvilOutputGlassesStack = evt.getLeft().copy();

		NBTTagCompound tag = anvilOutputGlassesStack.getTagCompound();

		Item itm = evt.getRight().getItem();

		evt.setCost(0);

		if(itm == Item.getItemFromBlock(Blocks.DAYLIGHT_DETECTOR)) {
			tag.setBoolean("daylightDetector", true);
			tag.setInteger("upkeepCost", tag.getInteger("upkeepCost")+1); //increase power usage by 1
			evt.setCost(20);
		}
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "upgrade"))
				&& evt.getRight().getMetadata() == 23) { //oc tankUpgrade
			tag.setBoolean("tankUpgrade", true);
			tag.setInteger("upkeepCost", tag.getInteger("upkeepCost")+1); //increase power usage by 1
			evt.setCost(20);
		}
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "motionSensor"))) {
			tag.setBoolean("motionsensor", true);
			tag.setInteger("upkeepCost", tag.getInteger("upkeepCost")+1); //increase power usage by 1
			evt.setCost(20);
		}
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "geolyzer"))) {
			tag.setBoolean("geolyzer", true);
			tag.setInteger("upkeepCost", tag.getInteger("upkeepCost")+1); //increase power usage by 1
			evt.setCost(20);
		}

		/* battery and database upgrades */
		else if(itm.getRegistryName().equals(new ResourceLocation("opencomputers", "upgrade"))) {
			switch (evt.getRight().getMetadata()) {
				case 1:	case 2:	case 3: //battery upgrades
					IEnergyStorage storage = anvilOutputGlassesStack.getCapability(CapabilityEnergy.ENERGY, null);
					int newEnergyBufferSize = storage.getMaxEnergyStored();
					int energyBufferTotalLimit = 5000000; //limit upgrades to max 5M FE
					if (newEnergyBufferSize > energyBufferTotalLimit) break; //cancel upgrade when the buffer is allready at it's limit
					switch (evt.getRight().getMetadata()) {
						case 1: //battery upgrade T1
							newEnergyBufferSize += 100000;
							evt.setCost(10);
							break;
						case 2: //battery upgrade T2
							newEnergyBufferSize += 250000;
							evt.setCost(20);
							break;
						case 3: //battery upgrade T3
							newEnergyBufferSize += 1000000;
							evt.setCost(34);
							break;
					}
					if (newEnergyBufferSize > energyBufferTotalLimit) newEnergyBufferSize = energyBufferTotalLimit;
					tag.setInteger("EnergyCapacity", newEnergyBufferSize);
					break;

				case 12: case 13: case 14:  //database upgrades
					int newWidgetLimit = tag.getInteger("widgetLimit");
					int widgetsTotalLimit = 255;
					if (newWidgetLimit >= widgetsTotalLimit) break;
					switch (evt.getRight().getMetadata()) {
						case 12: //database upgrade T1
							newWidgetLimit += 9;
							evt.setCost(9);
							break;
						case 13: //database upgrade T2
							newWidgetLimit += 25;
							evt.setCost(20);
							break;
						case 14: //database upgrade T3
							newWidgetLimit += 81;
							evt.setCost(34);
							break;
					}
					if (newWidgetLimit > widgetsTotalLimit) newWidgetLimit = widgetsTotalLimit;
					tag.setInteger("widgetLimit", newWidgetLimit);
					break;
			}
		}

		if(evt.getCost() > 0)
			evt.setOutput(anvilOutputGlassesStack);

		return;
	}
}
