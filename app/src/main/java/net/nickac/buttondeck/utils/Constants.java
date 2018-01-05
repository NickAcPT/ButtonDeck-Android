package net.nickac.buttondeck.utils;

import android.content.SharedPreferences;
import android.util.LongSparseArray;

import net.nickac.buttondeck.ButtonDeckActivity;
import net.nickac.buttondeck.networking.INetworkPacket;
import net.nickac.buttondeck.networking.impl.AlternativeHelloPacket;
import net.nickac.buttondeck.networking.impl.ButtonInteractPacket;
import net.nickac.buttondeck.networking.impl.DesktopDisconnectPacket;
import net.nickac.buttondeck.networking.impl.DeviceIdentityPacket;
import net.nickac.buttondeck.networking.impl.HeartbeatPacket;
import net.nickac.buttondeck.networking.impl.HelloPacket;
import net.nickac.buttondeck.networking.impl.SingleSlotImageChangePacket;
import net.nickac.buttondeck.networking.impl.SlotImageChangeChunkPacket;

/**
 * Created by NickAc on 24/12/2017.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
public class Constants {
    public static ButtonDeckActivity buttonDeckContext;
    public static String sharedPreferencesName = "ApplicationData";
    public static SharedPreferences sharedPreferences;

    public static String DEVICE_GUID_PREF = "device_guid";
    public static int PROTOCOL_VERSION = 9;
    public static int PORT_NUMBER = 5080;
    public static LongSparseArray<INetworkPacket> packetMap = new LongSparseArray<>();

    static {
        registerPacket(new HelloPacket());
        registerPacket(new DeviceIdentityPacket());
        registerPacket(new HeartbeatPacket());
        registerPacket(new DesktopDisconnectPacket());
        registerPacket(new SingleSlotImageChangePacket());
        registerPacket(new AlternativeHelloPacket());
        registerPacket(new SlotImageChangeChunkPacket());
        registerPacket(new ButtonInteractPacket(ButtonInteractPacket.ButtonAction.NONE));
    }


    public static void registerPacket(INetworkPacket packet) {
        packetMap.append(packet.getPacketId(), packet);
    }

    public static INetworkPacket getNewPacket(long id) {
        INetworkPacket packet = packetMap.get(id, null);
        return packet.clonePacket();
    }
}
