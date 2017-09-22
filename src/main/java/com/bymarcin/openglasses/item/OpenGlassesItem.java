package com.bymarcin.openglasses.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bymarcin.openglasses.OpenGlasses;
import com.bymarcin.openglasses.utils.Location;

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
}