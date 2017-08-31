package com.bymarcin.openglasses.surface;

public class WidgetModifierConditionType {
   public static final short 
		IS_LIGHTLEVEL_MIN = 1,
		IS_LIGHTLEVEL_MAX = 2,
		IS_WEATHER_RAIN = 3,
		IS_WEATHER_CLEAR = 4,
		IS_SWIMMING = 5,
		IS_NOT_SWIMMING = 6,
		IS_SNEAKING = 7,
		IS_NOT_SNEAKING	= 8,
		OVERLAY_ACTIVE = 9,
		OVERLAY_INACTIVE = 10;
		
	public static String getName(short v){
		switch(v){
			case 1: return "IS_LIGHTLEVEL_MIN";
			case 2: return "IS_LIGHTLEVEL_MAX";
			case 3: return "IS_WEATHER_RAIN";
			case 4: return "IS_WEATHER_CLEAR";
			case 5: return "IS_SWIMMING";
			case 6: return "IS_NOT_SWIMMING";
			case 7: return "IS_SNEAKING";
			case 8: return "IS_NOT_SNEAKING";
			case 9: return "OVERLAY_ACTIVE";
			case 10: return "OVERLAY_INACTIVE";
		}		
		return "UNKNOWN";
	}
	
	public static short getIndex(String name){
		switch(name){
			case "IS_LIGHTLEVEL_MIN": return 1;
			case "IS_LIGHTLEVEL_MAX": return 2;
			case "IS_WEATHER_RAIN": return 3;
			case "IS_WEATHER_CLEAR": return 4;
			case "IS_SWIMMING": return 5;
			case "IS_NOT_SWIMMING": return 6;
			case "IS_SNEAKING": return 7;
			case "IS_NOT_SNEAKING": return 8;
			case "OVERLAY_ACTIVE": return 9;
			case "OVERLAY_INACTIVE": return 10;
		}		
		return 0;
	}
}
