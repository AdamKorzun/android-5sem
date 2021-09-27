package com.example.lab02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ArrayList<Product> products = new ArrayList<>();
    productAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillList(15);
        ListView productList = (ListView)findViewById(R.id.productList);
        adapter = new productAdapter(getApplicationContext(),
                R.layout.list_item_view, products);
        productList.setAdapter(adapter);


        registerForContextMenu(productList);
    }
    private void fillList(int numberOfProducts){
        for (int i = 0; i < numberOfProducts; i++){
            products.add(new Product("Product " + i, new Date(101,
                    1, 1), 5, (float)30.5));
        }
    }

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