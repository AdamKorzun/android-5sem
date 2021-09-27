package com.example.lab02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ArrayList<Product> products = new ArrayList<>();
    ProductAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillList(15);
        ListView productList = findViewById(R.id.productList);
        adapter = new ProductAdapter(getApplicationContext(),
                R.layout.list_item_view, products);
        productList.setAdapter(adapter);
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),  product_activity.class);
                intent.putExtra("Name", adapter.getItem(i).getName());
                intent.putExtra("Price", adapter.getItem(i).getPrice());
                intent.putExtra("Quantity", adapter.getItem(i).getQuantity());
                intent.putExtra("ReceiptDate", adapter.getItem(i).getReceiptDate());
                startActivity(intent);

            }
        });
        Button sortButton = findViewById(R.id.sortButton);
        sortButton.setOnClickListener(sortButtonHandler);

        registerForContextMenu(productList);
    }
    private void fillList(int numberOfProducts){
        for (int i = 0; i < numberOfProducts; i++){
            Random r = new Random();
            float random = 0 + r.nextFloat() * (100);
            int randomQuantity = r.nextInt(100);
            int randomDay = 1 + r.nextInt(59);
            Date ct = Calendar.getInstance().getTime();
            long diffMil = TimeUnit.MILLISECONDS.convert(randomDay, TimeUnit.DAYS);

            ct = new Date(ct.getTime() - diffMil);
//                    Calendar.getInstance().set(randomMonth, randomDay);
            products.add(new Product("Product " + i, ct, randomQuantity, random));

        }
    }
    private View.OnClickListener sortButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<Product> productsToHide = new ArrayList<>();
            for (int i = 0; i < adapter.getCount(); i++){
                Product tempPr = adapter.getItem(i);
                System.out.println(tempPr.getName());
                Date currentTime = Calendar.getInstance().getTime();
                long diffInMillies = Math.abs(currentTime.getTime() - tempPr.getReceiptDate().getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (diff < 31){
                    if (tempPr.getPrice() > 50){
                        productsToHide.add(tempPr);
                    }
//                    System.out.println(tempPr.getReceiptDate() + "\n" + diff + "\n");

                }

            }
            for (int i = 0; i < productsToHide.size(); i++){
                adapter.remove(productsToHide.get(i));
            }

            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.product_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo i = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.productPage:
                Toast.makeText(this, "Page", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.deleteProduct:
                products.remove(i.position);
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
                return super.onContextItemSelected(item);

        }
    }
}