package com.example.socialmediaapp.Fillter;

import android.widget.Filter;

import com.example.socialmediaapp.Adater.SearchUserAdapter;
import com.example.socialmediaapp.Model.User;

import java.util.ArrayList;

public class SearchUser extends Filter {
    private SearchUserAdapter adapter ;
    private ArrayList<User> list;

    public SearchUser(SearchUserAdapter adapter, ArrayList<User> list) {
        this.adapter = adapter;
        this.list = list;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        Filter.FilterResults results = new Filter.FilterResults();

        if(charSequence!=null && charSequence.length()>0){
            //Check editSearch is not null
            charSequence = charSequence.toString().toUpperCase();

            ArrayList<User> users = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                //search by name and category products
                if(list.get(i).getName().toUpperCase().contains(charSequence) ){
                   users.add(list.get(i));
                }
            }
            results.count = users.size();
            results.values = users ;
        }else{
            results.count = list.size();
            results.values = list;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
        adapter.arrayList = (ArrayList<User>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
