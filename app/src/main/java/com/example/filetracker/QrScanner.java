package com.example.filetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/** @noinspection ALL*/
public class QrScanner extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_QR_SCAN = 101;

    Button scanBtn;
    TextView EmployeeText, DivisionText;

    private final ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);
                if (intentResult != null) {
                    String scanResult = intentResult.getContents();
                    String scanFormat = intentResult.getFormatName();

                    // Display scan result and format
                    EmployeeText.setText(scanResult != null ? scanResult : "Cancelled");
                    DivisionText.setText(scanFormat != null ? scanFormat : "");
                }
            }
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        scanBtn = findViewById(R.id.scanQRButton);
        EmployeeText = findViewById(R.id.employee);
        DivisionText = findViewById(R.id.DivisionText);

        // Set OnClickListener for the button
        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Start scanning activity
        IntentIntegrator intentIntegrator= new IntentIntegrator(this)
                .setPrompt("Scan a barcode or QR Code")
                .setOrientationLocked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK && data != null) {
            String scanResult = data.getStringExtra(Intents.Scan.RESULT);
            String scanFormat = data.getStringExtra(Intents.Scan.RESULT_FORMAT);

            // Display scan result and format
            EmployeeText.setText(scanResult != null ? scanResult : "Cancelled");
            DivisionText.setText(scanFormat != null ? scanFormat : "");
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}