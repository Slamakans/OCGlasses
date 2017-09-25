package com.bymarcin.openglasses.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

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
import org.apache.logging.log4j.LogManager;

public class OpenGlassesItem extends ItemArmor {
	private final int energyBuffer;
	private final int energyTransfer;
	private final int energyMultiplier;

	public OpenGlassesItem() {
		super(ArmorMaterial.IRON, 0, EntityEquipmentSlot.HEAD);

		this.energyBuffer = 1000000; //buffer
		this.energyMultiplier = 1; //cost for each widget
		this.energyTransfer = 10000; //charge rate

		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenGlasses.creativeTab);
		setUnlocalizedName("openglasses");
		setRegistryName("openglasses");
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return OpenGlasses.MODID + ":textures/models/glasses.png";
	}

	public static Location getUUID(ItemStack itemStack){
		NBTTagCompound tag = getItemTag(itemStack);
		if (!tag.hasKey("X") || !tag.hasKey("Y") || ! tag.hasKey("Z") || ! tag.hasKey("uniqueKey")) return null;
		return new Location(new BlockPos(tag.getInteger("X"),tag.getInteger("Y"),tag.getInteger("Z")),tag.getInteger("DIM"), tag.getLong("uniqueKey"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltip, boolean par4) {
		super.addInformation(itemStack, player, tooltip, par4);

		NBTTagCompound tag = getItemTag(itemStack);
		Location uuid = getUUID(itemStack);
		if(tag.getLong("uniqueKey") > 0){
			tooltip.add("linked to: X: " + tag.getInteger("X") + ", Y: " + tag.getInteger("Y") + ", Z: " + tag.getInteger("Z") + " (DIM: " + tag.getInteger("DIM") +")");
			tooltip.add("terminal: " + tag.getLong("uniqueKey"));
			tooltip.add("user: " + tag.getString("user"));
		}
		else
			tooltip.add("use at glassesterminal to link glasses");


		if(tag.getBoolean("daylightDetector"))
			tooltip.add("lightsensor: installed");
		else
			tooltip.add("lightsensor: not installed (install on anvil with minecraft daylight sensor)");

		if(tag.getBoolean("tankUpgrade"))
			tooltip.add("rainsensor: installed");
		else
			tooltip.add("rainsensor: not installed (install on anvil with opencomputers tank upgrade)");

		if(tag.getBoolean("motionsensor"))
			tooltip.add("sneak detection: installed");
		else
			tooltip.add("sneak detection: not installed (install on anvil with opencomputers motionsensor)");

		if(tag.getBoolean("geolyzer"))
			tooltip.add("geolyzer: installed");
		else
			tooltip.add("geolyzer: not installed (install on anvil with opencomputers geolyzer to enable swimming detection)");

		IEnergyStorage storage = itemStack.getCapability(CapabilityEnergy.ENERGY, null);
		tooltip.add(String.format("%s/%s FE", storage.getEnergyStored(), storage.getMaxEnergyStored()));
	}

	public static NBTTagCompound getItemTag(ItemStack stack) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		return stack.getTagCompound();
	}

	public void bindToTerminal(ItemStack glass, Location uuid) {
		NBTTagCompound tag = getItemTag(glass);
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
		if (!(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) entity;
		if(!player.world.isRemote && OpenGlasses.getGlassesStack((EntityPlayer) entity) == stack) {
			this.consumeEnergy(stack);
			player.inventoryContainer.detectAndSendChanges();
		}
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


	public int getEnergyBuffer(ItemStack stack){
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		return storage.getEnergyStored();
	}

	public int setEnergyBuffer(ItemStack stack, int bufferSize){
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		int charge = bufferSize - storage.getEnergyStored();

		return storage.receiveEnergy(charge, false);
	}

	public int consumeEnergy(ItemStack stack){
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		return storage.extractEnergy(this.energyMultiplier, false);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt){
		return new EnergyCapabilityProvider(stack, this);
	}

	private static class EnergyCapabilityProvider implements ICapabilityProvider{
		public final EnergyStorage storage;

		public EnergyCapabilityProvider(final ItemStack stack, OpenGlassesItem item){
			this.storage = new EnergyStorage(item.energyBuffer, item.energyTransfer, item.energyTransfer){
				@Override
				public int getEnergyStored(){
					return stack.getTagCompound().getInteger("Energy");
				}

				public void setEnergyStored(int energy){
					stack.getTagCompound().setInteger("Energy", energy);
				}

				@Override
				public int receiveEnergy(int receive, boolean simulate){
					int energy = this.getEnergyStored();

					int energyReceived = Math.min(this.capacity-energy, Math.min(this.maxReceive, receive));

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