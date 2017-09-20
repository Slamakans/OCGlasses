package com.bymarcin.openglasses.utils;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.block.material.Material;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class OGUtils {
	
	public static int getIntFromColor(float red, float green, float blue, float alpha){
	    int R = Math.round(255 * red);
	    int G = Math.round(255 * green);
	    int B = Math.round(255 * blue);
	    int A = Math.round(255 * alpha);
	    A = (A << 24) & 0xFF000000;
	    R = (R << 16) & 0x00FF0000;
	    G = (G << 8) & 0x0000FF00;
	    B = B & 0x000000FF;
	    return A | R | G | B;
	}
	
	public static boolean isIntColorVisible(int color){
		if((color & 0x000000FF) > 0) return true;
		
		return false;		
	}
	
	public static boolean inRange(double x, double y, double z, double sx, double sy, double sz, double r){
		return (((x-sx)*(x-sx)) + ((y-sy)*(y-sy)) + ((z-sz)*(z-sz))) <= (r*r);
	}
	
	public static boolean inRange(EntityPlayer player, double sx, double sy, double sz, double r){
		return inRange(player.posX, player.posY, player.posZ, sx, sy, sz, r);
	}
	
	public static boolean isLookingAt(RayTraceResult pos, float[] target){
		if(pos == null) return false;
		if(pos.getBlockPos().getX() != target[0]) return false;
		if(pos.getBlockPos().getY() != target[1]) return false;
		if(pos.getBlockPos().getZ() != target[2]) return false;
					
		return true;
	}
	
	public static int getLightLevelPlayer(EntityPlayer e){		
		return (int) e.world.getLightFor(EnumSkyBlock.SKY, new BlockPos(e.posX, e.posY + 1, e.posZ));
    }
    
    public static boolean isPlayerSwimming(EntityPlayer e){
		if(e.world.getBlockState(new BlockPos(e.posX, e.posY, e.posZ)).getMaterial().isLiquid()
			|| e.world.getBlockState(new BlockPos(e.posX, e.posY+1, e.posZ)).getMaterial().isLiquid())
			return true;
			
		return false;
	}
    
    public static String getPlayerBiomeName(EntityPlayer e){		
		return e.world.getBiome(e.getPosition()).getBiomeName();
	}
	
	public static float getPlayerBiomeTemp(EntityPlayer e){	
		BlockPos pos = e.getPosition();	
		return e.world.getBiome(pos).getFloatTemperature(pos);
	}
}
