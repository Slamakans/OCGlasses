package com.bymarcin.openglasses.surface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.bymarcin.openglasses.surface.widgets.component.face.Text;
import com.bymarcin.openglasses.utils.Location;

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

import net.minecraft.client.renderer.GlStateManager;


@SideOnly(Side.CLIENT)
public class ClientSurface {
	public static ClientSurface instances = new ClientSurface();
	public Map<Integer, IRenderableWidget> renderables = new ConcurrentHashMap<Integer, IRenderableWidget>();
	public Map<Integer, IRenderableWidget> renderablesWorld = new ConcurrentHashMap<Integer, IRenderableWidget>();
	boolean isPowered = false;
	public boolean haveGlasses = false;
	public boolean OverlayActive = false;
	public Location lastBind;
	IRenderableWidget noPowerRender;
	
	private ClientSurface() {
		noPowerRender = getNoPowerRender();
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
	
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent evt) {
		if (evt.getType() != ElementType.HELMET) return;
		if (!(evt instanceof RenderGameOverlayEvent.Post)) return;
		if(!shouldRenderStart(evt)) return;
		if(renderables.size() < 1) return;
		
		EntityPlayer player = Minecraft.getMinecraft().player;
		UUID playerUUID = player.getGameProfile().getId();		
		
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glScaled(evt.getResolution().getScaledWidth_double()/512D, evt.getResolution().getScaledHeight_double()/512D*16D/9D, 0);
		
		GL11.glDepthMask(false);		
		for(IRenderableWidget renderable : renderables.values()){
			if(renderable.shouldWidgetBeRendered() && (renderable.getWidgetOwner() == null || playerUUID.equals(renderable.getWidgetOwner()))){
				GL11.glPushMatrix();
				renderable.render(player, player.posX, player.posY, player.posZ, this.OverlayActive);
				GL11.glPopMatrix();
			}			
		}
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
	
	public boolean shouldRenderStart(RenderGameOverlayEvent evt){
		if(!haveGlasses) 
			return false;
		
		if(!isPowered && noPowerRender != null){
			if(evt != null){
				EntityPlayer player = Minecraft.getMinecraft().player;
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glPushMatrix();
				GL11.glScaled(evt.getResolution().getScaledWidth_double()/512D, evt.getResolution().getScaledHeight_double()/512D*16D/9D, 0);
				noPowerRender.render(player, player.posX, player.posY, player.posZ, this.OverlayActive); 
				GL11.glPopMatrix();
				GL11.glPopAttrib();
			}
			return false;
		}
		
		if(lastBind == null) 
			return false;
				
		return true;		
	}		
	
	public double[] getEntityPlayerLocation(EntityPlayer e, float partialTicks){
		double x = (double) e.prevPosX + (e.posX - e.prevPosX) * partialTicks; 
		double y = (double) e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
		double z = (double) e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;
		return new double[]{x, y, z};
	}	 	
	
	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event)	{	
		if(!shouldRenderStart(null)) return;
		if(renderablesWorld.size() < 1) return;
		
		EntityPlayer player= Minecraft.getMinecraft().player;
		UUID playerUUID = player.getGameProfile().getId();		
		
		double[] playerLocation = getEntityPlayerLocation(player, event.getPartialTicks());
						
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		
		GL11.glTranslated(-playerLocation[0], -playerLocation[1], -playerLocation[2]);
		GL11.glTranslated(lastBind.x, lastBind.y, lastBind.z);
		
		GL11.glDepthMask(false);
		//Start Drawing In World		
		for(IRenderableWidget renderable : renderablesWorld.values()){
			if(renderable.shouldWidgetBeRendered() && (renderable.getWidgetOwner() == null || playerUUID.equals(renderable.getWidgetOwner()))){
				GL11.glPushMatrix();
				renderable.render(player, playerLocation[0] - lastBind.x, playerLocation[1] - lastBind.y, playerLocation[2] - lastBind.z, this.OverlayActive);
				GL11.glPopMatrix();	
			}
		}		
		//Stop Drawing In World
		GL11.glPopMatrix();		
		GL11.glPopAttrib();
	}
	
	public static RayTraceResult getBlockCoordsLookingAt(EntityPlayer player){
		RayTraceResult objectMouseOver;
		objectMouseOver = player.rayTrace(200, 1);	
		if(objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			return objectMouseOver;
		}
		return null;
	}
	
	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}
	
	private IRenderableWidget getNoPowerRender(){
		Text t = new Text();
		t.setText("NO POWER");
		t.WidgetModifierList.addColor(1F, 0F, 0F, 0.5F);		
		return t.getRenderable();
	}
	
}
