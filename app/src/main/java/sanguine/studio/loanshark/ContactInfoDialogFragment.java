package sanguine.studio.loanshark;


import android.app.Dialog;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactInfoDialogFragment extends DialogFragment implements Interfaces.INewPersonListener{

    TextView nameTextView, owesMeTextView, iOweTextView, riskLevelTextView;
    RatingBar ratingBar;
    Button modifyButton, closeButton, showTransactionsButton;
    ImageButton backButton;
    PhoneNumberListViewAdapter phoneNumberListViewAdapter;
    NonScrollListView phoneNumberListView;
    PersonRecord record;
    private Interfaces.INewPersonListener listener;
    float creditorDebt, borrowerDebt;

    public ContactInfoDialogFragment() {
        // Required empty public constructor
    }

    public void SetPersonRecord(PersonRecord record){
        this.record = record;
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
        View view = inflater.inflate(R.layout.fragment_contact_info_dialog, container, false);
        setUpUiElements(view);
        LoadRecord();
        SetUpOnClick();
        return view;
    }

    void setUpUiElements(View v){
        nameTextView = v.findViewById(R.id.contactInfo_nameTextView);
        owesMeTextView = v.findViewById(R.id.contactInfo_borrowerDebtTextView);
        iOweTextView = v.findViewById(R.id.contactInfo_creditorDebtTextView);
        ratingBar = v.findViewById(R.id.contactInfo_ratingBar);
        modifyButton = v.findViewById(R.id.contactInfo_editButton);
        closeButton = v.findViewById(R.id.contactInfo_closeButton);
        backButton = v.findViewById(R.id.contactInfo_BackImageButton);
        phoneNumberListView = v.findViewById(R.id.contactInfo_phoneNumbersListView);
        riskLevelTextView = v.findViewById(R.id.contactInfo_riskLevelTextView);
        phoneNumberListViewAdapter = new PhoneNumberListViewAdapter(getContext(), record.phoneNumbers);
        phoneNumberListView.setAdapter(phoneNumberListViewAdapter);
        phoneNumberListViewAdapter.notifyDataSetChanged();
        showTransactionsButton = v.findViewById(R.id.contactInfo_showTransactionsButton);
    }

    void LoadRecord(){

        nameTextView.setText(record.toString());
        ratingBar.setRating(record.getPer_risk_level());
        riskLevelTextView.setText(record.toString().toUpperCase().substring(0,1));
        switch (record.getPer_risk_level()){
            case 1:
                riskLevelTextView.setBackgroundTintList(getContext().getColorStateList(R.color.risk_1));
                break;
            case 2:
                riskLevelTextView.setBackgroundTintList(getContext().getColorStateList(R.color.risk_2));
                break;
            case 3:
                riskLevelTextView.setBackgroundTintList(getContext().getColorStateList(R.color.risk_3));
                break;
            case 4:
                riskLevelTextView.setBackgroundTintList(getContext().getColorStateList(R.color.risk_4));
                break;
            case 5:
            default:
                riskLevelTextView.setBackgroundTintList(getContext().getColorStateList(R.color.risk_5));
        }
        phoneNumberListViewAdapter.notifyDataSetChanged();
        new GetDebtInfo().execute();
    }

    void SetUpOnClick(){
        phoneNumberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number = record.phoneNumbers.get(i);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setMessage("Call " + record.toString() + " on " + number + "?")
                        .setTitle("Call a number")
                        .setPositiveButton("Call", (dialogInterface, i1) -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ number));
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .setNeutralButton("SMS", ((dialog1, which) -> {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number));
                            intent.putExtra("sms_body", "Remember me?");
                            startActivity(intent);
                        }))
                        .create();
                dialog.show();
            }
        });

        backButton.setOnClickListener(view -> dismiss());
        closeButton.setOnClickListener(view -> dismiss());
        modifyButton.setOnClickListener(view -> {
            AddEditPersonFragment fragment = new AddEditPersonFragment();
            fragment.setRecord(record);
            fragment.setNewPersonListener(this);
            FragmentManager manager = getFragmentManager();
            fragment.show(manager,"");
        });

        showTransactionsButton.setOnClickListener(view -> {
            TransactionListDialogFragment fragment = new TransactionListDialogFragment();
            fragment.setData(1, record.getPer_id());
            FragmentManager manager = getFragmentManager();
            fragment.show(manager, "");
        });
    }

    @Override
    public void RefreshPersonList(PersonRecord ... records) {
        record = records[0];
        LoadRecord();
        listener.RefreshPersonList();
    }

    public void setListener(Interfaces.INewPersonListener listener) {
        this.listener = listener;
    }

    private class GetDebtInfo extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            borrowerDebt = MainActivity.mainDatabase.personDao().getTotalBorrowerDebt(record.getPer_id());
            creditorDebt = MainActivity.mainDatabase.personDao().getTotalCreditorDebt(record.getPer_id());
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integers){
            owesMeTextView.setText(String.format("$%.2f",borrowerDebt));
            iOweTextView.setText(String.format("$%.2f",creditorDebt));
            if(borrowerDebt == 0f && creditorDebt == 0f){
                showTransactionsButton.setVisibility(View.GONE);
            }
        }
    }
}
