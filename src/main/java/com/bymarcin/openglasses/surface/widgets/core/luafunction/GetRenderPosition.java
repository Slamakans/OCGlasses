package com.bymarcin.openglasses.surface.widgets.core.luafunction;

import com.bymarcin.openglasses.surface.WidgetGLWorld;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;

import com.bymarcin.openglasses.lua.LuaFunction;
import net.minecraft.util.math.BlockPos;

public class GetRenderPosition extends LuaFunction{

    @Override
    public Object[] call(Context context, Arguments arguments) {
        super.call(context, arguments);
        WidgetGLWorld widget = (WidgetGLWorld) getSelf().getWidget();
        if(widget instanceof WidgetGLWorld){
            BlockPos renderPosition = getSelf().getWidget().WidgetModifierList.getRenderPosition(arguments.checkString(0));

            return new Object[] { renderPosition.getX(), renderPosition.getY(), renderPosition.getZ() };
        }
        throw new RuntimeException("Component does not exists!");
    }

    @Override
    public String getName() {
        return "getRenderPosition";
    }

}
