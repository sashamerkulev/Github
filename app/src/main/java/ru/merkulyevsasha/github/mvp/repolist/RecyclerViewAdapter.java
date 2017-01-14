package ru.merkulyevsasha.github.mvp.repolist;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.models.Repo;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    public List<Repo> mItems;
    private final MvpListView mView;

    public RecyclerViewAdapter(MvpListView view, List<Repo> items){
        mItems = items;
        mView = view;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview_item, parent, false);

        return new ViewHolder(view, new OnClickListener() {
            @Override
            public void onItemClick(int position) {
                mView.showDetails(mItems.get(position));
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        Repo item = mItems.get(position);
        holder.mRepoName.setText(item.getFullName());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mRepoName;

        public ViewHolder(View itemView, final OnClickListener clickListener) {
            super(itemView);
            mRepoName = (TextView)itemView.findViewById(R.id.tv_repo_full_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

}
