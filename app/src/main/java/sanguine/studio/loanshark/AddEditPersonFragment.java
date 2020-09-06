package sanguine.studio.loanshark;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.provider.Settings;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */


//TODO: switch to List<PhoneNumber> instead of getting info from TextViews

public class AddEditPersonFragment extends DialogFragment {

    private static final int REQUEST_CODE = 777;
    EditText fnameEditText, lnameEditText;
    Button addButton, addFromContactsButton, addNewPhoneNumberButton, cancelButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    RatingBar ratingBar;
    boolean isNewRecord = true;
    PersonRecord record;
    PhoneNumberRecyclerViewAdapter adapter;
    Context mContext;

    Util.PermissionAskListener permissionAskListener;

    public void setRecord(PersonRecord record){
        this.record = record;
        isNewRecord = false;
    }

    private Interfaces.INewPersonListener newPersonListener;

    public void setNewPersonListener(Interfaces.INewPersonListener newPersonListener) {
        this.newPersonListener = newPersonListener;
    }

    public AddEditPersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_person, container, false);
        fnameEditText = view.findViewById(R.id.addPer_fnameEditText);
        lnameEditText = view.findViewById(R.id.addPer_lnameEditText);
        addNewPhoneNumberButton = view.findViewById(R.id.addPer_addNewPhoneNumberButton);
        cancelButton = view.findViewById(R.id.addPer_cancelButton);
        addButton = view.findViewById(R.id.addPer_addNewButton);
        ratingBar = view.findViewById(R.id.ratingBar);
        addFromContactsButton = view.findViewById(R.id.addPer_addFromContactsButton);
        recyclerView = view.findViewById(R.id.addPer_recyclerView);
        backButton = view.findViewById(R.id.addPer_BackImageButton);

        addButton.setOnClickListener(view1 -> updateRecord());
        backButton.setOnClickListener(view1 -> dismiss());
        cancelButton.setOnClickListener(view1 -> dismiss());
        mContext = getContext();

        permissionAskListener = new Util.PermissionAskListener() {
            @Override
            public void onPermissionAsk() {
                requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE
                );

            }
            @Override
            public void onPermissionPreviouslyDenied() {
                //show a dialog explaining permission and then request permission
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Permission Required");
                dialog.setMessage("This app needs READ CONTACTS permission to pick a contact. Grant permission now?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Grant Permission", ((dialogInterface, i) -> {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_CONTACTS},
                            REQUEST_CODE
                    );
                }));
                dialog.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()));
                dialog.show();
            }
            @Override
            public void onPermissionDisabled() {
                AlertDialog.Builder permissionDialog = new AlertDialog.Builder(mContext);
                permissionDialog.setTitle("Permission Denied");
                permissionDialog.setMessage("You need to grant READ CONTACTS permission in the app settings to be able to pick a contact. Open the app settings now?");
                permissionDialog.setCancelable(true);
                permissionDialog.setPositiveButton("Open Settings", ((dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }));
                permissionDialog.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()));
                permissionDialog.show();
            }
            @Override
            public void onPermissionGranted() {
                readContacts();
            }
        };

        addNewPhoneNumberButton.setOnClickListener(view1 -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("New number");
            dialog.setMessage("Enter phone number:");
            dialog.setCancelable(true);

            final EditText phoneNumberText = new EditText(mContext);
            phoneNumberText.setInputType(InputType.TYPE_CLASS_PHONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            phoneNumberText.setLayoutParams(params);
            dialog.setView(phoneNumberText);

            dialog.setPositiveButton("Add", (dialogInterface, i) -> {
                String s = phoneNumberText.getText().toString();
                record.phoneNumbers.add(s);
                adapter.notifyDataSetChanged();
            });

            dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
            dialog.show();

        });

        addFromContactsButton.setOnClickListener(view1 -> {
            Util.checkPermission(mContext, Manifest.permission.READ_CONTACTS, permissionAskListener);
        });

        loadRecord();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int RC, String[] per, int[] PResult) {
        super.onRequestPermissionsResult(RC, per, PResult);

        switch (RC) {
            case REQUEST_CODE:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext,"Permission Granted", Toast.LENGTH_LONG).show();
                    readContacts();
                } else {
                    Toast.makeText(mContext,"Permission Canceled, you need to grant it to import any contacts.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    void readContacts(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //get contact name and phone number and plug it into the text fields
            Uri uri = intent.getData();
            Cursor nameCursor, phoneNumberCursor;
            String nameHolder = "", contactIdHolder = "", idResult = "";
            try {
                //get display name
                nameCursor = mContext.getContentResolver().query(uri, null, null, null, null);
                if(nameCursor.moveToFirst()) {
                    nameHolder = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //check if contact has a phone number and construct a query
                    contactIdHolder = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    idResult = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); //if has phone number then it will be "1"
                    int hasPhoneNumber = Integer.valueOf(idResult); //cast
                    ArrayList<String> tempList = new ArrayList<>();

                    if (hasPhoneNumber == 1) { //if has phone number then proceed
                        //construct query
                        phoneNumberCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactIdHolder,
                                null,
                                null);
                        record.phoneNumbers.clear();
                        while (phoneNumberCursor.moveToNext()) {
                            String s = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            s = s.replaceAll("[^\\d]","");
                            tempList.add(s);
                        }
                        for(String s : tempList){
                            if(!record.phoneNumbers.contains(s)){
                                record.phoneNumbers.add(s);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e){
                Log.e("onActivityResult", "AddEditPersonFragment.onActivityResult");
                e.printStackTrace();
            } finally {
                if (!nameHolder.isEmpty() && !record.phoneNumbers.isEmpty()) {
                    String[] names = nameHolder.split(" ");
                    String lname = "";
                    fnameEditText.setText(names[0]);
                    lnameEditText.setText("");
                    if(names.length > 1) {
                        for (int i = 1; i < names.length; i++) {
                            lname += names[i] + " ";
                        }
                        lname = lname.trim();
                        lnameEditText.setText(lname);
                    }
                } else {
                    Toast.makeText(mContext, "Could not pick contact because name or phone number is missing", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void loadRecord(){
        if(!isNewRecord) {
            fnameEditText.setText(record.getFirstName());
            lnameEditText.setText(record.getLastName());
            ratingBar.setRating(record.getPer_risk_level());
        }
        else
            record = new PersonRecord.PersonRecordBuilder()
                    .setPhoneNumbers(new ArrayList<>())
                    .createPersonRecord();

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new PhoneNumberRecyclerViewAdapter(record.phoneNumbers, mContext);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    Drawable background = new ColorDrawable(getResources().getColor(R.color.colorRed, null));

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final RecyclerView.ViewHolder mViewHolder = viewHolder;
                        int position = mViewHolder.getAdapterPosition();
                        record.phoneNumbers.remove(position);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildDraw(Canvas c,
                                            RecyclerView recyclerView,
                                            RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState,
                                            boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;

                        background.setBounds(0, itemView.getTop(),   itemView.getRight(), itemView.getBottom());

                        background.draw(c);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        adapter.notifyDataSetChanged();
    }

    void updateRecord(){
        String fname, lname;
        int rating;
        fname = fnameEditText.getText().toString();
        lname = lnameEditText.getText().toString();
        rating = (int)ratingBar.getRating();
        Log.e("rating stars", String.valueOf(rating));

        if(fname.isEmpty()){
            Toast.makeText(getContext(), "Please provide a name for this contact", Toast.LENGTH_LONG).show();
            return;
        }

        TablePerson person = new TablePerson();
        person.setPer_fname(fname);
        person.setPer_lname(lname);
        person.setPer_risk_level(rating);

        List<TablePhoneNumber> phoneNumbers = new ArrayList<>();

        for(String s : record.phoneNumbers){
            TablePhoneNumber table = new TablePhoneNumber();
            table.setPer_id(-1); //we get per_id after adding the person to the database, since per_id is auto generated
            table.setPhone_number(s);
            phoneNumbers.add(table);
        }

        new Updater().execute(person, phoneNumbers);
    }

    private class Updater extends AsyncTask<Object, Void, String>{

        @Override
        protected String doInBackground(Object... params) {
            TablePerson personTable = (TablePerson) params[0];
            List<TablePhoneNumber> phoneNumberTable = (List<TablePhoneNumber>) params[1];
            int per_id;
            if(isNewRecord) {
                MainActivity.mainDatabase.personDao().addPerson(personTable);
                per_id = MainActivity.mainDatabase.personDao().getLastInsertedPersonID();
            }
            else{
                personTable.setPer_id(record.getPer_id());
                MainActivity.mainDatabase.personDao().updatePerson(personTable);
                per_id = record.getPer_id();
            }
             //get per_id from person table

            if(!isNewRecord)
                MainActivity.mainDatabase.personDao().deletePhoneNumbersById(per_id);

            for(TablePhoneNumber p : phoneNumberTable){
                p.setPer_id(per_id);
                MainActivity.mainDatabase.personDao().addPhoneNumber(p);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s){

                record.setFirstName(fnameEditText.getText().toString());
                record.setLastName(lnameEditText.getText().toString());
                record.setPer_risk_level((int)ratingBar.getRating());


            newPersonListener.RefreshPersonList(record);
            dismiss();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }
}
