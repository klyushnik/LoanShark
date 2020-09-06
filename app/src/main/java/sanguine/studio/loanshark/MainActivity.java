package sanguine.studio.loanshark;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;
import android.content.Context;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static MainDatabase mainDatabase;

    ConstraintLayout mainToolbarLayout;
    FrameLayout fragmentContainer;
    Bundle mSavedInstanceState;
    TextView toolbarHeaderTextView;
    ImageButton toolbarMenuButton, toolbarAddButton, toolbarSearchButton, toolbarSortButton;
    Button navBorrowersButton, navCreditorsButton, navHistoryButton, navSettingsButton, navContactsButton, navManualButton, toolbarCancelSearchButton;
    DrawerLayout drawerLayout;
    LinearLayout searchLayout;
    EditText filterEditText;

    int fragmentSenderId;
    public String DB_NAME;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("appSettings", Context.MODE_PRIVATE);
        DB_NAME = sharedPref.getString("db_name", "mainDB");
        mainDatabase = Room.databaseBuilder(getApplicationContext(), MainDatabase.class, DB_NAME).build();
        setContentView(R.layout.activity_main);
        fragmentSenderId = getIntent().getIntExtra("id", R.id.borrowersButton); //id from dashboard activity

        SetUpUiElements(); //this is a long ass method so I'll keep it out of onCreate

        mSavedInstanceState = savedInstanceState; //needed to call AttachFragment from another method

        if(sharedPref.getBoolean("firstLaunch", true)){
            SampleRecords();
        }else {
            BeginAttachFragment(fragmentSenderId); //attach the fragment with id from dashboard
        }
    }


    private void SetUpUiElements(){
        mainToolbarLayout = findViewById(R.id.mainToolbarConstraintLayout);
        fragmentContainer = findViewById(R.id.mainActivityFragmentFrameLayout);
        toolbarHeaderTextView = findViewById(R.id.toolbarHeaderTextView);
        toolbarMenuButton = findViewById(R.id.toolbarMenuButton);
        toolbarAddButton = findViewById(R.id.toolbarAddButton);
        toolbarSearchButton = findViewById(R.id.toolbarSearchButton);
        toolbarSortButton = findViewById(R.id.toolbarSortButton);
        navBorrowersButton = findViewById(R.id.borrowersNavButton);
        navCreditorsButton = findViewById(R.id.creditorsNavButton);
        navHistoryButton = findViewById(R.id.historyNavButton);
        navSettingsButton = findViewById(R.id.settingsNavButton);
        navContactsButton = findViewById(R.id.contactsNavButton);
        navManualButton = findViewById(R.id.manualNavButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        searchLayout = findViewById(R.id.mainActivityTextFilterLayout);
        filterEditText = findViewById(R.id.mainFilterEditText);
        toolbarCancelSearchButton = findViewById(R.id.toolbarCancelSearchButton);

        toolbarSortButton.setOnClickListener(this);
        toolbarSearchButton.setOnClickListener(this);
        toolbarAddButton.setOnClickListener(this);
        toolbarMenuButton.setOnClickListener(this);
        toolbarCancelSearchButton.setOnClickListener(this);

        navManualButton.setOnClickListener(this);
        navContactsButton.setOnClickListener(this);
        navSettingsButton.setOnClickListener(this);
        navHistoryButton.setOnClickListener(this);
        navCreditorsButton.setOnClickListener(this);
        navBorrowersButton.setOnClickListener(this);

        filterEditText.addTextChangedListener(new TextWatcher() {
            Fragment childFragment;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //get the fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                childFragment = fragmentManager.findFragmentById(R.id.mainActivityFragmentFrameLayout);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(childFragment instanceof MainRecordFragment){
                    ((MainRecordFragment) childFragment).Search(filterEditText.getText().toString());
                }
                else if(childFragment instanceof PersonFragment){
                    ((PersonFragment) childFragment).Search(filterEditText.getText().toString());
                }
                else if(childFragment instanceof HistoryFragment){
                    ((HistoryFragment) childFragment).Search(filterEditText.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void SampleRecords(){
        TablePerson person = new TablePerson();
        person.setPer_fname("Benedict");
        person.setPer_lname("Brokeford");
        person.setPer_risk_level(5);

        TablePhoneNumber tablePhoneNumber = new TablePhoneNumber();
        tablePhoneNumber.setPhone_number("19165556666");

        TableMainRecord record = new TableMainRecord();
        record.setDebtAmount(500f);
        record.setDebtReason("Owes me rent");
        record.setDueDate("2025-12-25");
        record.setInitialDate("2018-01-01");
        record.setInterestRate(25);
        record.setInterestType("m");
        record.setIsCreditor(0);
        record.setIsMonetary(1);

        new FirstRun().execute(person, tablePhoneNumber, record, fragmentSenderId);


        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("firstLaunch", false);
        editor.apply();
    }

    public void BeginAttachFragment(int id){
        Fragment newFragment;
        filterEditText.setText(null);
        searchLayout.setVisibility(View.GONE);
        Bundle params = new Bundle();
        //set all toolbar buttons to visible, so that they can be removed when necessary
        toolbarAddButton.setVisibility(View.VISIBLE);
        toolbarSearchButton.setVisibility(View.VISIBLE);
        toolbarSortButton.setVisibility(View.VISIBLE);
        switch (id) {
            case R.id.borrowersButton:
            case R.id.borrowersNavButton:
                toolbarHeaderTextView.setText(R.string.toolbar_borrowers);
                newFragment = new MainRecordFragment();
                params.putInt("mode", 0);
                break;
            case R.id.creditorsButton:
            case R.id.creditorsNavButton:
                toolbarHeaderTextView.setText(R.string.toolbar_creditors);
                newFragment = new MainRecordFragment();
                params.putInt("mode", 1);
                break;
            case R.id.historyButton:
            case R.id.historyNavButton:
                toolbarHeaderTextView.setText(R.string.toolbar_history);
                toolbarAddButton.setVisibility(View.GONE);
                newFragment = new HistoryFragment();
                params.putInt("mode", 0);
                break;
            case R.id.settingsButton:
            case R.id.settingsNavButton:
                toolbarHeaderTextView.setText(R.string.toolbar_settings);
                toolbarAddButton.setVisibility(View.GONE);
                toolbarSearchButton.setVisibility(View.GONE);
                toolbarSortButton.setVisibility(View.GONE);
                newFragment = new SettingsFragment();
                params.putInt("mode", 0);
                break;
            case R.id.contactsButton:
            case R.id.contactsNavButton:
                toolbarHeaderTextView.setText(R.string.toolbar_contacts);
                newFragment = new PersonFragment();
                break;
            case R.id.manualButton:
            case R.id.manualNavButton:
                toolbarHeaderTextView.setText(R.string.toolbar_manual);
                toolbarAddButton.setVisibility(View.GONE);
                toolbarSearchButton.setVisibility(View.GONE);
                toolbarSortButton.setVisibility(View.GONE);
                newFragment = new ManualFragment();
                break;
            default:
                Log.e("###_DASHBOARD_", "DashBoardFragment.case: Trying to call onClick with an undefined view!");
                throw new IllegalArgumentException();
        }

        AttachFragment(newFragment, fragmentContainer, mSavedInstanceState, params);
    }

    public void AttachFragment(Fragment fragment, View view, Bundle savedInstanceState, Bundle params){ //copy paste from android dev center
        if (view != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(params);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainActivityFragmentFrameLayout, fragment).commit();
        }

    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment childFragment = fragmentManager.findFragmentById(R.id.mainActivityFragmentFrameLayout);
        switch (v.getId()){
            case R.id.toolbarAddButton:
                //get fragment from the container view, if right one, call its AddRecord()

                if(childFragment instanceof MainRecordFragment){
                    ((MainRecordFragment) childFragment).AddRecord();
                } else if(childFragment instanceof PersonFragment){
                    ((PersonFragment) childFragment).AddRecord();
                }
                break;
            case R.id.toolbarSearchButton:
            case R.id.toolbarCancelSearchButton:
                if(searchLayout.getVisibility() == View.GONE) {
                    searchLayout.setVisibility(View.VISIBLE);
                    filterEditText.setText(null);
                    filterEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm != null) {
                        imm.showSoftInput(filterEditText, 0);
                    }
                }
                else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(filterEditText.getWindowToken(), 0);
                    }
                    filterEditText.clearFocus();
                    filterEditText.setText(null);
                    searchLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.toolbarMenuButton:
                if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.closeDrawer(Gravity.LEFT, true);
                }else{
                    drawerLayout.openDrawer(Gravity.LEFT, true);
                }
                break;
            case R.id.toolbarSortButton:
                if(childFragment instanceof MainRecordFragment){
                    ((MainRecordFragment) childFragment).Sort();
                }
                else if(childFragment instanceof PersonFragment){
                    ((PersonFragment) childFragment).Sort();
                }
                else if(childFragment instanceof HistoryFragment){
                    ((HistoryFragment) childFragment).Sort();
                }
                break;
            case R.id.borrowersNavButton:
            case R.id.creditorsNavButton:
            case R.id.historyNavButton:
            case R.id.settingsNavButton:
            case R.id.contactsNavButton:
            case R.id.manualNavButton:
                if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT, true);
                }
                BeginAttachFragment(v.getId());
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
    }

    private class FirstRun extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {

            TablePerson personTable = (TablePerson) params[0];
            TablePhoneNumber phoneNumberTable = (TablePhoneNumber) params[1];
            TableMainRecord record = (TableMainRecord)params[2];
            int per_id;

            MainActivity.mainDatabase.personDao().addPerson(personTable);
            per_id = MainActivity.mainDatabase.personDao().getLastInsertedPersonID(); //get per_id from person table


            phoneNumberTable.setPer_id(per_id);
            MainActivity.mainDatabase.personDao().addPhoneNumber(phoneNumberTable);

            record.setFk_per_id(per_id);

            for(int i = 0; i < 3; i++) {
                MainActivity.mainDatabase.personDao().addRecord(record);
            }

            personTable.setPer_fname("Humphrey");
            personTable.setPer_lname("Fudgeworth");
            MainActivity.mainDatabase.personDao().addPerson(personTable);
            per_id = MainActivity.mainDatabase.personDao().getLastInsertedPersonID();

            phoneNumberTable.setPer_id(per_id);
            phoneNumberTable.setPhone_number("19165551111");
            MainActivity.mainDatabase.personDao().addPhoneNumber(phoneNumberTable);

            record.setDebtAmount(1000);
            record.setDebtReason("Cash advance");
            record.setFk_per_id(per_id);
            record.setIsCreditor(1);
            MainActivity.mainDatabase.personDao().addRecord(record);


            return null;
        }

        @Override
        protected void onPostExecute(String s){
            BeginAttachFragment(fragmentSenderId);
        }
    }
}
