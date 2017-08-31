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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		NBTTagCompound ogc = getItemTag(itemStack);
		super.addInformation(itemStack, player, list, par4);
		Location uuid = getUUID(itemStack);
		if(ogc.getLong("uniqueKey") > 0){
			list.add("linked to: X: " + ogc.getInteger("X") + ", Y: " + ogc.getInteger("Y") + ", Z: "+ ogc.getInteger("Z") + " (DIM: " + ogc.getInteger("DIM") +")");			
			list.add("terminal: " + ogc.getLong("uniqueKey"));			
			list.add("user: " + ogc.getString("user"));
		}
		else
			list.add("use at glassesterminal to link glasses");
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
