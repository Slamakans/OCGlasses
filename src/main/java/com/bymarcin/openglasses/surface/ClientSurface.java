package com.bymarcin.openglasses.surface;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.bymarcin.openglasses.OpenGlasses;
import com.bymarcin.openglasses.item.OpenGlassesItem;
import com.bymarcin.openglasses.surface.widgets.component.face.Text;
import com.bymarcin.openglasses.utils.Location;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bymarcin.openglasses.utils.OGUtils;

@SideOnly(Side.CLIENT)
public class ClientSurface {
	public static ClientSurface instances = new ClientSurface();
	public Map<Integer, IRenderableWidget> renderables = new ConcurrentHashMap<Integer, IRenderableWidget>();
	public Map<Integer, IRenderableWidget> renderablesWorld = new ConcurrentHashMap<Integer, IRenderableWidget>();
	public boolean glassesHaveSensorWater = false;
	public boolean glassesHaveSensorRain = false;
	public boolean glassesHaveSensorSneaking = false;
	public boolean glassesHaveSensorLight = false;
	public boolean OverlayActive = false;
	public OpenGlassesItem glasses;
	public ItemStack glassesStack;
	public Location lastBind;


	private IRenderableWidget noPowerRender, noLinkRender;
	
	public long conditionStates = 0;
	private long lastExtendedConditionCheck = 0;

	private ClientSurface() {
		noPowerRender = getNoPowerRender();
		noLinkRender = getNoLinkRender();
	}	
	
	//gets the current widges and puts them to the correct hashmap
	public void updateWidgets(Set<Entry<Integer, Widget>> widgets){
		for(Entry<Integer, Widget> widget : widgets){
			IRenderableWidget r = widget.getValue().getRenderable();
			switch(r.getRenderType()){
			case GameOverlayLocated: 
				renderables.put(widget.getKey(), r);
				break;
			case WorldLocated: 
				renderablesWorld.put(widget.getKey(), r);
				break;
			}
		}
	}
	
	public void removeWidgets(List<Integer> ids){
		for(Integer id : ids){
			renderables.remove(id);
			renderablesWorld.remove(id);
		}
	}
	
	public void removeAllWidgets(){
		renderables.clear();
		renderablesWorld.clear();
	}

	private long getConditionStates(EntityPlayer player){
		long curConditionStates = 0;
		long checkConditions = ~0;
		
		if(OverlayActive)
			curConditionStates |= ((long) 1 << WidgetModifierConditionType.OVERLAY_ACTIVE); 
		else 
			curConditionStates |= ((long) 1 << WidgetModifierConditionType.OVERLAY_INACTIVE); 
		
		if(glassesHaveSensorSneaking && (((checkConditions >>> WidgetModifierConditionType.IS_SNEAKING) & 1) != 0 || ((checkConditions >>> WidgetModifierConditionType.IS_NOT_SNEAKING) & 1) != 0)){
			if(player.isSneaking())
				curConditionStates |= ((long) 1 << WidgetModifierConditionType.IS_SNEAKING); 
			else 
				curConditionStates |= ((long) 1 << WidgetModifierConditionType.IS_NOT_SNEAKING);				
		}
		
		//bs
		if((player.world.getWorldTime() - lastExtendedConditionCheck) < 20){
			this.conditionStates &= ~((long) 1 << WidgetModifierConditionType.OVERLAY_ACTIVE); 
			this.conditionStates &= ~((long) 1 << WidgetModifierConditionType.OVERLAY_INACTIVE); 
			this.conditionStates &= ~((long) 1 << WidgetModifierConditionType.IS_SNEAKING); 
			this.conditionStates &= ~((long) 1 << WidgetModifierConditionType.IS_NOT_SNEAKING); 
			return (curConditionStates | this.conditionStates);
		}

		lastExtendedConditionCheck = player.world.getWorldTime();

		if(glassesHaveSensorRain && (((checkConditions >>> WidgetModifierConditionType.IS_WEATHER_RAIN) & 1) != 0 || ((checkConditions >>> WidgetModifierConditionType.IS_WEATHER_CLEAR) & 1) != 0)){
			if(player.world.isRaining())
				curConditionStates |= ((long) 1 << WidgetModifierConditionType.IS_WEATHER_RAIN); 
			else 
				curConditionStates |= ((long) 1 << WidgetModifierConditionType.IS_WEATHER_CLEAR); 
		}
		
		if(glassesHaveSensorWater && (((checkConditions >>> WidgetModifierConditionType.IS_SWIMMING) & 1) != 0 || ((checkConditions >>> WidgetModifierConditionType.IS_NOT_SWIMMING) & 1) != 0)){
			if(OGUtils.isPlayerSwimming(player))
				curConditionStates |= ((long) 1 << WidgetModifierConditionType.IS_SWIMMING); 
			else 
				curConditionStates |= ((long) 1 << WidgetModifierConditionType.IS_NOT_SWIMMING); 	
		}			
		
		if(glassesHaveSensorLight) {
			int lightLevel = OGUtils.getLightLevelPlayer(player);

			for (int i = WidgetModifierConditionType.IS_LIGHTLEVEL_MIN_0, l = 0; i < WidgetModifierConditionType.IS_LIGHTLEVEL_MIN_15; i++, l++)
				if (((checkConditions >>> i) & 1) != 0 && lightLevel >= l)
					curConditionStates |= ((long) 1 << i);

			for (int i = WidgetModifierConditionType.IS_LIGHTLEVEL_MAX_0, l = 0; i < WidgetModifierConditionType.IS_LIGHTLEVEL_MAX_15; i++, l++)
				if (((checkConditions >>> i) & 1) != 0 && lightLevel <= l)
					curConditionStates |= ((long) 1 << i);
		}
		
		
		return curConditionStates;
	}
	
	
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent evt) {
		if (evt.getType() != ElementType.HELMET) return;
		if (!(evt instanceof RenderGameOverlayEvent.Post)) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		UUID playerUUID = player.getGameProfile().getId();		
		
		if(!shouldRenderStart(evt, player)) return;
		if(renderables.size() < 1) return;		
		
		this.conditionStates = getConditionStates(player);
		
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glScaled(evt.getResolution().getScaledWidth_double()/512D, evt.getResolution().getScaledHeight_double()/512D*16D/9D, 0);
		
		GL11.glDepthMask(false);		
		for(IRenderableWidget renderable : renderables.values()){
			if(renderable.shouldWidgetBeRendered(player) && (renderable.getWidgetOwner() == null || playerUUID.equals(renderable.getWidgetOwner()))){
				GL11.glPushMatrix();
				renderable.render(player, lastBind, this.conditionStates);
				GL11.glPopMatrix();
			}			
		}
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
	
	public boolean shouldRenderStart(RenderGameOverlayEvent evt, EntityPlayer player){
		if(glasses == null)
			return false;

		if(glasses.getEnergyBuffer(glassesStack) == 0 && noPowerRender != null){
			if(evt != null){
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glPushMatrix();
				GL11.glScaled(evt.getResolution().getScaledWidth_double()/512D, evt.getResolution().getScaledHeight_double()/512D*16D/9D, 0);
				noPowerRender.render(player, lastBind, ~0);
				GL11.glPopMatrix();
				GL11.glPopAttrib();
			}
			return false;
		}
		
		if(lastBind == null && noLinkRender != null) {
			if(evt != null){
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glPushMatrix();
				GL11.glScaled(evt.getResolution().getScaledWidth_double()/512D, evt.getResolution().getScaledHeight_double()/512D*16D/9D, 0);
				noPowerRender.render(player, lastBind, ~0);
				GL11.glPopMatrix();
				GL11.glPopAttrib();
			}
			return false;
		}
		
		return true;		
	}		
	
	public double[] getEntityPlayerLocation(EntityPlayer e, float partialTicks){
		double x = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
		double y = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
		double z = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;
		return new double[]{x, y, z};
	}	 	
	
	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event)	{	
		if(renderablesWorld.size() < 1) return;		
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if(!shouldRenderStart(null, player)) return;
		
		UUID playerUUID = player.getGameProfile().getId();		
		
		double[] playerLocation = getEntityPlayerLocation(player, event.getPartialTicks());
		
		this.conditionStates = getConditionStates(player);
		
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		
		GL11.glTranslated(-playerLocation[0], -playerLocation[1], -playerLocation[2]);
		GL11.glTranslated(lastBind.x, lastBind.y, lastBind.z);
		
		GL11.glDepthMask(false);
		//Start Drawing In World		
		for(IRenderableWidget renderable : renderablesWorld.values()){
			if(renderable.shouldWidgetBeRendered(player) && (renderable.getWidgetOwner() == null || playerUUID.equals(renderable.getWidgetOwner()))){
				GL11.glPushMatrix();
				renderable.render(player, lastBind, this.conditionStates);
				GL11.glPopMatrix();	
		} }		
		//Stop Drawing In World
		GL11.glPopMatrix();		
		GL11.glPopAttrib();
	}
	
	public static RayTraceResult getBlockCoordsLookingAt(EntityPlayer player){
		RayTraceResult objectMouseOver = player.rayTrace(200, 1);	
		if(objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK){
			return objectMouseOver;
		}
		return null;
	}

	private IRenderableWidget getNoPowerRender(){
		Text t = new Text();
		t.setText("NO POWER");
		t.WidgetModifierList.addColor(1F, 0F, 0F, 0.5F);		
		return t.getRenderable();
	}

	private IRenderableWidget getNoLinkRender(){
		Text t = new Text();
		t.setText("NOT LINKED");
		t.WidgetModifierList.addColor(1F, 1F, 1F, 0.5F);
		return t.getRenderable();
	}
	
}
