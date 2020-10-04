package it.orsaferrovie.orsaferrovieapp;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Fabrizio on 28-mag-15.
 */
public class NewsAdapter extends ArrayAdapter<Notizia> {
    private Context mContext;
    private ArrayList<Notizia> values;

    public NewsAdapter(Context context, ArrayList<Notizia> values) {
        super(context, -1, values);
        mContext = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        Notizia item = values.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.item_notizia, parent, false);
            vh.imgNotizia = (ImageView)convertView.findViewById(R.id.imgNotizia);
            vh.txtData = (TextView)convertView.findViewById(R.id.txtData);
            vh.txtTesto = (TextView)convertView.findViewById(R.id.txtTesto);
            convertView.setTag(vh);
        }
        else
            vh = (ViewHolder)convertView.getTag();
        if (item!= null) {
            Picasso.with(getContext()).load(item.get_immagine()).into(vh.imgNotizia);
            vh.txtData.setText(item.get_data());
            vh.txtTesto.setText(item.get_testo());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imgNotizia;
        TextView txtData, txtTesto;
    }
}
