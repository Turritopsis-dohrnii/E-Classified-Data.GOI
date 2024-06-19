package com.example.railwaybiddingdata_goi;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.app.DatePickerDialog;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Calendar;


public class InsertingDataActivity extends AppCompatActivity {

    EditText bidAmount;
    EditText auctionId;
    EditText bidderID;
    EditText enterpriseName;
    Spinner status;

    EditText dunsNumber;
    TextView creationDate;
    TextView updationDate;
    Button insert;
    static String cdate;
    static String udate;

    private int mYear, mMonth, mDay;
    DatabaseReference railwayDbRef;
    private KeyDatabaseHelper keyDatabaseHelper;
    public  static byte[] key;

    static ArrayList<byte[]> keyListPerma= new ArrayList<>();


    public SecurityKyber ob1= new SecurityKyber();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserting_data);


        bidAmount= findViewById(R.id.bidAmount);
        auctionId = findViewById(R.id.auctionId);
        bidderID= findViewById(R.id.bidderId);
        enterpriseName = findViewById(R.id.enterpriseName);
        status = findViewById(R.id.verification_status);
        dunsNumber = findViewById(R.id.dunsNumber);
        creationDate = findViewById(R.id.creationDate);
        updationDate = findViewById(R.id.updationDate);
        insert = findViewById(R.id.btnInsert);
        railwayDbRef = FirebaseDatabase.getInstance().getReference("Railways");
        keyDatabaseHelper = new KeyDatabaseHelper(this);

        try {
            // Generate key pair
            Security.addProvider(new BouncyCastleProvider());
            Security.addProvider(new BouncyCastlePQCProvider());
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SecurityKyber.KEM_ALGORITHM, SecurityKyber.PROVIDER);

            keyPairGenerator.initialize(SecurityKyber.KEM_PARAMETER_SPEC, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Generate encapsulation using the public key
            SecurityKyber.encapsulation = ob1.generateSecretKeySender(keyPair.getPublic());
        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                 InvalidAlgorithmParameterException e) {
            e.printStackTrace();

        }


    }
    public void insert_button(View view) {
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
    }

    public void update_button(View view) {
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
    }

    public void insertData (View view)
    {
        insertRailwayData(cdate, udate);
    }

    private void insertRailwayData(String cdate, String udate) {


        // Get values from EditText and Spinner fields
        String bidAmt = bidAmount.getText().toString().trim();
        String aucId = auctionId.getText().toString().trim();
        String bidId = bidderID.getText().toString().trim();
        String enterprise = enterpriseName.getText().toString().trim();
        String sstatus = status.getSelectedItem().toString();
        String dnumber = dunsNumber.getText().toString().trim();

        try {
            key = ob1.encapsulation.getEncoded();
            String id = railwayDbRef.push().getKey();

            // Save key to SQLite
            if (id != null) {
                keyDatabaseHelper.insertKey(id, key);
            }

            // Encrypt data
            String e_bidAmt = SecurityKyber.encrypt(bidAmt, key);
            String e_aucId = SecurityKyber.encrypt(aucId, key);
            String e_bidId = SecurityKyber.encrypt(bidId, key);
            String e_enterprise = SecurityKyber.encrypt(enterprise, key);
            String e_sstatus = SecurityKyber.encrypt(sstatus, key);
            String e_dnumber = SecurityKyber.encrypt(dnumber, key);
            String e_cdate = SecurityKyber.encrypt(cdate, key);
            String e_udate = SecurityKyber.encrypt(udate, key);

            // Check if any of the fields are empty
            if (bidAmt.isEmpty() || aucId.isEmpty() || bidId.isEmpty() ||
                    enterprise.isEmpty() || dnumber.isEmpty() || cdate.isEmpty() || udate.isEmpty()) {
                Toast.makeText(InsertingDataActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Railway railway = new Railway(id, e_bidAmt, e_aucId, e_bidId, e_enterprise, e_sstatus, e_dnumber, e_cdate, e_udate);

            assert id != null;
            railwayDbRef.child(id).setValue(railway);
            Toast.makeText(InsertingDataActivity.this,"Data inserted!",Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}