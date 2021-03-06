package com.example.android.ffxivhelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.ffxivhelper.data.CollectiblesContract.MountEntry;

public class CollectibleCursorAdapter extends CursorAdapter {

    public CollectibleCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View newView = inflater.inflate(R.layout.collectible_item,parent,false);
        return newView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameView = view.findViewById(R.id.tv_collectible_name);
        TextView idView = view.findViewById(R.id.tv_collectible_id);

        long id = cursor.getLong(cursor.getColumnIndex(MountEntry._ID));
        //TODO: Handle ids greater than db size
        String name = cursor.getString(cursor.getColumnIndex(MountEntry.COLUMN_NAME));

        nameView.setText(name);
        idView.setText(String.valueOf(id));
    }
}
