package net.nickac.buttondeck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import net.nickac.buttondeck.utils.Constants;
import net.nickac.buttondeck.utils.networkscan.NetworkDevice;
import net.nickac.buttondeck.utils.networkscan.NetworkDeviceAdapter;
import net.nickac.buttondeck.utils.networkscan.NetworkSearch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.protocolVersionTextView);
        textView.setText(textView.getText().toString().replace("{0}", String.valueOf(Constants.PROTOCOL_VERSION)));

        scanDevices();

    }

    private void scanDevices() {
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
                    //Connect to the device
                    Intent intent = new Intent(this, ButtonDeckActivity.class);
                    intent.putExtra(ButtonDeckActivity.EXTRA_IP, devices.get(0).getIp());
                    startActivity(intent);
                    break;
            }
            Toast.makeText(this, "Found " + devices.size() + " devices on the network!", Toast.LENGTH_LONG).show();
        });

    }
}
