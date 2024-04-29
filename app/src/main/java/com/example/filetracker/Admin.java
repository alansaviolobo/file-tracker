package com.example.filetracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;




import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.os.AsyncTask;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;


public class Admin extends AppCompatActivity implements  View.OnClickListener {

    // creating variables for our edittext, button and dbhandler
    private EditText EmployeeName, DivisionName;
    private Button addBtn, readBtn, scanBtn;
    private DBHandler dbHandler;

    //Apache POI
    private Button btnFetchData;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> employeeNames;
    private static final String EXCEL_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRnjsCiY_MV7PBvE8qkUjSqSuFrfyAQlpuoDbJ2WsItmd4LmswTjsTkFc-GQ6z2-Uluqn4fOC299enn/pub?gid=1956630541&single=true&output=csv";
    private static final String FILE_NAME = "employees.xlsx"; // Local file name

    // URLs for API endpoints
    private static final String URL2 = "https://goawrd.gov.in/file-tracker/scan?code=<code>&employee=<employee>&username=<username>";
    private static final String URL3 = "http://goawrd.gov.in/file-tracker/regcode?code=<code>&filename=<filename>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        // initializing all our variables.
        EmployeeName = findViewById(R.id.employee);
        DivisionName = findViewById(R.id.DivisionText);
        readBtn = findViewById(R.id.btnList);
        addBtn = findViewById(R.id.add);
        scanBtn = findViewById(R.id.scanQRButton);

        scanBtn.setOnClickListener(this);

        //Apache POI
        btnFetchData = findViewById(R.id.btnFetchData);
        listView = findViewById(R.id.listView);
        employeeNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeeNames);
        listView.setAdapter(adapter);

        btnFetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromServer();
            }
        });

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(Admin.this);

        // below line is to add on click listener for our add course button.
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // below line is to get data from all edit text fields.
                String Name = EmployeeName.getText().toString();
                String Division = DivisionName.getText().toString();


                // validating if the text fields are empty or not.
                if (Name.isEmpty()) {
                    Toast.makeText(Admin.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Division.isEmpty()) {
                    Toast.makeText(Admin.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
//                dbHandler.addNewCourse(Name, Division);

                // after adding the data we are displaying a toast message.
                Toast.makeText(Admin.this, "Course has been added.", Toast.LENGTH_SHORT).show();
                EmployeeName.setText("");
                DivisionName.setText("");

            }
        });


        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity via a intent.
                Intent i = new Intent(Admin.this, DataView.class);
                startActivity(i);
            }
        });


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


    private void fetchDataFromServer() {
        new FetchDataTask().execute(EXCEL_URL);
    }

    private class FetchDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String csvUrl = params[0];
            try {
                URL url = new URL(csvUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream outputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();

                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                new ReadCsvTask().execute();
            } else {
                Toast.makeText(Admin.this, "Failed to download CSV file", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class ReadCsvTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                FileInputStream fileInputStream = openFileInput(FILE_NAME);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

                SQLiteDatabase db = dbHandler.getWritableDatabase();

                String line;
                while ((line = reader.readLine()) != null) {
                    // Process each line of the CSV file
                    String[] values = line.split(","); // Assuming CSV is comma-separated
                    if (values.length >= 2) {
                        String name = values[1]; // Extract value from column 1
                        String division = values[0]; // Extract value from column 2

                        // Create a ContentValues object to store the data
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("EmployeeName", name); // Replace with actual column name
                        contentValues.put("Division", division); // Replace with actual column name

                        // Insert the data into the database
                        db.insert("File", null, contentValues); // Replace with actual table name
                    }
                }

                reader.close();
                fileInputStream.close();
                db.close(); // Close the database connection

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                adapter.notifyDataSetChanged();
                Toast.makeText(Admin.this, "Employee data fetched and saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Admin.this, "Failed to read CSV file or save data to database", Toast.LENGTH_SHORT).show();
            }
        }
    }
    

}
