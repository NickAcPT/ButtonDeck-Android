package net.nickac.buttondeck.utils;

import android.content.SharedPreferences;
import android.util.LongSparseArray;

import net.nickac.buttondeck.networking.INetworkPacket;
import net.nickac.buttondeck.networking.impl.DeviceIdentity;
import net.nickac.buttondeck.networking.impl.HelloPacket;

/**
 * Created by NickAc on 24/12/2017.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
public class Constants {
    public static String sharedPreferencesName = "ApplicationData";
    public static SharedPreferences sharedPreferences;

    public static String DEVICE_GUID_PREF = "device_guid";
    public static int PROTOCOL_VERSION = 5;
    public static int PORT_NUMBER = 5080;
    public static LongSparseArray<INetworkPacket> packetMap = new LongSparseArray<>();

    static {
        registerPacket(new HelloPacket());
        registerPacket(new DeviceIdentity());
    }


    public static void registerPacket(INetworkPacket packet) {
        packetMap.append(packet.getPacketId(), packet);
    }

    public static INetworkPacket getNewPacket(long id) {
        INetworkPacket packet = packetMap.get(id, null);
        return packet.clonePacket();
    }
}
