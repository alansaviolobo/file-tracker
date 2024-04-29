package com.example.filetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// implements onClickListener for the onclick behaviour of button
public class QrCode extends AppCompatActivity implements View.OnClickListener {
    Button scanBtn;
    TextView EmployeeName, DivisionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        // referencing and initializing
        // the button and textviews
        scanBtn = findViewById(R.id.scanQRButton);
        EmployeeName = findViewById(R.id.employee);
        DivisionName = findViewById(R.id.DivisionText);

        // adding listener to the button
        scanBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of QR library
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                String scannedData = intentResult.getContents();
                // Assuming the scanned data is in vCard format
                String[] parts = scannedData.split("\n");
                String firstName = "", lastName = "";
                for (String part : parts) {
                    if (part.startsWith("FN:")) {
                        // Extract first name
                        firstName = part.substring(3);
                    } else if (part.startsWith("N:")) {
                        // Extract last name
                        String[] nameParts = part.substring(2).split(";");
                        lastName = nameParts[0];
                    }
                }
                // Assuming you have EditText fields for first name and last name
                EmployeeName.setText(firstName);
                DivisionName.setText(lastName);
            } else {
                // QR code scanning canceled
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
