package com.bymarcin.openglasses.network.packet;

import java.io.IOException;

import com.bymarcin.openglasses.network.Packet;

import com.bymarcin.openglasses.surface.ClientSurface;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerminalStatusPacket extends Packet<TerminalStatusPacket, IMessage>{
	public static enum TerminalEvent{
		SYNC_SCREEN_SIZE,
		ASYNC_SCREEN_SIZES
	}

	TerminalEvent terminalEvent;

	public TerminalStatusPacket(TerminalEvent status) {
		this.terminalEvent = status;
	}

	public TerminalStatusPacket() {}  //dont remove, in use by NetworkRegistry.registerPacket in OpenGlasses.java

	@Override
	protected void read() throws IOException {
		this.terminalEvent = TerminalEvent.values()[readInt()];
	}

	@Override
	protected void write() throws IOException {
		writeInt(this.terminalEvent.ordinal());
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected IMessage executeOnClient() {
 		switch(this.terminalEvent){
			case ASYNC_SCREEN_SIZES:
				ClientSurface.instances.eventHandler.sendResolution();
				break;
		}

		return null;
	}

	@Override
	protected IMessage executeOnServer() {
		return null;
	}

}
