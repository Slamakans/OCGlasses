package com.bymarcin.openglasses.network.packet;

import java.io.IOException;

import com.bymarcin.openglasses.network.Packet;
import com.bymarcin.openglasses.surface.ClientSurface;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class TerminalStatusPacket extends Packet<TerminalStatusPacket, IMessage>{
	TerminalStatus status;
	
	public TerminalStatusPacket() {}
	
	public TerminalStatusPacket(TerminalStatus status) {
		this.status = status;
	}
	
	@Override
	protected void read() throws IOException {
		status = TerminalStatus.values()[readInt()];
	}

	@Override
	protected void write() throws IOException {
		writeInt(status.ordinal());
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected IMessage executeOnClient() {
		return null;
	}

	@Override
	protected IMessage executeOnServer() {
		return null;
	}
	public enum TerminalStatus{
		HavePower,NoPower;
	}
}
