package com.example.han.businesscardsindexer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class CardDetails extends AppCompatActivity {

    // Declare the class variables
    private Button saveChagesButton;
    private EditText personName;
    private EditText jobTitle;
    private EditText companyName;
    private EditText phone;
    private EditText email;
    private EditText postalAddress;
    private EditText additionalDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        // find the elements
        saveChagesButton = (Button) findViewById(R.id.saveButton);
        personName = (EditText) findViewById(R.id.editText2);
        jobTitle = (EditText) findViewById(R.id.editText3);
        companyName = (EditText) findViewById(R.id.editText4);
        phone = (EditText) findViewById(R.id.editText6);
        email = (EditText) findViewById(R.id.editText7);
        postalAddress = (EditText) findViewById(R.id.editText9);
        additionalDetails = (EditText) findViewById(R.id.editText10);

        // button listener for Save Changes
        saveChagesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
    }

    public HashMap<String, String> getFieldData(){
        HashMap<String, String> data = new HashMap<String, String>();


        return data;
    }
}
