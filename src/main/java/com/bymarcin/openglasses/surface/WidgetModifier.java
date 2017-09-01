package com.bymarcin.openglasses.surface;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import com.bymarcin.openglasses.surface.WidgetModifierType;
import java.util.ArrayList; 

import com.bymarcin.openglasses.utils.OGUtils;

public abstract class WidgetModifier{
	// rotate, translate, color, scale, texture, 
	public int conditions = 0;
	public short condition_lightlevel_min = 0;
	public short condition_lightlevel_max = 0;
	
	int pollIntervalCounter = 0;
	boolean cachedApplyCheck = false;
	
	public boolean shouldApplyModifier(EntityPlayer player, boolean overlayActive){
		if(((conditions >>> WidgetModifierConditionType.OVERLAY_ACTIVE) & 1) != 0) {
			if(overlayActive != true) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.OVERLAY_INACTIVE) & 1) != 0) {
			if(overlayActive != false) return false;
		}
		
		// do timeintense calls after this, so they didnt get called to often
		pollIntervalCounter++;
		if(pollIntervalCounter % 30 != 0) 
			return cachedApplyCheck;
			
		pollIntervalCounter = 0;		
		this.cachedApplyCheck = false;
		
		if(((conditions >>> WidgetModifierConditionType.IS_LIGHTLEVEL_MIN) & 1) != 0) {		
			if(OGUtils.getLightLevelPlayer(player) < condition_lightlevel_min) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.IS_LIGHTLEVEL_MAX) & 1) != 0) {		
			if(OGUtils.getLightLevelPlayer(player) > condition_lightlevel_max) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.IS_WEATHER_RAIN) & 1) != 0) {
			if(player.world.isRaining() == false) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.IS_WEATHER_CLEAR) & 1) != 0) {
			if(player.world.isRaining() == true) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.IS_SWIMMING) & 1) != 0) {
			if(OGUtils.isPlayerSwimming(player) == false) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.IS_NOT_SWIMMING) & 1) != 0) {
			if(OGUtils.isPlayerSwimming(player) == true) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.IS_SNEAKING) & 1) != 0) {
			if(player.isSneaking() == false) return false;
		}
		if(((conditions >>> WidgetModifierConditionType.IS_NOT_SNEAKING) & 1) != 0) {
			if(player.isSneaking() == true) return false;
		}
			
		this.cachedApplyCheck = true;
					
		return cachedApplyCheck;
	}
	
	public Object[] getConditions(){
		Object[] foo = new Object[10];
		int i = 0;
		if(((conditions >>> WidgetModifierConditionType.IS_LIGHTLEVEL_MIN) & 1) != 0) { foo[i] = "IS_LIGHTLEVEL_MIN_"+condition_lightlevel_min; i++; }
		if(((conditions >>> WidgetModifierConditionType.IS_LIGHTLEVEL_MAX) & 1) != 0) { foo[i] = "IS_LIGHTLEVEL_MAX_"+condition_lightlevel_max; i++; }
		if(((conditions >>> WidgetModifierConditionType.IS_WEATHER_RAIN) & 1) != 0) { foo[i] = "IS_WEATHER_RAIN"; i++; }
		if(((conditions >>> WidgetModifierConditionType.IS_WEATHER_CLEAR) & 1) != 0) { foo[i] = "IS_WEATHER_CLEAR"; i++; }
		if(((conditions >>> WidgetModifierConditionType.IS_SWIMMING) & 1) != 0) { foo[i] = "IS_SWIMMING"; i++; }
		if(((conditions >>> WidgetModifierConditionType.IS_NOT_SWIMMING) & 1) != 0) { foo[i] = "IS_NOT_SWIMMING"; i++; }
		if(((conditions >>> WidgetModifierConditionType.IS_SNEAKING) & 1) != 0) { foo[i] = "IS_SNEAKING"; i++; }
		if(((conditions >>> WidgetModifierConditionType.IS_NOT_SNEAKING) & 1) != 0) { foo[i] = "IS_NOT_SNEAKING"; i++; }
		if(((conditions >>> WidgetModifierConditionType.OVERLAY_ACTIVE) & 1) != 0) { foo[i] = "OVERLAY_ACTIVE"; i++; }
		if(((conditions >>> WidgetModifierConditionType.OVERLAY_INACTIVE) & 1) != 0) { foo[i] = "OVERLAY_INACTIVE"; i++; }
		
		return foo;
	}
	
	
	public void writeData(ByteBuf buff){
		//sync conditions
		buff.writeInt(conditions);
		buff.writeShort(condition_lightlevel_min);
		buff.writeShort(condition_lightlevel_max);
	}
	
	public void readData(ByteBuf buff){
		//sync conditions
		conditions = buff.readInt();
		condition_lightlevel_min = buff.readShort();
		condition_lightlevel_max = buff.readShort();
	}
	
	public void configureCondition(short type, boolean state){		
		if(state == true) 
			conditions |= (1 << type); 
		else
			conditions &= ~(1 << type); 
	}	
	
	//this stuff should be overwritten by childs
	public void apply(EntityPlayer player, boolean overlayActive){};	
	public short getType(){ return 0; };
	public Object[] getValues(){ return null; };
}
