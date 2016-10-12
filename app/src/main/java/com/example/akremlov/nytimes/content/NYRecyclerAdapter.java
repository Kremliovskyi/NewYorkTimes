package com.example.akremlov.nytimes.content;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.activity.WebViewActivity;
import com.squareup.picasso.Picasso;
import java.util.List;


public class NYRecyclerAdapter extends RecyclerView.Adapter<NYRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<NYItem> mList;
    private OnScrollListener onScrollListener;

    public interface OnScrollListener {
        void onScrollEnd();
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public NYRecyclerAdapter(Context mContext, List<NYItem> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        String imageUrl = mList.get(position).getPhoto();

        if (position == 0 && !TextUtils.isEmpty(imageUrl)) {
            holder.mFirstItemPhoto.setVisibility(View.VISIBLE);
            holder.mItemPhoto.setVisibility(View.GONE);
            Picasso.with(mContext).load(imageUrl).resize(350, 200).centerInside().into(holder.mFirstItemPhoto);
        } else {
            if (!TextUtils.isEmpty(imageUrl)) {
                holder.mFirstItemPhoto.setVisibility(View.GONE);
                holder.mItemPhoto.setVisibility(View.VISIBLE);
                holder.mItemPhoto.setImageDrawable(null);
                Picasso.with(mContext).load(imageUrl).resize(150, 150).centerInside().into(holder.mItemPhoto);
            } else {
                holder.mItemPhoto.setVisibility(View.GONE);
                holder.mFirstItemPhoto.setVisibility(View.GONE);
            }
        }
        holder.mHeadLine.setText(mList.get(position).getHeadLine());
        holder.mSnippet.setText(mList.get(position).getSnippet());

        holder.mHeadLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri webLink = Uri.parse(mList.get(position).getWebUrl());
//                Intent intent = new Intent(Intent.ACTION_VIEW, webLink);
//                mContext.startActivity(intent);

                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("url", mList.get(holder.getAdapterPosition()).getWebUrl());
                mContext.startActivity(intent);
            }
        });

        if (position == getItemCount() - 5) {
            onScrollListener.onScrollEnd();
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mItemPhoto;
        private CardView mCardView;
        private TextView mHeadLine;
        private TextView mSnippet;
        private ImageView mFirstItemPhoto;

        private ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mItemPhoto = (ImageView) mCardView.findViewById(R.id.item_photo);
            mHeadLine = (TextView) mCardView.findViewById(R.id.headline);
            mSnippet = (TextView) mCardView.findViewById(R.id.snippet);
            mFirstItemPhoto = (ImageView) mCardView.findViewById(R.id.first_item_photo);
        }
    }
}
