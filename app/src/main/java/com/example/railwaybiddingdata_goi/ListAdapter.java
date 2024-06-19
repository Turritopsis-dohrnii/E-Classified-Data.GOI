package com.example.railwaybiddingdata_goi;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter {

    private Activity mContext;
    List<Railway> bidList;
    public ListAdapter(Activity mContext, List<Railway> bidList){
        super(mContext,R.layout.list_item,bidList);
        this.mContext = mContext;
        this.bidList = bidList;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.list_item,null,true);

        TextView bidAmount= listItemView.findViewById(R.id.text_bid_amount);
        TextView auctionId = listItemView.findViewById(R.id.text_auction_id);
        TextView bidderID= listItemView.findViewById(R.id.text_bidder_id);
        TextView enterpriseName = listItemView.findViewById(R.id.text_enterprise_name);
        TextView status = listItemView.findViewById(R.id.text_sam_status);
        TextView dunsNumber = listItemView.findViewById(R.id.text_duns_number);
        TextView creationDate = listItemView.findViewById(R.id.text_creation_date);
        TextView updationDate = listItemView.findViewById(R.id.text_updation_date);




        Railway bidders = bidList.get(position);

        bidAmount.setText(bidders.getBidAmount());
        auctionId.setText(bidders.getAuctionId());
        bidderID.setText(bidders.getBidderId());
        enterpriseName.setText(bidders.getEnterpriseName());
        status .setText(bidders.getSamStatus());
        dunsNumber.setText(bidders.getDunsNumber());
        creationDate.setText(bidders.getCreationDate());
        updationDate.setText(bidders.getUpdationDate());

        return listItemView;
    }
}
