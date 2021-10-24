package com.example.lab02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        Product initProduct = (Product)extras.get("Product");
        nameView.setText( initProduct.getName());

        String priceString = String.format("%.8f", initProduct.getPrice());
        priceView.setText(priceString);
        DateFormat df = DateFormat.getDateInstance();
        String strDate = df.format(initProduct.getReceiptDate());
        dateView.setText(strDate);
        quantityView.setText(String.valueOf(initProduct.getQuantity()));

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            String name = nameView.getText().toString();
            float price = Float.parseFloat(priceView.getText().toString());
            Date date = initProduct.getReceiptDate();
            int quantity = Integer.parseInt(quantityView.getText().toString());
            Product pr = new Product(name, date, quantity, price);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("InitProduct", initProduct);
            returnIntent.putExtra("NewProduct", pr);
            setResult(100, returnIntent);
            finish();
        });
    }
}