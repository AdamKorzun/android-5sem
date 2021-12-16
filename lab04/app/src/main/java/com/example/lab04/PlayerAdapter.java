package com.example.lab04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends ArrayAdapter<TableEntry> {
    private ArrayList<TableEntry> players;

    public PlayerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TableEntry> objects) {
        super(context, resource, objects);
        this.players = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent,
                    false);
        }
        TextView indexView = convertView.findViewById(R.id.indexView);

        TextView usernameView = convertView.findViewById(R.id.usernameView);
        TextView nameView = convertView.findViewById(R.id.nameView);
        TextView scoreView = convertView.findViewById(R.id.scoreView);
        TextView timeView = convertView.findViewById(R.id.timeView);

        indexView.setText(String.valueOf(position));
        usernameView.setText(players.get(position).getUsername());
        nameView.setText(players.get(position).getName());
        scoreView.setText(String.valueOf(players.get(position).getScore()));
        timeView.setText(String.valueOf(players.get(position).getPlayingTime()));
        return convertView;
    }
}
