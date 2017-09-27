package com.bymarcin.openglasses.item;

import java.util.List;

import com.bymarcin.openglasses.surface.ClientSurface;
import com.bymarcin.openglasses.utils.OGUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bymarcin.openglasses.OpenGlasses;
import com.bymarcin.openglasses.utils.Location;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraft.util.EnumFacing;

import net.minecraft.world.World;

public class OpenGlassesItem extends ItemArmor {
	public OpenGlassesItem() {
		super(ArmorMaterial.IRON, 0, EntityEquipmentSlot.HEAD);
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenGlasses.creativeTab);
		setUnlocalizedName("openglasses");
		setRegistryName("openglasses");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		ItemStack is = new ItemStack(item);

		par3List.add(is.copy());

		NBTTagCompound tag = is.getTagCompound();
		tag.setInteger("Energy", 5000000);
		tag.setInteger("EnergyCapacity", 5000000);
		tag.setInteger("widgetLimit", 255);
		tag.setInteger("upkeepCost", 0);
		tag.setBoolean("daylightDetector", true);
		tag.setBoolean("tankUpgrade", true);
		tag.setBoolean("motionsensor", true);
		tag.setBoolean("geolyzer", true);
		par3List.add(is);
	}

	@Override
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt){
		if(nbt != null)
			stack.setTagCompound(nbt);
		else
			stack.setTagCompound(new NBTTagCompound());

		NBTTagCompound tag = stack.getTagCompound();
		tag.setInteger("widgetLimit", 9); //default to max 9 Widgets
		tag.setInteger("upkeepCost", 1);  //default to upkeep cost of 1FE / tick
		tag.setInteger("EnergyCapacity", 50000); //set the default EnergyBuffer to 50k FE
		return new EnergyCapabilityProvider(stack, this);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return OpenGlasses.MODID + ":textures/models/glasses.png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltip, boolean par4) {
		super.addInformation(itemStack, player, tooltip, par4);

		NBTTagCompound tag = itemStack.getTagCompound();
		Location uuid = OGUtils.getGlassesTerminalUUID(itemStack);
		if(tag.getLong("uniqueKey") > 0){
			tooltip.add("linked to: X: " + tag.getInteger("X") + ", Y: " + tag.getInteger("Y") + ", Z: " + tag.getInteger("Z") + " (DIM: " + tag.getInteger("DIM") +")");
			tooltip.add("terminal: " + tag.getLong("uniqueKey"));
			tooltip.add("user: " + tag.getString("user"));
		}
		else
			tooltip.add("use at glassesterminal to link glasses");


		if(tag.getBoolean("daylightDetector"))
			tooltip.add("lightsensor: installed");
		else {
			tooltip.add("lightsensor: not installed");
			tooltip.add("(install on anvil with minecraft daylight sensor)");
		}
		if(tag.getBoolean("tankUpgrade"))
			tooltip.add("rainsensor: installed");
		else {
			tooltip.add("rainsensor: not installed");
			tooltip.add("(install on anvil with opencomputers tank upgrade)");
		}
		if(tag.getBoolean("motionsensor"))
			tooltip.add("sneak detection: installed");
		else {
			tooltip.add("sneak detection: not installed");
			tooltip.add("(install on anvil with opencomputers motionsensor)");
		}
		if(tag.getBoolean("geolyzer"))
			tooltip.add("geolyzer: installed");
		else {
			tooltip.add("geolyzer: not installed");
			tooltip.add("(install on anvil with opencomputers geolyzer to enable swimming detection)");
		}

		int widgetCount = ClientSurface.instances.getWidgetCount();
		tooltip.add("using " + widgetCount + "/" + tag.getInteger("widgetLimit") + " widgets");

		IEnergyStorage storage = itemStack.getCapability(CapabilityEnergy.ENERGY, null);
		tooltip.add(String.format("%s/%s FE", storage.getEnergyStored(), storage.getMaxEnergyStored()));
		tooltip.add("usage " + tag.getInteger("upkeepCost") + " FE/tick");
	}

	public void bindToTerminal(ItemStack glass, Location uuid) {
		NBTTagCompound tag = glass.getTagCompound();
		tag.setInteger("X", uuid.x);
		tag.setInteger("Y", uuid.y);
		tag.setInteger("Z", uuid.z);
		tag.setInteger("DIM", uuid.dimID);
		tag.setLong("uniqueKey", uuid.uniqueKey);
		tag.setString("user",  Minecraft.getMinecraft().player.getGameProfile().getId().toString());
	}

	// Forge Energy

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {
		if(world.isRemote) return;
		if (!(entity instanceof EntityPlayer)) return;

		ItemStack glasses = OpenGlasses.getGlassesStack((EntityPlayer) entity);
		if(glasses == null) return;

		if(glasses.equals(stack))
			this.consumeEnergy(stack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack){
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if(storage.getEnergyStored() >= storage.getMaxEnergyStored())
			return false;
		else
			return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack){
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		return 1 - (((double) 1 / storage.getMaxEnergyStored()) * storage.getEnergyStored());
	}

	public int consumeEnergy(ItemStack stack){
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		return storage.extractEnergy(stack.getTagCompound().getInteger("upkeepCost"), false);
	}

	public double getEnergyStored(ItemStack stack){
		return stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
	}

	private static class EnergyCapabilityProvider implements ICapabilityProvider{
		public final EnergyStorage storage;

		public EnergyCapabilityProvider(final ItemStack stack, OpenGlassesItem item){
			this.storage = new EnergyStorage(0, 1000, 1000){
				@Override
				public int getEnergyStored(){
					return stack.getTagCompound().getInteger("Energy");
				}

				@Override
				public int getMaxEnergyStored(){
					return stack.getTagCompound().getInteger("EnergyCapacity");
				}

				public void setEnergyStored(int energy){
					stack.getTagCompound().setInteger("Energy", energy);
				}

				@Override
				public int receiveEnergy(int receive, boolean simulate){
					int energy = this.getEnergyStored();

					int energyReceived = Math.min(this.getMaxEnergyStored()-energy, Math.min(this.maxReceive, receive));

					if(!simulate) this.setEnergyStored(energy+energyReceived);

					return energyReceived;
				}

				@Override
				public int extractEnergy(int extract, boolean simulate){
					if(!this.canExtract()) return 0;

					int energy = this.getEnergyStored();

					int energyExtracted = Math.min(energy, Math.min(this.maxExtract, extract));
					if(!simulate) this.setEnergyStored(energy-energyExtracted);

					return energyExtracted;
				}
			};
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing){
			return this.getCapability(capability, facing) != null;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing){
			if(capability == CapabilityEnergy.ENERGY){
				return (T) this.storage;
			}
			return null;
		}
	}
}