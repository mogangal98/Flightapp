package com.mgangal.flightview.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mgangal.flightview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<String> countryList = null;
    private ArrayList<String> countryArraylist;

    private List<String> countryCodeList = null;
    private ArrayList<String> countryCodeArraylist;

    //constructor
    public ListViewAdapter(Context context, List<String> countryList,List<String> countryCodeList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);

        this.countryList = countryList;
        this.countryCodeList = countryCodeList;

        this.countryArraylist = new ArrayList<String>();
        this.countryArraylist.addAll(countryList);

        this.countryCodeArraylist = new ArrayList<String>();
        this.countryCodeArraylist.addAll(countryCodeList);


    }

    public class ViewHolder {TextView name;}

    @Override
    public int getCount() {
        return countryList.size();
    }               //Listsize

    @Override
    public Object getItem(int i) {
        return countryList.get(i);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_items, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(countryList.get(position));
        return view;
    }

    // Filter search
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        countryList.clear();
        countryCodeList.clear();
        if (charText.length() == 0) {
            countryList.addAll(countryArraylist);
            countryCodeList.addAll(countryCodeArraylist);
        } else {
            for (int i=0 ; i < countryArraylist.size();i++) {
                String wp = countryArraylist.get(i);
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    countryList.add(wp);
                    countryCodeList.add(countryCodeArraylist.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }
}
