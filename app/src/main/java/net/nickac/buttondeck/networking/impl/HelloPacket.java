package net.nickac.buttondeck.networking.impl;

import net.nickac.buttondeck.networking.INetworkPacket;
import net.nickac.buttondeck.networking.io.ArchitectureAnnotation;
import net.nickac.buttondeck.networking.io.PacketArchitecture;
import net.nickac.buttondeck.utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by NickAc on 24/12/2017.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
@ArchitectureAnnotation(PacketArchitecture.CLIENT_TO_SERVER)
public class HelloPacket implements INetworkPacket {
    @Override
    public void execute() {

    }

    @Override
    public INetworkPacket clonePacket() {
        return new HelloPacket();
    }

    @Override
    public long getPacketId() {
        return 1;
    }

    @Override
    public void toOutputStream(DataOutputStream writer) throws IOException {
        writer.writeInt(Constants.PROTOCOL_VERSION);
    }

    @Override
    public void fromInputStream(DataInputStream reader) throws IOException {

    }

}
