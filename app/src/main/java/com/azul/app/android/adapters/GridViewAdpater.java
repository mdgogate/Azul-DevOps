package com.azul.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.azul.app.android.R;


public class GridViewAdpater extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater;
    private final String[] numberWord;
    private final int[] transportImage;

    public GridViewAdpater(Context c, String[] numberWord, int[] transportImage){
        context = c;
        this.numberWord = numberWord;
        this.transportImage = transportImage;
    }

    @Override
    public int getCount() {
        return numberWord.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null){
            convertView = inflater.inflate(R.layout.activity_grid_view_adpater, null);
        }

        ImageView imageView = convertView.findViewById(R.id.image_view);
        TextView textView = convertView.findViewById(R.id.text_view);

        imageView.setImageResource(transportImage[position]);
        textView.setText(numberWord[position]);

        return convertView;
    }
}
