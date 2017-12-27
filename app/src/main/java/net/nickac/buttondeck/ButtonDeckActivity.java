package net.nickac.buttondeck;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.nickac.buttondeck.networking.impl.HelloPacket;
import net.nickac.buttondeck.networking.io.TcpClient;

import static net.nickac.buttondeck.utils.Constants.PORT_NUMBER;
import static net.nickac.buttondeck.utils.Constants.sharedPreferences;
import static net.nickac.buttondeck.utils.Constants.sharedPreferencesName;

public class ButtonDeckActivity extends AppCompatActivity {

    TcpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_deck);
        //Ask android to set the app to landscape only.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //Ask android to remove the action bar.
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        if (sharedPreferences == null) {
            sharedPreferences = this.getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
        }

        client = new TcpClient("10.0.2.2", PORT_NUMBER);
        client.onConnected(() -> {
            client.sendPacket(new HelloPacket());
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        client.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.close();
    }
}
