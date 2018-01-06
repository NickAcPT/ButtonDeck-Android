package net.nickac.buttondeck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.nickac.buttondeck.networking.impl.ButtonInteractPacket;
import net.nickac.buttondeck.networking.impl.HelloPacket;
import net.nickac.buttondeck.networking.io.TcpClient;
import net.nickac.buttondeck.utils.Constants;

import java.io.IOException;

import static net.nickac.buttondeck.utils.Constants.sharedPreferences;
import static net.nickac.buttondeck.utils.Constants.sharedPreferencesName;

public class ButtonDeckActivity extends AppCompatActivity {

    public static final String EXTRA_IP = "net.nickac.buttondeck.networking.IP";
    private static final int IDLE_DELAY_MINUTES = 5;
    private static TcpClient client;
    Handler _idleHandler = new Handler();
    Runnable _idleRunnable = () -> {
        dimScreen(1.0f);
    };

    Vibrator vibe;

    public void dimScreen(float dim) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = dim;
        getWindow().setAttributes(lp);
    }

    public ImageButton getImageButton(int id) {
        return findViewById(getResources().getIdentifier("button" + id, "id", Constants.buttonDeckContext.getPackageName()));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default activity creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_deck);

        //Save our reference on a variable. This will allow us to access this activity later.
        Constants.buttonDeckContext = this;

        Intent intent = getIntent();
        String connectIP = intent.getStringExtra(EXTRA_IP);
        int connectPort = Constants.PORT_NUMBER;

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Ask android to keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //Ask android to set the app to landscape only.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //Ask android to remove the action bar, since we don't need it.
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        if (sharedPreferences == null) {
            sharedPreferences = this.getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
        }


        if (savedInstanceState == null && client == null) {
            client = new TcpClient(connectIP, connectPort);
            try {
                client.connect();
                client.onConnected(() -> client.sendPacket(new HelloPacket()));
            } catch (IOException e) {
            }
        }

        for (int i = 0; i < 15; i++) {
            final boolean[] mDownTouch = {false};

            ImageView button = getImageButton(i + 1);
            if (button != null) {
                int finalI = i;
                button.setOnTouchListener((view, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mDownTouch[0] = true;
                            if (client != null) {
                                client.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_DOWN));
                            }
                            return true;

                        case MotionEvent.ACTION_UP:
                            if (mDownTouch[0]) {
                                mDownTouch[0] = false;
                                vibe.vibrate(100);
                                view.performClick();
                                if (client != null) {
                                    client.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_UP));
                                    client.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_CLICK));
                                }
                                return true;
                            }
                    }
                    return false;
                });
            }
        }


    }

    @Override
    public void onUserInteraction() {
        dimScreen(0.0f);
        super.onUserInteraction();
        delayedIdle(IDLE_DELAY_MINUTES);

    }

    private void delayedIdle(int delayMinutes) {
        _idleHandler.removeCallbacks(_idleRunnable);
        _idleHandler.postDelayed(_idleRunnable, (delayMinutes * 1000 * 60));
    }


    @Override
    protected void onPause() {
        //Constants.buttonDeckContext = null;
        super.onPause();

    }

    @Override
    protected void onStop() {
        Constants.buttonDeckContext = null;
        super.onStop();
        if (client != null) client.close();
        client = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.buttonDeckContext = this;
    }
}
