package com.cleannrooster.cleannarsenal.entities;

import carpet.fakes.ClientConnectionInterface;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;

public class FakeClientConnection extends ClientConnection
{
    public FakeClientConnection(NetworkSide p)
    {
        super(p);
        // compat with adventure-platform-fabric. This does NOT trigger other vanilla handlers for establishing a channel
        // also makes #isOpen return true, allowing enderpearls to teleport fake players
    }



    @Override
    public void handleDisconnection()
    {
    }

    @Override
    public void setPacketListener(PacketListener packetListener)
    {
    }


}