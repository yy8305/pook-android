package kr.co.pook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.pook.R;
import kr.co.pook.vo.Pook_reserve;
import kr.co.pook.vo.Pook_review;
import kr.co.pook.vo.Pook_time;

public class TimeAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<Pook_time> m_oData = null;
    private int nListCnt = 0;

    public TimeAdapter(ArrayList<Pook_time> _oData)
    {
        m_oData = _oData;
        if(m_oData != null){
            nListCnt = m_oData.size();
        }
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
            convertView = inflater.inflate(R.layout.time_item, parent, false);
        }

        TextView time_item_name = (TextView) convertView.findViewById(R.id.time_item_name);
        TextView time_item_date = (TextView) convertView.findViewById(R.id.time_item_date);
        TextView time_item_time = (TextView) convertView.findViewById(R.id.time_item_time);
        TextView time_item_people = (TextView) convertView.findViewById(R.id.time_item_people);

        time_item_name.setText(m_oData.get(position).getStore_name());
        time_item_date.setText(m_oData.get(position).getReserve_date());
        time_item_time.setText(m_oData.get(position).getReserve_time());
        time_item_people.setText(m_oData.get(position).getReserve_people());
        return convertView;
    }

    public void addItem(ArrayList<Pook_time> pook_times){
        m_oData.addAll(pook_times);
        nListCnt = m_oData.size();
    }
}
