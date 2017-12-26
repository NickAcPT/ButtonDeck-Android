package net.nickac.buttondeck.networking.io;

import net.nickac.buttondeck.networking.INetworkPacket;
import net.nickac.buttondeck.utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NickAc on 26/12/2017.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
public class TcpClient {
    private final List<INetworkPacket> toDeliver = new ArrayList<>();
    private String ip;
    private int port;
    private Socket internalSocket;
    private Thread internalThread;
    private Thread dataThread;
    private Thread dataDeliveryThread;

    public TcpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        internalThread = new Thread(this::initSocket);
        internalThread.start();
    }

    public void sendPacket(INetworkPacket packet) {
        ArchitectureAnnotation annot = packet.getClass().getAnnotation(ArchitectureAnnotation.class);
        if (annot != null) {
            if (!(annot.value() == PacketArchitecture.CLIENT_TO_SERVER || annot.value() == PacketArchitecture.BOTH_WAYS)) {
                throw new IllegalStateException("Packet doesn't support being sent to the server.");
            }
        }
        toDeliver.add(packet);


    }

    private void readData() {
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(internalSocket.getInputStream());
            while (internalSocket != null && internalSocket.isConnected()) {
                long packetNumber = inputStream.readLong();
                INetworkPacket packet = Constants.getNewPacket(packetNumber);
                if (packet != null) {
                    packet.fromInputStream(inputStream);
                    packet.execute();
                }
            }
        } catch (IOException e) {
        }
    }

    private void sendData() {
        DataOutputStream outputStream;
        try {
            outputStream = new DataOutputStream(internalSocket.getOutputStream());

            while (toDeliver != null && internalSocket != null && internalSocket.isConnected()) {
                if (toDeliver.size() < 1) {
                    Thread.sleep(50);
                }
                synchronized (toDeliver) {
                    for (INetworkPacket iNetworkPacket : toDeliver) {
                        outputStream.writeLong(iNetworkPacket.getPacketId());
                        iNetworkPacket.toOutputStream(outputStream);
                    }
                    toDeliver.clear();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            internalSocket.close();
        } catch (IOException e) {

        }
    }

    private void initSocket() {
        try {
            internalSocket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataThread = new Thread(this::readData);
        dataThread.start();
        dataDeliveryThread = new Thread(this::sendData);
        dataDeliveryThread.start();
    }
}
