package com.example.chenzhe.eyerhyme.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chenzhe.eyerhyme.R;
import com.example.chenzhe.eyerhyme.model.TheaterCommentItem;
import com.example.chenzhe.eyerhyme.util.PostUtil;
import com.example.chenzhe.eyerhyme.util.TransferUtil;

import java.util.List;

/**
 * Created by Jay on 2016/5/6.
 */
public class TheaterCommentAdapter extends ArrayAdapter<TheaterCommentItem>  {
    private int resourceId;
    public TheaterCommentAdapter(Context context, int textViewResourceId, List<TheaterCommentItem> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        TheaterCommentItem theaterCommentItem = getItem(position);
        RelativeLayout newView;
        if(convertView == null){
            newView = new RelativeLayout(getContext());
            LayoutInflater inflater = (LayoutInflater)getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(resourceId, newView, true);
        }
        else{
            newView = (RelativeLayout)convertView;
        }
        ImageView head = (ImageView)newView.findViewById(R.id.comment_user_head);
        head.setMinimumHeight(head.getWidth());
        PostUtil.newInstance().imageGET(head, "user", theaterCommentItem.getUser_id());
        TextView name = (TextView)newView.findViewById(R.id.theater_comment_user_name);
        ImageView[] stars = new ImageView[5];
        stars[0] = (ImageView)newView.findViewById(R.id.comment_star_1);
        stars[1] = (ImageView)newView.findViewById(R.id.comment_star_2);
        stars[2] = (ImageView)newView.findViewById(R.id.comment_star_3);
        stars[3] = (ImageView)newView.findViewById(R.id.comment_star_4);
        stars[4] = (ImageView)newView.findViewById(R.id.comment_star_5);
        TextView content = (TextView)newView.findViewById(R.id.theater_comment_content);
        TextView time = (TextView)newView.findViewById(R.id.theater_comment_time);
        name.setText(theaterCommentItem.getName());
        int num = (int)theaterCommentItem.getGrade()/2, i;
        Log.e("num", num+"");
        for (i = 0; i < num; i++)
            stars[i].setImageResource(R.mipmap.star);
        for ( ; i < 5; i++)
            stars[i].setImageResource(R.mipmap.unstar);
        content.setText(theaterCommentItem.getContent());
        time.setText("发表于: "+ TransferUtil.transferDate(theaterCommentItem.getDate_time().substring(0, theaterCommentItem.getDate_time().length()-2)));
        return newView;
    }
}
