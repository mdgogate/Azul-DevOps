package com.sdp.appazul.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdp.appazul.R;


public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private final String[] numberWord;
    String rotateImage;
    private final int[] transportImage;
    int hideShow;

    public GridViewAdapter(Context c, String[] numberWord, int[] transportImage) {
        context = c;
        this.numberWord = numberWord;
        this.transportImage = transportImage;
    }

    public void updateUi(Context c, String rotateImage, int hideShow) {
        context = c;
        this.rotateImage = rotateImage;
        this.hideShow = hideShow;
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_grid_view_adpater, null);
        }

        ImageView imageView = convertView.findViewById(R.id.image_view);
        ImageView menuDownArrow = convertView.findViewById(R.id.menuDownArrow);
        TextView textView = convertView.findViewById(R.id.text_view);

        if (numberWord[position].equalsIgnoreCase(context.getResources().getString(R.string.consultant))) {
            menuDownArrow.setImageResource(R.drawable.down_arrow_svg_fmt);
        } else if (numberWord[position].equalsIgnoreCase(context.getResources().getString(R.string.linkPage))) {
            menuDownArrow.setImageResource(R.drawable.down_arrow_svg_fmt);
        } else {
            menuDownArrow.setImageResource(R.drawable.right_arrow_svg_fmt);
        }
        checkEventForConsult(position, menuDownArrow);
        checkEventForLinkDePago(position, menuDownArrow);
        imageView.setImageResource(transportImage[position]);
        textView.setText(numberWord[position]);


        return convertView;
    }

    private void checkEventForConsult(int position, ImageView menuDownArrow) {


        if (rotateImage != null && !TextUtils.isEmpty(rotateImage) && rotateImage.equalsIgnoreCase(numberWord[position])) {
            if (hideShow == 1) {
                menuDownArrow.setImageResource(R.drawable.up_arrow_svg_fmt);
            } else {
                menuDownArrow.setImageResource(R.drawable.down_arrow_svg_fmt);
            }
            notifyDataSetChanged();
        }
    }

    private void checkEventForLinkDePago(int position, ImageView menuDownArrow) {


        if (rotateImage != null && !TextUtils.isEmpty(rotateImage) && rotateImage.equalsIgnoreCase(numberWord[position])) {
            if (hideShow == 1) {
                menuDownArrow.setImageResource(R.drawable.up_arrow_svg_fmt);
            } else {
                menuDownArrow.setImageResource(R.drawable.down_arrow_svg_fmt);
            }
            notifyDataSetChanged();
        }
    }


}
