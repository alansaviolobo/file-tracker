package com.example.filetracker;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Admin extends AppCompatActivity implements View.OnClickListener {

    // URLs for API endpoints
    private static final String URL2 = "https://goawrd.gov.in/file-tracker/scan?code=<code>&employee=<employee>&username=<username>";
    private static final String URL3 = "http://goawrd.gov.in/file-tracker/regcode?code=<code>&filename=<filename>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        FloatingActionButton scanBtn = findViewById(R.id.scanQRButton);
        scanBtn.setOnClickListener(this);
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scanQRButton) {
            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
            } else {
                Toast.makeText(Admin.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() != null) {
                    String scannedData = intentResult.getContents();
                    String username = getIntent().getStringExtra("USERNAME");
                    Toast.makeText(this, "Scanned data: " + scannedData, Toast.LENGTH_SHORT).show();
                    openDialogForEmployeeName(scannedData, username);
                } else {
                    Toast.makeText(this, "Scan canceled or failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openDialogForEmployeeName(final String scannedData, final String username) {
        final String[] parts = scannedData.split(";");
        final String code = parts[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Employee Name");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_employee_name, null);
        final EditText inputEmployeeName = dialogView.findViewById(R.id.input_employee_name);
        final TextView textCode = dialogView.findViewById(R.id.text_code);
        final TextView textUsername = dialogView.findViewById(R.id.text_username);
        textCode.setText("Code: " + code);
        textUsername.setText("Username: " + username);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String employeeName = inputEmployeeName.getText().toString().trim();
                if (!employeeName.isEmpty()) {
                    sendRequestToURL2(code, username, employeeName);
                    Toast.makeText(Admin.this, "Submitted....", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Admin.this, "Please enter employee name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendRequestToURL2(final String code, final String username, final String employeeName) {
        String url = URL2.replace("<code>", code)
                .replace("<username>", username)
                .replace("<employee>", employeeName);

        Log.d("Admin", "URL2: " + url);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Admin.this, "Error: Network failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    if (responseBody.equals("Ok")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Admin.this, "Entry saved in database", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (responseBody.equals("File not in System")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                promptForFilename(code);
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Admin.this, "Error: Unsuccessful response", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void promptForFilename(final String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Filename");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filename = input.getText().toString().trim();
                if (!filename.isEmpty()) {
                    sendRequestToURL3(code, filename);
                } else {
                    Toast.makeText(Admin.this, "Please enter filename", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendRequestToURL3(final String code, final String filename) {
        String url = URL3.replace("<code>", code)
                .replace("<filename>", filename);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Admin.this, "Error: Network failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Admin.this, "Response from URL3: " + responseBody, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Admin.this, "Error: Unsuccessful response", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}