package xyz.nucleoid.packettweaker;

import net.minecraft.network.ClientConnection;

public interface ConnectionHolder {
    void setConnection(ClientConnection connection);
}
