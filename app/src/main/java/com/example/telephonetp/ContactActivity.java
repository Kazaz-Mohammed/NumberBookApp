package com.example.telephonetp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.Manifest;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

//import javax.security.auth.callback.Callback;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;




public class ContactActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final String TAG = "CONTACTS_APP";


    private ListView lvContacts;
//    private ArrayList<String> contactList = new ArrayList<>();
//    private ArrayAdapter<String> adapter;


//    ****************
    private List<Contact> contactList = new ArrayList<>();
    private ContactAdapter adapter;


//    *********************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        lvContacts = findViewById(R.id.lvContacts);
        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList);
        lvContacts.setAdapter(adapter);

        // Check and request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getNumbers(getContentResolver()); // This should populate contactList and notify adapter
        }

        Button btnSendContacts = findViewById(R.id.btnSendContacts);
        btnSendContacts.setOnClickListener(v -> {
            for (Contact contact : contactList) {
                try {
                    Log.d(TAG, "Sending " + contact.getName() + " - " + contact.getNumber());
                    sendContactToBackend(contact);
                } catch (Exception e) {
                    Log.e(TAG, "Error sending contact: " + contact.getName() + " | " + e.getMessage());
                }
            }
        });

        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
//
//            Contact contact = contactList.get(position);
//            String name = contact.getName();
//            String number = contact.getNumber();

//            ************
            // Check if there are any items in the adapter
            if (adapter.getCount() > 0) {
                Contact contact = adapter.getItem(position);  // Use adapter.getItem instead of contactList.get
                String name = contact.getName();
                String number = contact.getNumber();

//            *************
                new android.app.AlertDialog.Builder(ContactActivity.this)
                        .setTitle("Choose Action")
                        .setMessage("Do you want to call or send a message to " + name + "?")
                        .setPositiveButton("Call", (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + number));
                            startActivity(intent);
                        })
                        .setNegativeButton("Send Message", (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("sms:" + number));
                            startActivity(intent);
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterContacts(newText);
//                return true;
//            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText); // Use the adapter's filter
                return true;
            }

        });
    }


    private void filterContacts(String text) {
        List<Contact> filtered = new ArrayList<>();
        for (Contact c : contactList) {
            if (c.getName().toLowerCase().contains(text.toLowerCase()) ||
                    c.getNumber().contains(text)) {
                filtered.add(c);
            }
        }
        adapter.clear();
        adapter.addAll(filtered);
        adapter.notifyDataSetChanged();
    }


//    ******************

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getNumbers(getContentResolver());
            } else {
                Log.e(TAG, "Permission denied to read your contacts");
            }
        }
    }

    public void getNumbers(ContentResolver cr) {
        Cursor phones = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (phones != null) {
            contactList.clear(); // Clear existing contacts before adding new ones

            while (phones.moveToNext()) {
                String name = phones.getString(
                        phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String number = phones.getString(
                        phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Contact contact = new Contact(name, number);
                contactList.add(contact);

                Log.d(TAG, "Name: " + name + ", Number: " + number);
            }

            phones.close();
            adapter.updateOriginalList(contactList); // After adding all contacts, update the adapter lists
        } else {
            Log.d(TAG, "No contacts found");
        }
    }


//    ****************************

    // Method to send contact to the backend using Retrofit
    private void sendContactToBackend(Contact contact) {
        // Base URL for your server (change it to your actual backend URL)
        String baseUrl = "http://192.168.1.7/contactprojet/";  // Example IP



        ContactApi apiService = ApiClient.getRetrofit(baseUrl).create(ContactApi.class);
        Call<Void> call = apiService.insertContact(contact);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Contact sent successfully");
                } else {
                    Log.e(TAG, "Failed to send contact: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error sending contact: " + t.getMessage());
            }
        });


    }




}
