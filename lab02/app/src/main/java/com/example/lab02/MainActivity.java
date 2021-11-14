package com.example.lab02;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Environment;
import android.os.ParcelFileDescriptor;
//import android.provider.DocumentsContract;
//import android.util.JsonReader;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ArrayList<Product> products = new ArrayList<>();
    ProductAdapter adapter;
    ArrayList<Product> productsToHide = new ArrayList<>();
    boolean sorted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityResultLauncher fileLoadLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResultCallback) result -> {
            ActivityResult res = (ActivityResult)result;
            if (res.getResultCode()  == RESULT_OK) {
                Uri fileUri = res.getData().getData();
                readTextFromUri(fileUri);

            }
        });
        ActivityResultLauncher productChangeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() {
            @Override
            public void onActivityResult(Object result) {
                ActivityResult res = (ActivityResult)result;
                if (res.getResultCode() == RESULT_OK) {
                    Bundle extras = res.getData().getExtras();
                    Product initProduct = (Product)extras.get("InitProduct");
                    Product newProduct = (Product)extras.get("NewProduct");
                    if (!initProduct.equals(newProduct)){
                        for (Product pr : products){
                            System.out.println(initProduct);
                            if (pr.equals(initProduct)){
                                pr.setName(newProduct.getName());
                                pr.setPrice(newProduct.getPrice());
                                pr.setReceiptDate(newProduct.getReceiptDate());
                                pr.setQuantity(newProduct.getQuantity());
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        });
        setContentView(R.layout.activity_main);
//        fillList(15);
        ListView productList = findViewById(R.id.productList);
        adapter = new ProductAdapter(getApplicationContext(),
                R.layout.list_item_view, products);
        productList.setAdapter(adapter);
        productList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(),  product_activity.class);
            intent.putExtra("Product",adapter.getItem(i));
            productChangeLauncher.launch(intent);
        });
        Button sortButton = findViewById(R.id.sortButton);
        sortButton.setOnClickListener(sortButtonHandler);
//        try {
//            String root = Environment.getExternalStorageDirectory().toString();
//
//            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("application/json");
//            intent.putExtra(Intent.EXTRA_TITLE, "file.json");
//            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "{firstname:\"hi\"}");
//
//            startActivityForResult(intent, 12);
//        }
//        catch (Exception e){
//            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
//            System.out.println(e);
//        }

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType( "*/*");
            fileLoadLauncher.launch(intent);

        });
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
            products.add(new Product("Product " + i, ct, randomQuantity, random));

        }
    }
    private final View.OnClickListener sortButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (sorted){
                for (Product pr : productsToHide){
                    adapter.add(pr);
                }
                productsToHide.clear();
            }
            else {
                for (int i = 0; i < adapter.getCount(); i++){
                    Product tempPr = adapter.getItem(i);
                    System.out.println(tempPr.getName());
                    Date currentTime = Calendar.getInstance().getTime();
                    long diffInms = Math.abs(currentTime.getTime() - tempPr.getReceiptDate().getTime());
                    long diff = TimeUnit.DAYS.convert(diffInms, TimeUnit.MILLISECONDS);
                    if (diff < 31){
                        productsToHide.add(tempPr);
//                    System.out.println(tempPr.getReceiptDate() + "\n" + diff + "\n");
                    }
                    else {
                        if (tempPr.getPrice() < 50){
                            productsToHide.add(tempPr);
                        }
                    }

                }
                for (int i = 0; i < productsToHide.size(); i++){
                    adapter.remove(productsToHide.get(i));
                }
                adapter.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product product, Product t1) {
                        return (product.getName()).compareTo(t1.getName());

                    }
                });
            }

            sorted = !sorted;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            readTextFromUri(fileUri);

        }

        if (requestCode == 12){
            Uri uri = data.getData();
            alterDocument(uri);

        }
        if (requestCode == 100) {
            Bundle extras = data.getExtras();
            Product initProduct = (Product)extras.get("InitProduct");
            Product newProduct = (Product)extras.get("NewProduct");
            if (!initProduct.equals(newProduct)){
                for (Product pr : products){
                    System.out.println(initProduct);
                    if (pr.equals(initProduct)){
                        pr.setName(newProduct.getName());
                        pr.setPrice(newProduct.getPrice());
                        pr.setReceiptDate(newProduct.getReceiptDate());
                        pr.setQuantity(newProduct.getQuantity());
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void alterDocument(Uri uri) {
        try {
            JSONArray ja = new JSONArray();
           for (Product pr: products){
               JSONObject jo = new JSONObject();
               jo.put("Name", pr.getName());
               jo.put("Date", pr.getReceiptDate().getTime());
               jo.put("Quantity", pr.getQuantity());
               jo.put("Price", pr.getPrice());
               ja.put(jo.toString());
           }
            ParcelFileDescriptor doc = getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(doc.getFileDescriptor());
            fileOutputStream.write((ja.toString()).getBytes());
            fileOutputStream.close();
            doc.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void readTextFromUri(Uri uri)  {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
             String line;
             while ((line = reader.readLine()) != null) {
                 stringBuilder.append(line);
             }
             JSONArray ja = new JSONArray(stringBuilder.toString());
             products.clear();
             for (int i = 0; i < ja.length(); i ++){
                JSONObject jo = new JSONObject(ja.getString(i));
                products.add(new Product(jo.getString("Name"), new Date(jo.getLong("Date")),
                        jo.getInt("Quantity"), (float)jo.getDouble("Price")));
             }
        } catch (IOException | JSONException  e) {
            e.printStackTrace();
        }
        finally {
            adapter.notifyDataSetChanged();
        }

    }
}