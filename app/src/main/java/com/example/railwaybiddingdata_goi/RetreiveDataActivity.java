package com.example.railwaybiddingdata_goi;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RetreiveDataActivity extends InsertingDataActivity {

    ListView myListview;
    List<Railway> bidList;
    DatabaseReference railwayDbRef;

    static String cdate;
    static String udate;
    private KeyDatabaseHelper keyDatabaseHelper;
    private int mYear, mMonth, mDay;

    static int counter = 0;

    static byte[] key_current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retreive_data);
        keyDatabaseHelper = new KeyDatabaseHelper(this);

        myListview = findViewById(R.id.myListView);
        bidList = new ArrayList<>();
        railwayDbRef = FirebaseDatabase.getInstance().getReference("Railways");

        railwayDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bidList.clear();

                for (DataSnapshot railwayDatasnap : dataSnapshot.getChildren()) {
                    Railway bidders = railwayDatasnap.getValue(Railway.class);
                    String id = bidders != null ? bidders.id : null;

                    if (id != null) {
                        byte[] key = keyDatabaseHelper.getKey(id);
                        if (key != null) {
                            // Decrypt encrypted fields retrieved from Firebase
                            try {
                                if (bidders != null) {
                                    bidders.bidAmount = SecurityKyber.decrypt(bidders.bidAmount, key);
                                    bidders.auctionId = SecurityKyber.decrypt(bidders.auctionId, key);
                                    bidders.bidderId = SecurityKyber.decrypt(bidders.bidderId, key);
                                    bidders.enterpriseName = SecurityKyber.decrypt(bidders.enterpriseName, key);
                                    bidders.samStatus = SecurityKyber.decrypt(bidders.samStatus, key);
                                    bidders.dunsNumber = SecurityKyber.decrypt(bidders.dunsNumber, key);
                                    bidders.creationDate = SecurityKyber.decrypt(bidders.creationDate, key);
                                    bidders.updationDate = SecurityKyber.decrypt(bidders.updationDate, key);
                                }
                            } catch (Exception e) {
                                e.printStackTrace(); // Print the stack trace to debug any decryption errors
                                // Handle the decryption error here, if necessary
                            }
                        }
                    }

                    bidList.add(bidders);
                }

                ListAdapter adapter = new ListAdapter(RetreiveDataActivity.this, bidList);
                myListview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });

        // Set itemLongClickListener on listview item
        myListview.setOnItemLongClickListener((parent, view, position, id) -> {
            Railway bidders = bidList.get(position);
            showUpdateDialog(bidders.id, bidders.enterpriseName);

            return false;
        });
    }

    private void showUpdateDialog(final String id, String name) {
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mDialogView = inflater.inflate(R.layout.update_dialog, null);

        mDialog.setView(mDialogView);

        final EditText bidAmount = mDialogView.findViewById(R.id.bidAmount2);
        final EditText auctionId = mDialogView.findViewById(R.id.auctionId2);
        final EditText bidderID = mDialogView.findViewById(R.id.bidderId2);
        final EditText enterpriseName = mDialogView.findViewById(R.id.enterpriseName2);
        final Spinner status = mDialogView.findViewById(R.id.verification_status2);
        final EditText dunsNumber = mDialogView.findViewById(R.id.dunsNumber2);
        final TextView creationDate = mDialogView.findViewById(R.id.creationDate2);
        final TextView updationDate = mDialogView.findViewById(R.id.updationDate2);

        Button btnUpdate = mDialogView.findViewById(R.id.btnUpdate);
        Button btnDelete = mDialogView.findViewById(R.id.btnDelete);

        Button create_date = mDialogView.findViewById(R.id.cd2);
        Button update_date = mDialogView.findViewById(R.id.ud2);

        create_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (datePickerView, year, month, dayOfMonth) -> {
                        creationDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        cdate = dayOfMonth + "-" + (month + 1) + "-" + year;
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        update_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (datePickerView, year, month, dayOfMonth) -> {
                        updationDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        udate = dayOfMonth + "-" + (month + 1) + "-" + year;
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        mDialog.setTitle("Updating " + name + " record");

        final AlertDialog alertDialog = mDialog.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(v -> {
            try {
                String bamt = bidAmount.getText().toString();
                String aucId = auctionId.getText().toString();
                String biId = bidderID.getText().toString();
                String eName = enterpriseName.getText().toString();
                String stat = status.getSelectedItem().toString();
                String dNum = dunsNumber.getText().toString();

                // Retrieve the current key for this record
                byte[] key = keyDatabaseHelper.getKey(id);
                if (key != null) {
                    String e_bidAmt = SecurityKyber.encrypt(bamt, key);
                    String e_aucId = SecurityKyber.encrypt(aucId, key);
                    String e_bidId = SecurityKyber.encrypt(biId, key);
                    String e_enterprise = SecurityKyber.encrypt(eName, key);
                    String e_sstatus = SecurityKyber.encrypt(stat, key);
                    String e_dnumber = SecurityKyber.encrypt(dNum, key);
                    String e_cdate = SecurityKyber.encrypt(cdate != null ? cdate : creationDate.getText().toString(), key);
                    String e_udate = SecurityKyber.encrypt(udate != null ? udate : updationDate.getText().toString(), key);

                    updateData(id, e_bidAmt, e_aucId, e_bidId, e_enterprise, e_sstatus, e_dnumber, e_cdate, e_udate);

                    Toast.makeText(RetreiveDataActivity.this, "Record Updated", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(RetreiveDataActivity.this, "Key not found for this record", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        btnDelete.setOnClickListener(v -> {
            deleteRecord(id);
            alertDialog.dismiss();
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteRecord(String id) {
        // Create reference to database
        DatabaseReference railwayDbRef = FirebaseDatabase.getInstance().getReference("Railways").child(id);
        // We are referencing child here because we will be deleting one record, not the whole data in the database
        Task<Void> mTask = railwayDbRef.removeValue();
        mTask.addOnSuccessListener(aVoid -> showToast("Deleted"))
                .addOnFailureListener(e -> showToast("Error deleting record"));
    }

    private void updateData(String id, String bidAmount, String auctionId, String bidderId, String enterpriseName, String samStatus, String dunsNumber, String creationDate, String updationDate) {
        // Creating database reference
        DatabaseReference railwayDbRef = FirebaseDatabase.getInstance().getReference("Railways").child(id);
        Railway bidders = new Railway(id, bidAmount, auctionId, bidderId, enterpriseName, samStatus, dunsNumber, creationDate, updationDate);
        railwayDbRef.setValue(bidders);
    }
}
