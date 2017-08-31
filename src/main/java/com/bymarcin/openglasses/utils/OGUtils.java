package com.bymarcin.openglasses.utils;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.util.math.BlockPos;

//import net.minecraft.world.World; 
import net.minecraft.world.IBlockAccess; 
import net.minecraft.entity.player.EntityPlayer;

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
	
	public static boolean inRange(double x, double y, double z, double sx, double sy, double sz, double r){
		return (((x-sx)*(x-sx)) + ((y-sy)*(y-sy)) + ((z-sz)*(z-sz))) <= (r*r);
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
		//if (this.status != EntityBoat.Status.UNDER_WATER && this.status != EntityBoat.Status.UNDER_FLOWING_WATER)		
		return false;
	}
    
    public static String getPlayerBiomeName(EntityPlayer e){		
		//return e.world.getBiomeGenForCoords(e.getPosition()).biomeName;
		return "";
	}
	
	public static float getPlayerBiomeTemp(EntityPlayer e){		
		//return e.world.getBiomeGenForCoords(e.getPosition()).getFloatTemperature();
		return 0;
	}
			
	public static void setBit(byte[] data, int pos, int val) {
      short posBit = (short) (pos%8);
      byte oldByte = data[pos/8];
      oldByte = (byte) (((0xFF7F>>posBit) & oldByte) & 0x00FF);
      byte newByte = (byte) ((val<<(8-(posBit+1))) | oldByte);
      data[pos/8] = newByte;
   }
   
   public static boolean getBit(byte[] data, int pos) {
      if((data[pos/8]>>(8-((pos%8)+1)) & 0x0001) == 0)  
		return true;
	  
	  return false;
   }    
}
