package net.nickac.buttondeck.networking.io;

import android.util.Log;

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
    private List<Runnable> eventConnected = new ArrayList<>();

    public TcpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        internalThread = new Thread(this::initSocket);
        internalThread.start();
    }

    public void onConnected(Runnable event) {
        eventConnected.add(event);
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
        List<Byte> readBytes = new ArrayList<>();
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(internalSocket.getInputStream());
            while (internalSocket != null && internalSocket.isConnected()) {
                if (inputStream.available() > 0) {
                    long packetNumber = inputStream.readLong();
                    INetworkPacket packet = Constants.getNewPacket(packetNumber);
                    if (packet != null) {
                        packet.fromInputStream(inputStream);
                        packet.execute(this, true);
                    }
                } else {
                    Thread.sleep(50);
                }
            }
        } catch (IOException e) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        DataOutputStream outputStream;
        try {
            outputStream = new DataOutputStream(internalSocket.getOutputStream());

            while (toDeliver != null && internalSocket != null && internalSocket.isConnected()) {
                if (toDeliver.size() < 1) {
                    Thread.sleep(50);
                    continue;
                }
                synchronized (toDeliver) {
                    for (INetworkPacket iNetworkPacket : toDeliver) {
                        Log.i("ButtonDeck", "Written packet with ID " + iNetworkPacket.getPacketId() + ".");
                        outputStream.writeLong(iNetworkPacket.getPacketId());
                        iNetworkPacket.toOutputStream(outputStream);
                        outputStream.flush();
                        iNetworkPacket.execute(this, false);
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
            for (Runnable r : eventConnected) {
                r.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataThread = new Thread(this::readData);
        dataThread.start();
        dataDeliveryThread = new Thread(this::sendData);
        dataDeliveryThread.start();
    }
}
