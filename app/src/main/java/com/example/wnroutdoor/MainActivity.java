package com.example.wnroutdoor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //View object
    private Button buttonScanning;
    private TextView textViewName,textViewclass,textViewNim;
    //qr scanning object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //view object
        buttonScanning = (Button) findViewById(R.id.buttonscan);
        textViewName = (TextView) findViewById(R.id.textViewNama);
        textViewclass = (TextView) findViewById(R.id.textViewKelas);
        textViewNim = (TextView) findViewById(R.id.textViewNIM);

        //Inisialisasi Scan
        qrScan = new IntentIntegrator(this);
        //Inisialisasi onClickListener
        buttonScanning.setOnClickListener(this);
    }

    //untuk mendapatkan hasil scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator .parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            //jika qrCode tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil Scanning Tidak Ada",
                        Toast.LENGTH_LONG).show();
            }else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }else if (Patterns.PHONE.matcher(result.getContents()).matches()) {
                String telp = String.valueOf(result.getContents());
                Intent CallIntent = new Intent(Intent.ACTION_CALL);
                CallIntent.setData(Uri.parse("tel:" + telp));
                startActivity(CallIntent);
                try {
                    startActivity(Intent.createChooser(CallIntent,"waiting..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this,"There ada no phone apk client installed.", Toast.LENGTH_SHORT).show();
                }
            } else{
                //jika qCode tidak ditemukan datanya
                try {
                    //konversi datanya ke json
                    JSONObject obj = new JSONObject(result.getContents());
                    //diset nilai datanya ke textview
                    textViewName.setText(obj.getString("Nama"));
                    textViewclass.setText(obj.getString("Kelas"));
                    textViewNim.setText(obj.getString("Nim"));
                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(),
                            Toast.LENGTH_LONG).show();
                }
            } }
        {
            try {
                String geoUri = result.getContents();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                //Set Package
                intent.setPackage("com.google.android.apps.maps");

                //Set Flag
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            } finally {

            }

        } {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View v) {
        //inisialisasi qrCode scanning
        qrScan.initiateScan();
    }
}