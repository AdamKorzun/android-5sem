package com.example.lab02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class product_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        TextView nameView = findViewById(R.id.productNameView);
        TextView priceView = findViewById(R.id.productPriceView);
        TextView dateView = findViewById(R.id.productReceiptDateView);
        TextView quantityView = findViewById(R.id.productQuantityView);

        Bundle extras = getIntent().getExtras();
        nameView.setText( extras.getString("Name"));

        String priceString = String.format("%.2f", extras.getFloat("Price"));
        priceView.setText(priceString + "$");
        DateFormat df = DateFormat.getDateInstance();
        String strDate = df.format((Date)extras.get("ReceiptDate"));
        dateView.setText(strDate);
        quantityView.setText(String.valueOf(extras.getInt("Quantity")));

    }
}