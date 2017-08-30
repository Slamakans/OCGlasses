package com.bymarcin.openglasses.surface;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public interface IRenderableWidget {
	public void render(EntityPlayer player, double playerX, double playerY, double playerZ, boolean overlayActive);
	public RenderType getRenderType();
	public boolean shouldWidgetBeRendered();
	public UUID getWidgetOwner();
}
