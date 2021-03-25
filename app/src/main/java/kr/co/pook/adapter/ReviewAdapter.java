package kr.co.pook.adapter;

import android.content.Context;
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
import kr.co.pook.vo.Pook_review;
import kr.co.pook.vo.Pook_store;

public class ReviewAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<Pook_review> m_oData = null;
    private int nListCnt = 0;

    public ReviewAdapter(ArrayList<Pook_review> _oData)
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
            convertView = inflater.inflate(R.layout.review_item, parent, false);
        }

        TextView review_item_nickname = (TextView) convertView.findViewById(R.id.review_item_nickname);
        RatingBar review_item_score = (RatingBar) convertView.findViewById(R.id.review_item_score);
        TextView review_item_review = (TextView) convertView.findViewById(R.id.review_item_review);

        review_item_nickname.setText(m_oData.get(position).getNickname());
        review_item_score.setRating(Float.parseFloat(m_oData.get(position).getScore()));
        review_item_review.setText(m_oData.get(position).getReview());
        return convertView;
    }

    public void addItem(ArrayList<Pook_review> pook_review){
        m_oData.addAll(pook_review);
        nListCnt = m_oData.size();
    }
}
