package com.example.lab02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.text.DateFormat;
import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    private ArrayList<Product> productList = new ArrayList<>();

    public ProductAdapter(@NonNull Context context, int resource, ArrayList<Product> productList) {
        super(context, resource, productList);
        this.productList = productList;
    }
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//        Log.d("FOO", "item count: " + getCount());
//    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        System.out.println("YOUYOUYOYOYOYOYOYOYO");
        ;
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_view, parent,
                    false);
        }
        TextView nameView = convertView.findViewById(R.id.productNameView);
        TextView receiptDate = convertView.findViewById(R.id.productReceiptDateView);
        TextView price = convertView.findViewById(R.id.productPriceView);
        TextView quantity = convertView.findViewById(R.id.productQuantityView);

        nameView.setText(productList.get(position).getName());
        DateFormat df = DateFormat.getDateInstance();
        String strDate = df.format(productList.get(position).getReceiptDate());
        receiptDate.setText(strDate);
        String priceString = String.format("%.2f", productList.get(position).getPrice());
        price.setText(priceString + "$");

        quantity.setText(String.valueOf("Available: " + productList.get(position).getQuantity()));
        return convertView;
    }

}
