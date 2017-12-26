package net.nickac.buttondeck;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.nickac.buttondeck.networking.impl.HelloPacket;
import net.nickac.buttondeck.networking.io.TcpClient;

import static net.nickac.buttondeck.utils.Constants.PORT_NUMBER;

public class ButtonDeckActivity extends AppCompatActivity {

    TcpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_deck);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        client = new TcpClient("10.0.2.2", PORT_NUMBER);
        client.sendPacket(new HelloPacket());
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.close();
    }
}
