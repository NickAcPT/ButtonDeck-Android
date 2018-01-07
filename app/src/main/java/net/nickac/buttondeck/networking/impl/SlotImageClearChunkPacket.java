package net.nickac.buttondeck.networking.impl;

import android.widget.ImageButton;
import android.widget.ImageView;

import net.nickac.buttondeck.networking.INetworkPacket;
import net.nickac.buttondeck.networking.io.ArchitectureAnnotation;
import net.nickac.buttondeck.networking.io.PacketArchitecture;
import net.nickac.buttondeck.networking.io.TcpClient;
import net.nickac.buttondeck.utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NickAc on 06/01/2018.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
@ArchitectureAnnotation(PacketArchitecture.SERVER_TO_CLIENT)
public class SlotImageClearChunkPacket implements INetworkPacket {
    List<Integer> toClear = new ArrayList<>();

    @Override
    public void execute(TcpClient client, boolean received) {
        if (Constants.buttonDeckContext != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int slot : toClear) {
                        ImageButton view = Constants.buttonDeckContext.getImageButton(slot);

                        if (view != null) {
                            //Log.i("ButtonDeck", "Setting button [CHUNK]!");
                            view.setScaleType(ImageView.ScaleType.FIT_XY);
                            view.setBackgroundResource(0);
                        }
                        System.gc();
                    }
                    synchronized (this) {
                        this.notify();
                    }
                }
            };
            synchronized (runnable) {
                Constants.buttonDeckContext.runOnUiThread(runnable);
                try {
                    runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public INetworkPacket clonePacket() {
        return new SlotImageClearChunkPacket();
    }

    @Override
    public long getPacketId() {
        return 10;
    }

    @Override
    public void toOutputStream(DataOutputStream writer) {

    }

    @Override
    public void fromInputStream(DataInputStream reader) throws IOException {
        int number = reader.readInt();
        for (int i = 0; i < number; i++) {
            toClear.add(reader.readInt());
        }
    }
}
