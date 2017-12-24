package net.nickac.buttondeck.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by NickAc on 23/12/2017.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
public interface INetworkPacket {
    long getPacketId();

    void toOutputStreamWriter(DataOutputStream writer) throws IOException;

    void fromInputStreamReader(DataInputStream reader);
}
