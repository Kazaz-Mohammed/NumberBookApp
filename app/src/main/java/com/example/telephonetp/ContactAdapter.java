package com.example.telephonetp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> implements Filterable {
    private Context context;
    private List<Contact> contactList;

    private List<Contact> originalList;
    private List<Contact> filteredList;


//    public ContactAdapter(Context context, List<Contact> contacts) {
//        super(context, 0, contacts);
//        this.context = context;
//        this.contactList = contacts;
//    }

//    public ContactAdapter(Context context, List<Contact> contacts) {
//        super(context, 0, contacts);
//        this.context = context;
//        this.originalList = new ArrayList<>(contacts); // original backup
//        this.filteredList = contacts; // filtered list used for display
//    }

    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
        this.context = context;
        this.originalList = new ArrayList<>(contacts); // original backup
        this.filteredList = contacts; // filtered list used for display
    }

    public void updateOriginalList(List<Contact> newContacts) {
        this.originalList = new ArrayList<>(newContacts); // Make a copy
        this.filteredList = new ArrayList<>(newContacts);
        clear();
        addAll(newContacts);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
//        return filteredList.size();
        return filteredList != null ? filteredList.size() : 0; // Add null check
    }

    @Override
    public Contact getItem(int position) {
//        return filteredList.get(position);
        if (filteredList != null && position < filteredList.size()) {
            return filteredList.get(position);
        }
        return null;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        }

        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtNumber = convertView.findViewById(R.id.txtNumber);
        ImageView imgIcon = convertView.findViewById(R.id.imgIcon);

        txtName.setText(contact.getName());
        txtNumber.setText(contact.getNumber());

        // Optional: set SVG dynamically if needed
        imgIcon.setImageResource(R.drawable.ic_contact_svg); // put your icon in res/drawable

        return convertView;
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Contact> filteredResults = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    // No filter
                    filteredResults.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Contact contact : originalList) {
                        if (contact.getName().toLowerCase().contains(filterPattern) ||
                                contact.getNumber().contains(filterPattern)) {
                            filteredResults.add(contact);
                        }
                    }
                }

                results.values = filteredResults;
                results.count = filteredResults.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<Contact>) results.values);
                notifyDataSetChanged();
            }
        };
    }

}