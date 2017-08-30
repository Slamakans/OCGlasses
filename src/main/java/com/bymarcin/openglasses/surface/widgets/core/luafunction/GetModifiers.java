package com.bymarcin.openglasses.surface.widgets.core.luafunction;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;

import com.bymarcin.openglasses.lua.LuaFunction;
import com.bymarcin.openglasses.surface.WidgetModifierType;
import com.bymarcin.openglasses.surface.WidgetModifier;
import com.bymarcin.openglasses.surface.Widget;

public class GetModifiers extends LuaFunction{

	@Override
	public Object[] call(Context context, Arguments arguments) {
		super.call(context, arguments);
		Widget widget = getSelf().getWidget(); 
		
		Object[] foo;
		
		for(WidgetModifier mod : widget.WidgetModifierList.modifiers){
			switch(mod.getType()){
				default:
				return null;
			}			
		}
		
		throw new RuntimeException("Component does not exists!");
	}

	@Override
	public String getName() {
		return "getModifiers";
	}

}
