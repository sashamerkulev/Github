package ru.merkulyevsasha.github.mvp.repodetails;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.models.CommitInfo;

public class DetailsRecyclerViewAdapter extends RecyclerView.Adapter<DetailsRecyclerViewAdapter.ViewHolder>{

    public List<CommitInfo> mItems;

    public DetailsRecyclerViewAdapter(List<CommitInfo> items){
        mItems = items;
    }

    @Override
    public DetailsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_recyclerview_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailsRecyclerViewAdapter.ViewHolder holder, int position) {
        CommitInfo item = mItems.get(position);
        holder.mDate.setText(item.getCommit().getAuthor().getDate());
        holder.mAuthor.setText(item.getCommit().getAuthor().getName());
        holder.mMessage.setText(item.getCommit().getMessage());
        holder.mHash.setText(item.getSha());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mDate;
        private final TextView mHash;
        private final TextView mMessage;
        private final TextView mAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView)itemView.findViewById(R.id.textview_date);
            mHash = (TextView)itemView.findViewById(R.id.textview_hash);
            mMessage = (TextView)itemView.findViewById(R.id.textview_message);
            mAuthor = (TextView)itemView.findViewById(R.id.textview_author);

        }
    }

}
