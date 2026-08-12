package com.ouropro.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ouropro.player.R;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SortSpinnerAdapter extends BaseAdapter {
    public Context context;
    public List<String> names;

    public SortSpinnerAdapter(Context context, List<String> list) {
        this.context = context;
        this.names = list;
    }

    public int getCount() {
        List<String> list = this.names;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public Object getItem(int i) {
        return this.names.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewInflate = LayoutInflater.from(this.context).inflate(R.layout.item_sort, viewGroup, false);
        ((TextView) viewInflate.findViewById(R.id.txt_name)).setText(this.names.get(i));
        return viewInflate;
    }
}
