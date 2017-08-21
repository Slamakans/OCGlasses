package com.bymarcin.openglasses.surface;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import com.bymarcin.openglasses.surface.widgets.core.attribute.IAlpha;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public interface IRenderableWidget {
	public void render(EntityPlayer player, double playerX, double playerY, double playerZ, float alpha);
	public RenderType getRenderType();
	public boolean shouldWidgetBeRendered();
	public UUID getWidgetOwner();
	public float getAlpha(boolean HUDactive);
}
