package com.example.lab03;

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

public class SongAdapter extends ArrayAdapter<MediaSongVid> {
    private ArrayList<MediaSongVid> mediaList;
    public SongAdapter(Context context, int resource, ArrayList<MediaSongVid> mediaList){
        super(context, resource, mediaList);
        this.mediaList = mediaList;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_view, parent,
                    false);
        }
        TextView nameView = convertView.findViewById(R.id.mediaName);
        TextView authorView = convertView.findViewById(R.id.mediaAuthor);
        TextView formatView = convertView.findViewById(R.id.mediaFormat);

        nameView.setText(mediaList.get(position).getName());
        authorView.setText(mediaList.get(position).getAuthor());
        formatView.setText(mediaList.get(position).getFormat());
        return convertView;
    }
}
