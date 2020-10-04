package it.orsaferrovie.orsaferrovieapp;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Fabrizio on 28-mag-15.
 */
public class MenuAdapter extends ArrayAdapter<String>{
    private final Context mContext;
    private final String[] values;


    public MenuAdapter(Context context, String[] values) {
        super(context, -1, values);
        mContext = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = values[position];
        ViewHolder vh;
        if (convertView == null) {
            convertView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.manu_item, parent, false);
            vh = new ViewHolder();
            vh.testo = (TextView)convertView.findViewById(R.id.txtTestoMenu);
            vh.immagine = (ImageView)convertView.findViewById(R.id.img_menu_item);

            convertView.setTag(vh);
        }
        else
            vh = (ViewHolder)convertView.getTag();
        if (item != null) {
            vh.testo.setText(item);
            switch (position) {
                case 0:
                    vh.immagine.setImageResource(R.drawable.news);
                    break;
                case 1:
                    vh.immagine.setImageResource(R.drawable.contatti);
                    break;
                case 2:
                    vh.immagine.setImageResource(R.drawable.facebook);
                    break;
                case 3:
                    vh.immagine.setImageResource(R.drawable.settings);
                    break;
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView immagine;
        TextView testo;
    }
}
