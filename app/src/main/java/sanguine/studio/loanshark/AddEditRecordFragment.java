package sanguine.studio.loanshark;



import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditRecordFragment extends DialogFragment implements
                Interfaces.IOnDatePickedListener,
                View.OnClickListener,
                Interfaces.INewPersonListener{


    boolean isRecordMonetary = true;
    boolean hasInterest = false;
    boolean isNewRecord = true;
    boolean isCreditor = false;
    MainRecord record;

    Interfaces.IOnRecordEditListener listener;

    EditText reasonForBorrowingEditText, amountBorrowedEditText, interestAmountEditText;
    /*EditText nonMonItem0, nonMonItem1, nonMonItem2, nonMonItem3, nonMonItem4;*/
    EditText[] nonMonItemsEditTextList = new EditText[3];
    RadioButton monetaryRadioButton, nonMonetaryRadioButton;
    RadioGroup monetaryRadioGroup;
    Switch interestSwitch;
    Button addNewPersonButton, pickDateButton, updateRecordButton, cancelButton;
    TextView dateTextView;
    Spinner personSpinner, interestTypeSpinner;
    LinearLayout monetaryLinearLayout, nonMonetaryLinearLayout, interestLinearLayout;
    ConstraintLayout rootLayout;
    //NonScrollListView itemsBorrowedListView;
    List<TablePerson> person;
    ArrayAdapter personSpinnerAdapter;
    ArrayAdapter interestSpinnerAdapter;

    ArrayList<String> nonMonetaryItems;

    ImageButton backImageButton;


    //ItemsBorrowedListViewAdapter itemsBorrowedListViewAdapter;


    public void setRecord(MainRecord record, boolean isCreditor){ //if this is called, then update will be issued, else a new record
        this.record = record;
        isNewRecord = false;
        this.isCreditor = isCreditor;
    }

    public void setIsCreditor(boolean isCreditor){
        this.isCreditor = isCreditor;
    }

    public void setOnRecordEditListener(Interfaces.IOnRecordEditListener listener){
        this.listener = listener;
    }

    public AddEditRecordFragment() {
        // Required empty public constructor
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

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_edit_record, container, false);
        setUpUiElements(view);

        person = new ArrayList<>();
        nonMonetaryItems = new ArrayList<>();

        if(!isNewRecord){
            nonMonetaryItems.addAll(record.nonMonetaryItems);
        }

        personSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, person);
        personSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personSpinner.setAdapter(personSpinnerAdapter);

        RefreshPersonList();

        interestSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.interest_types));
        interestSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interestTypeSpinner.setAdapter(interestSpinnerAdapter);


        /*itemsBorrowedListViewAdapter = new ItemsBorrowedListViewAdapter(getContext(), nonMonetaryItems, true);
        itemsBorrowedListView.setAdapter(itemsBorrowedListViewAdapter);
        itemsBorrowedListViewAdapter.notifyDataSetChanged();*/

        nonMonetaryLinearLayout.setVisibility(View.GONE);


        if(!isNewRecord){
            //if this is an update, then we need to load all the text
            loadRecord(record);
        }

        return view;
    }

    @Override
    public void onDatePicked(String date) {
        dateTextView.setText(date);
        Log.e("date picked", date);
    }

    void setUpUiElements(View view){
        reasonForBorrowingEditText = view.findViewById(R.id.addRecReasonEditText);
        amountBorrowedEditText = view.findViewById(R.id.addRecAmountBorrowedEditText);
        interestAmountEditText = view.findViewById(R.id.addRecInterestAmtEditText);
        monetaryRadioButton = view.findViewById(R.id.addRecMonetaryRadioButton);
        nonMonetaryRadioButton = view.findViewById(R.id.addRecNonmonetaryRadioButton);
        monetaryRadioGroup = view.findViewById(R.id.addRecMonetaryRadioGroup);
        interestSwitch = view.findViewById(R.id.addRecInterestSwitch);
        addNewPersonButton = view.findViewById(R.id.addRecAddNewPersonButton);
        pickDateButton = view.findViewById(R.id.addRecDateButton);
        updateRecordButton = view.findViewById(R.id.addRecAddButton);
        cancelButton = view.findViewById(R.id.addRecCancelButton);
        dateTextView = view.findViewById(R.id.addRecDateTextView);
        personSpinner = view.findViewById(R.id.addRecSelectPersonSpinner);
        interestTypeSpinner = view.findViewById(R.id.addRecInterestTypeSpinner);
        monetaryLinearLayout = view.findViewById(R.id.addRecMonetaryLinearLayout);
        nonMonetaryLinearLayout = view.findViewById(R.id.addRecNonmonetaryLinearLayout);
        interestLinearLayout = view.findViewById(R.id.addRecInterestLinearLayout);
        rootLayout = view.findViewById(R.id.addRecRootLayout);
        backImageButton = view.findViewById(R.id.recordInfo_BackImageButton);

        /*nonMonItem0 = view.findViewById(R.id.addRecNonMonItem_0_editText);
        nonMonItem1 = view.findViewById(R.id.addRecNonMonItem_1_editText);
        nonMonItem2 = view.findViewById(R.id.addRecNonMonItem_2_editText);
        nonMonItem3 = view.findViewById(R.id.addRecNonMonItem_3_editText);
        nonMonItem4 = view.findViewById(R.id.addRecNonMonItem_4_editText);*/

        nonMonItemsEditTextList[0] = view.findViewById(R.id.addRecNonMonItem_0_editText);
        nonMonItemsEditTextList[1] = view.findViewById(R.id.addRecNonMonItem_1_editText);
        nonMonItemsEditTextList[2] = view.findViewById(R.id.addRecNonMonItem_2_editText);

        //addNewPersonButton, addNewItemButton, pickDateButton, updateRecordButton, cancelButton;
        addNewPersonButton.setOnClickListener(this);
        pickDateButton.setOnClickListener(this);
        updateRecordButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);

        monetaryRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.addRecMonetaryRadioButton){
                    monetaryLinearLayout.setVisibility(View.VISIBLE);
                    nonMonetaryLinearLayout.setVisibility(View.GONE);
                    isRecordMonetary = true;
                }
                else {
                    monetaryLinearLayout.setVisibility(View.GONE);
                    nonMonetaryLinearLayout.setVisibility(View.VISIBLE);
                    isRecordMonetary = false;
                }
            }
        });

        interestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    interestLinearLayout.setVisibility(View.VISIBLE);
                    hasInterest = true;
            }
                else {
                    interestLinearLayout.setVisibility(View.GONE);
                    hasInterest = false;
                }
            }
        });

    }

    void loadRecord(MainRecord record){
        //load all the values from the record
        //select person
        if (record == null) return;

        //load all the values
        //select person
        personSpinner.post(() -> {
            int i = 0;
            for(TablePerson t : person){
                if(t.getPer_id() == record.getPersonId()){
                    i = person.indexOf(t);
                    break;
                }
            }
            personSpinner.setSelection(i);
        });
        //debt reason
        reasonForBorrowingEditText.setText(record.getDebtReason());

        //load items to the nonmonetary items list
        if(!record.isMonetary()){
            monetaryRadioGroup.check(R.id.addRecNonmonetaryRadioButton);
            //all items are already added in the start, so dont have to add
            amountBorrowedEditText.setText("0");

            for(String s : nonMonetaryItems){
                nonMonItemsEditTextList[nonMonetaryItems.indexOf(s)].setText(s);
            }

        }else{
            amountBorrowedEditText.setText(String.valueOf(record.getDebtAmount()));
            interestAmountEditText.setText(String.valueOf(record.getInterestRate()));
            if(record.getInterestType() != "n"){
                interestSwitch.setChecked(true);
            }
            interestTypeSpinner.post(() -> {
                int i;
                switch(record.getInterestType()){
                    //select the required index from interestTypes.xml
                    case "d": //daily
                        i=5;
                        break;
                    case "w": //weekly
                        i=4;
                        break;
                    case "bw": //bi-weekly
                        i=3;
                        break;
                    case "m": //legalese monthly
                        i=2;
                        break;
                    case "y": //annual
                        i=1;
                        break;
                    case "n": //no interest
                    default:
                        i=0;
                }
                interestTypeSpinner.setSelection(i);
            });
        }

        //pick the date
        if (!isNewRecord)
            dateTextView.setText(record.getDueDate());
    }

    private MainRecord processRecord(){
        //generate a new record
        TablePerson tablePerson = (TablePerson)personSpinner.getSelectedItem();
        Float debtAmount = amountBorrowedEditText.getText().toString().isEmpty() ?
                0 : Float.parseFloat(amountBorrowedEditText.getText().toString());
        Float interestRate = interestAmountEditText.getText().toString().isEmpty() ?
                0 : Float.parseFloat(interestAmountEditText.getText().toString());
        String interestType;
        if(!interestSwitch.isChecked() | interestRate == 0)
            interestType = "n";
        else
        {
            switch (interestTypeSpinner.getSelectedItemPosition()){
                case 0:
                    interestType = "n";
                    break;
                case 1:
                    interestType = "y";
                    break;
                case 2:
                    interestType = "m";
                    break;
                case 3:
                    interestType = "bw";
                    break;
                case 4:
                    interestType = "w";
                    break;
                case 5:
                    interestType = "d";
                    break;
                default:
                    interestType = "n";
                    break;
            }
        }

        if(!monetaryRadioButton.isChecked()){
            //nonmonetaryitems is string, the record is generated later upon update
            nonMonetaryItems.clear();
            for(int i = 0; i < 3; i++){
                if(!nonMonItemsEditTextList[i].getText().toString().isEmpty()){
                    nonMonetaryItems.add(nonMonItemsEditTextList[i].getText().toString());
                }
            }
        }

        return new MainRecord.MainRecordBuilder()
                .setDebtAmount(debtAmount)
                .setDebtReason(reasonForBorrowingEditText.getText().toString())
                .setDueDate(dateTextView.getText().toString())
                .setInitialDate(isNewRecord ? Util.GetTodaysDate() : record.getInitialDate())
                .setInterestRate(interestRate)
                .setInterestType(interestType)
                .setIsCreditor(isCreditor)
                .setIsMonetary(monetaryRadioButton.isChecked())
                .setNonMonetaryItems(nonMonetaryItems)
                .setPersonName(tablePerson.toString())
                .setPersonId(tablePerson.getPer_id())
                .setRecordId(isNewRecord ? -1 : record.getRecordId())
                .setRiskLevel(tablePerson.getPer_risk_level())
                .createMainRecord();
    }


    @Override
    public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        switch (v.getId()){
            case R.id.addRecAddButton:
                new MainRecordUpdater().execute(processRecord());
                dismiss();
                break;
            case R.id.addRecAddNewPersonButton:
                AddEditPersonFragment addEditPersonFragment = new AddEditPersonFragment();
                addEditPersonFragment.setNewPersonListener(this);
                addEditPersonFragment.show(manager, "");
                break;
            case R.id.addRecDateButton:
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setOnDatePickedListener(this);
                datePickerFragment.show(manager,"");
                break;
            case R.id.addRecCancelButton:
            case R.id.recordInfo_BackImageButton:
                dismiss();
                break;
            default:
                throw new IllegalArgumentException("AddEditRecordFragment.onClick: there is no such button!");
        }
    }

    @Override
    public void RefreshPersonList(PersonRecord... record) {
        new PersonUpdater().execute("");
    }

    private class PersonUpdater extends AsyncTask<String, Void, List<TablePerson>>{
        @Override
        protected void onPreExecute(){
            person.clear();
        }

        @Override
        protected List<TablePerson> doInBackground(String... strings) {
            List<TablePerson> list = MainActivity.mainDatabase.personDao().getPersons();
            return list;
        }

        @Override
        protected void onPostExecute(List<TablePerson> list){
            person.addAll(list);
            personSpinnerAdapter.notifyDataSetChanged();
        }

    }

    private class MainRecordUpdater extends AsyncTask<MainRecord, Void, MainRecord>{

        @Override
        protected MainRecord doInBackground(MainRecord... params) { //record and records[] are too similar
            //this is where the database is updated
            //issue update query if in update mode, else create new one
            TableMainRecord tableMainRecord = params[0].generateTableMainRecord(isNewRecord);
            int newRecId;

            if(isNewRecord){
                MainActivity.mainDatabase.personDao().addRecord(tableMainRecord);
                newRecId = MainActivity.mainDatabase.personDao().getLastInsertedRecordId();

                //add nonmonetary items
                if(!params[0].isMonetary()){
                    for(String s : params[0].nonMonetaryItems){
                        TableNonMonetaryItems nonMonetaryItems = params[0].generateNonMonetaryItem(newRecId, s);
                        MainActivity.mainDatabase.personDao().addNonMonetaryItems(nonMonetaryItems);
                    }
                }
            }else{
                MainActivity.mainDatabase.personDao().updateRecord(tableMainRecord);

                if(!params[0].isMonetary()) {
                    MainActivity.mainDatabase.personDao().clearNonMonetaryItems(params[0].getRecordId());

                    for(String s : params[0].nonMonetaryItems){
                        TableNonMonetaryItems nonMonetaryItems = params[0].generateNonMonetaryItem(params[0].getRecordId(), s);
                        MainActivity.mainDatabase.personDao().addNonMonetaryItems(nonMonetaryItems);
                    }
                }
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(MainRecord result){
            //dismiss the progress dialog and call the cleanup
            if(listener != null){
                listener.updateRecordInfo(result);
            }
            dismiss();
        }

        @Override
        protected void onPreExecute(){
            //start the progress dialog

        }
    }

}
