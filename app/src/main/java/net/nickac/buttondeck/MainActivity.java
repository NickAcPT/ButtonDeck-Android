package net.nickac.buttondeck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.nickac.buttondeck.utils.Constants;
import net.nickac.buttondeck.utils.networkscan.NetworkDevice;
import net.nickac.buttondeck.utils.networkscan.NetworkDeviceAdapter;
import net.nickac.buttondeck.utils.networkscan.NetworkSearch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean autoScan = true;
    String autoScanPref = "didAutoScan";

    @Override
    public void onBackPressed() {
        getPreferences(MODE_PRIVATE).edit().putBoolean(autoScanPref, false).apply();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button rescanButton = findViewById(R.id.rescanButton);

        rescanButton.setOnClickListener(view -> scanDevices());

        TextView textView = findViewById(R.id.protocolVersionTextView);
        textView.setText(textView.getText().toString().replace("{0}", String.valueOf(Constants.PROTOCOL_VERSION)));

        boolean alreadyScanned = getPreferences(MODE_PRIVATE).getBoolean(autoScanPref, false);
        rescanButton.setVisibility(!alreadyScanned ? View.INVISIBLE : View.VISIBLE);

        if (!alreadyScanned) {
            scanDevices();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void scanDevices() {
        Button rescanButton = findViewById(R.id.rescanButton);

        //Hide the button for now.
        rescanButton.setVisibility(View.INVISIBLE);


        NetworkDeviceAdapter adapter = new NetworkDeviceAdapter(this, new ArrayList<>());
        NetworkSearch.AsyncScan scan = new NetworkSearch.AsyncScan(adapter);
        scan.execute(adapter);

        scan.setAfterCompletion(() -> {
            List<NetworkDevice> devices = adapter.getDevices();
            switch (devices.size()) {
                case 0:
                    TextView textView = findViewById(R.id.statusTextView);
                    textView.setText(getString(R.string.devices_found_none));
                    break;
                case 1:
                    Toast.makeText(this, "Connecting to " + devices.get(0).getDeviceName() + "!", Toast.LENGTH_LONG).show();

                    //Connect to the device
                    Intent intent = new Intent(this, ButtonDeckActivity.class);
                    intent.putExtra(ButtonDeckActivity.EXTRA_IP, devices.get(0).getIp());
                    startActivity(intent);
                    break;
                default:

                    break;
            }
            rescanButton.setVisibility(View.VISIBLE);
            getPreferences(MODE_PRIVATE).edit().putBoolean(autoScanPref, true).apply();
        });

    }
}
