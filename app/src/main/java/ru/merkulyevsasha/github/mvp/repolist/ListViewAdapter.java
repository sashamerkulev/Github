package ru.merkulyevsasha.github.mvp.repolist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.models.Repo;


public class ListViewAdapter extends ArrayAdapter<Repo> {

    private final Context mContext;
    private final List<Repo> mItems;
    private final LayoutInflater mInflater;

    public ListViewAdapter(Context context, List<Repo> items) {
        super(context, R.layout.main_listview_item, items);

        mItems = items;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void addAll(@NonNull Collection<? extends Repo> collection) {
        super.addAll(collection);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.main_listview_item, parent, false);
            convertView.setTag(convertView.findViewById(R.id.textview_fullname));
        }
        TextView textViewTopic = (TextView) convertView.getTag();

        Repo item = mItems.get(position);

        textViewTopic.setText(item.getFullName());

        return convertView;
    }


}
