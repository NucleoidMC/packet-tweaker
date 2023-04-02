package xyz.nucleoid.packettweaker.impl;

import net.minecraft.network.ClientConnection;

public interface ConnectionHolder {
    void setConnection(ClientConnection connection);
}
