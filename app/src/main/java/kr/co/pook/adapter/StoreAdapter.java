package kr.co.pook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.co.pook.R;
import kr.co.pook.interfaces.Pook;
import kr.co.pook.vo.Pook_store;

public class StoreAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<Pook_store> m_oData = null;
    private int nListCnt = 0;

    public StoreAdapter(ArrayList<Pook_store> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return this.m_oData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.store_item, parent, false);
        }

        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView store_name = (TextView) convertView.findViewById(R.id.store_name);
        TextView store_category = (TextView) convertView.findViewById(R.id.store_category);
        TextView store_addr = (TextView) convertView.findViewById(R.id.store_addr);
        TextView store_score = (TextView) convertView.findViewById(R.id.store_score);
        RatingBar store_rating = (RatingBar) convertView.findViewById(R.id.store_rating);

        Picasso.get().load(Pook.URL+m_oData.get(position).getThumbnail_path()).into(thumbnail);
        store_name.setText(m_oData.get(position).getName());
        store_category.setText("음식종류: "+m_oData.get(position).getCategory());
        store_addr.setText("주소: "+m_oData.get(position).getAddr());
        store_score.setText("5 / "+ m_oData.get(position).getScore_avg());
        store_rating.setRating(Float.parseFloat(m_oData.get(position).getScore_avg()));
        return convertView;
    }

    public void addItem(ArrayList<Pook_store> pook_store){
        m_oData.addAll(pook_store);
        nListCnt = m_oData.size();
    }
}
