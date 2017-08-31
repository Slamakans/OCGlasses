package com.bymarcin.openglasses.surface;

public class WidgetModifierType {
	public static final short 
		TRANSLATE 	= 1, 
		COLOR 		= 2, 
		SCALE 		= 4,
		ROTATE 		= 8,
		TEXTURE 	= 16;
		
	public static String getName(int v){
		switch(v){
			case 1:	return "TRANSLATE";
			case 2:	return "COLOR";
			case 4:	return "SCALE";
			case 8:	return "ROTATE";
			case 16:return "TEXTURE";
		}
		return "UNKNOWN";
	}
}
