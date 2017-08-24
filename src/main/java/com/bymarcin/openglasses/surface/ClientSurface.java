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


@SideOnly(Side.CLIENT)
public class ClientSurface {
	public static ClientSurface instances = new ClientSurface();
	public Map<Integer, IRenderableWidget> renderables = new ConcurrentHashMap<Integer, IRenderableWidget>();
	public Map<Integer, IRenderableWidget> renderablesWorld = new ConcurrentHashMap<Integer, IRenderableWidget>();
	boolean isPowered = false;
	public boolean haveGlasses = false;
	public boolean HUDactive = false;
	public Location lastBind;
	IRenderableWidget noPowerRender;
	
	private ClientSurface() {
		noPowerRender = getNoPowerRender();
	}	
	
	public void updateWigets(Set<Entry<Integer, Widget>> widgets){
		for(Entry<Integer, Widget> widget : widgets){
			IRenderableWidget r = widget.getValue().getRenderable();
			switch(r.getRenderType()){
			case GameOverlayLocated: renderables.put(widget.getKey(), r);
				break;
			case WorldLocated: renderablesWorld.put(widget.getKey(), r);
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
		if(!shouldRenderStart()) return;
		
		EntityPlayer player= Minecraft.getMinecraft().thePlayer;
		UUID playerUUID = player.getGameProfile().getId();		
		
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glScaled(evt.getResolution().getScaledWidth_double()/512D, evt.getResolution().getScaledHeight_double()/512D*16D/9D, 0);

		for(IRenderableWidget renderable : renderables.values()){
			if(renderable.shouldWidgetBeRendered() && (renderable.getWidgetOwner() == null || playerUUID.equals(renderable.getWidgetOwner()))){
				renderable.render(null, 0, 0, 0, renderable.getAlpha(HUDactive));
			}
		}
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
	
	public boolean shouldRenderStart(){
		if(!haveGlasses) 
			return false;
		
		if(!isPowered && noPowerRender != null){
			noPowerRender.render(null, 0, 0, 0, noPowerRender.getAlpha(HUDactive)); 
			return false;
		}
		
		if(lastBind == null) 
			return false;
				
		return true;		
	}
		
	
	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event)	{	
		if(!shouldRenderStart()) return;
		
		EntityPlayer player= Minecraft.getMinecraft().thePlayer;
		UUID playerUUID = player.getGameProfile().getId();		
				
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks(); 
		double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
		double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();
		GL11.glTranslated(-playerX, -playerY, -playerZ);
		GL11.glTranslated(lastBind.x, lastBind.y, lastBind.z);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		//Start Drawing In World		
		for(IRenderableWidget renderable : renderablesWorld.values()){
			if(renderable.shouldWidgetBeRendered() && (renderable.getWidgetOwner() == null || playerUUID.equals(renderable.getWidgetOwner()))){
				renderable.render(player, playerX - lastBind.x, playerY - lastBind.y, playerZ - lastBind.z, renderable.getAlpha(HUDactive));
			}
		}		
		//Stop Drawing In World
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
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
		t.setAlpha(0.5);
		t.setScale(1);
		t.setColor(1, 0, 0);
		return t.getRenderable();
	}
	
}
