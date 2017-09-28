package com.bymarcin.openglasses.gui;

import com.bymarcin.openglasses.event.ClientEventHandler;
import com.bymarcin.openglasses.network.NetworkRegistry;
import com.bymarcin.openglasses.network.packet.GlassesEventPacket;
import com.bymarcin.openglasses.surface.ClientSurface;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class InteractGui extends GuiScreen {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){}

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(ClientSurface.instances.glasses == null) return;
        NetworkRegistry.packetHandler.sendToServer(new GlassesEventPacket(GlassesEventPacket.EventType.INTERACT_OVERLAY, ClientSurface.instances.lastBind, mc.player, mouseX, mouseY, mouseButton));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(!Keyboard.isKeyDown(ClientEventHandler.interactGUIKey.getKeyCode())){
			ClientSurface.instances.OverlayActive = false;
            mc.displayGuiScreen(null);
        }
    }
}
