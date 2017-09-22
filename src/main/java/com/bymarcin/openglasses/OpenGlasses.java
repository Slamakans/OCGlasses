package com.bymarcin.openglasses;

import com.bymarcin.openglasses.block.OpenGlassesTerminalBlock;
import com.bymarcin.openglasses.item.OpenGlassesItem;
import com.bymarcin.openglasses.item.OpenGlassesBaubleItem;
import com.bymarcin.openglasses.network.NetworkRegistry;
import com.bymarcin.openglasses.network.packet.GlassesEventPacket;
import com.bymarcin.openglasses.network.packet.TerminalStatusPacket;
import com.bymarcin.openglasses.network.packet.WidgetUpdatePacket;
import com.bymarcin.openglasses.proxy.CommonProxy;
import com.bymarcin.openglasses.tileentity.OpenGlassesTerminalTileEntity;
import li.cil.oc.api.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;

import net.minecraft.entity.player.EntityPlayer;

@Mod(modid = OpenGlasses.MODID, version = OpenGlasses.VERSION, dependencies = "required-after:OpenComputers@[1.4.0,);after:Baubles;")
public class OpenGlasses
{
	public static final String MODID = "openglasses";
	public static final String VERSION = "@VERSION@";

	public Configuration config;
	public static Logger logger = LogManager.getLogger(OpenGlasses.MODID);

	@SidedProxy(clientSide = "com.bymarcin.openglasses.proxy.ClientProxy", serverSide = "com.bymarcin.openglasses.proxy.CommonProxy")
	public static CommonProxy proxy;

	@Instance(value = OpenGlasses.MODID)
	public static OpenGlasses instance;

	public static CreativeTabs creativeTab = CreativeTabs.REDSTONE;

	public static Item openGlasses;
	public static OpenGlassesTerminalBlock openTerminal;

	public static int energyBuffer = 100;
	public static double energyMultiplier  = 1;

	public static boolean baubles = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if(Loader.isModLoaded("Baubles")) this.baubles = true;

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		NetworkRegistry.initialize();
		energyBuffer = config.get("Energy", "energyBuffer", 100).getInt(100);
		energyMultiplier = config.get("Energy", "energyMultiplier", 1.0, "PowerDrain= (NumberOfWidgets / 10) * energyMultiplier").getDouble(1.0);


		openTerminal = GameRegistry.register(new OpenGlassesTerminalBlock());
		Item i = GameRegistry.register(new ItemBlock(openTerminal).setRegistryName(openTerminal.getRegistryName()));
		proxy.registermodel(i, 0);


		GameRegistry.registerTileEntity(OpenGlassesTerminalTileEntity.class, "openglassesterminalte");

		openGlasses = getOGCObject(this.baubles);

		proxy.registermodel(openGlasses, 0);
		GameRegistry.register(openGlasses);

		proxy.init();
	}

	public Item getOGCObject(boolean bauble){
		if(bauble == true)
			return new OpenGlassesBaubleItem();
		else
			return new OpenGlassesItem();
	}

	public static Item getGlasses(EntityPlayer e){
		ItemStack glassesStack = getGlassesStack(e);

		if(!isGlassesStack(glassesStack))
			return null;

		Item glasses = glassesStack!=null?glassesStack.getItem():null;

		return glasses;
	}

	public static boolean isGlassesStack(ItemStack stack){
		Item glasses = stack!=null?stack.getItem():null;

		if(glasses instanceof OpenGlassesItem)
			return true;
		else
			return false;
	}

	public static ItemStack getGlassesStack(EntityPlayer e){
		//get armor slot
		ItemStack glassesStack = e.inventory.armorInventory[3];

		if(isGlassesStack(glassesStack))
			return glassesStack;

		return getGlassesStackBaubles(e);
	}

	public static ItemStack getGlassesStackBaubles(EntityPlayer e){
		//get baubles slot if glasses arent found in armor slot
		if(!Loader.isModLoaded("Baubles")) return null;

		IBaublesItemHandler handler = BaublesApi.getBaublesHandler(e);

		if (handler == null) return null;

		ItemStack glassesStack = handler.getStackInSlot(4);
			if(isGlassesStack(glassesStack))
				return glassesStack;

		return null;
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.registerPacket(0, GlassesEventPacket.class, Side.SERVER);
		NetworkRegistry.registerPacket(1, WidgetUpdatePacket.class, Side.CLIENT);
		NetworkRegistry.registerPacket(2, TerminalStatusPacket.class, Side.CLIENT);
	}


	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		ItemStack ram= Items.get("ram5").createItemStack(1);
		ItemStack graphics = Items.get("graphicsCard3").createItemStack(1);
		ItemStack wlanCard = Items.get("wlanCard").createItemStack(1);
		ItemStack server = Items.get("geolyzer").createItemStack(1);
		ItemStack screen = Items.get("screen3").createItemStack(1);
		ItemStack cpu = Items.get("cpu3").createItemStack(1);

		GameRegistry.addRecipe(new ItemStack(openGlasses),"SCS"," W ","   ", 'S', screen, 'W', wlanCard, 'C', graphics);
		GameRegistry.addRecipe(new ItemStack(openTerminal),"R  ","S  ","M  ", 'S', server, 'R', ram, 'M', cpu);

		config.save();

		proxy.postInit();
	}
}
