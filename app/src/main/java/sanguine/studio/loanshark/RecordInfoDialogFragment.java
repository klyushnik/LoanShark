package sanguine.studio.loanshark;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecordInfoDialogFragment extends DialogFragment implements
        Interfaces.IOnRecordEditListener,
        Interfaces.IOnPaymentListener{
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    MainRecord record;
    Button paymentReturnButton, closeButton, modifyButton;
    ImageButton backButton;
    LinearLayout monetaryLinearLayout, nonmonetaryLinearLayout, paymentsLinearLayout;
    TextView nameTextView, riskLevelTextView, debtReasonTextView; //basic info
    TextView initialAmtTextView, interestTextView, totalAmtTextView; //monetary debt info
    TextView paidSoFarTextView, stillOweTextView; //payments info - hidden if n/a
    TextView initialDateTextView, dueDateTextView; //due date and initial date
    NonScrollListView phoneNumberListView, itemsBorrowedListView; //nonscrollable listviews for phone numbers and items borrowed
    ConstraintLayout rootLayout;
    ItemsBorrowedListViewAdapter itemsBorrowedListViewAdapter;
    ArrayList<String> nonMonetaryItems;
    PhoneNumberListViewAdapter phoneNumberListViewAdapter;
    List<String> phoneNumbers;

    float acquiredInterest = 0, totalAmtOwed = 0, paidAmt = 0;

    public RecordInfoDialogFragment() {
        // Required empty public constructor
    }

    public void setMainRecord(MainRecord record){
        this.record = record;
    }

    Interfaces.IOnRecordEditListener mListener;

    public void setOnRecordEditListener(Interfaces.IOnRecordEditListener listener){
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme);

    }

    @Override
    public void onStart(){
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_info_dialog, container, false);

        findAllViews(view);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dpHeight = displayMetrics.heightPixels;
        int dpWidth = displayMetrics.widthPixels;
        rootLayout.setMinHeight(dpHeight - Util.GetNavBarHeight(getContext()) - Util.GetStatusBarHeight(getContext()) - 20);
        rootLayout.setMinWidth(dpWidth - 250);

        nonMonetaryItems = new ArrayList<>();
        nonMonetaryItems.addAll(record.getNonMonetaryItems());

        itemsBorrowedListViewAdapter = new ItemsBorrowedListViewAdapter(getContext(), nonMonetaryItems, false);
        itemsBorrowedListView.setAdapter(itemsBorrowedListViewAdapter);
        itemsBorrowedListViewAdapter.notifyDataSetChanged();

        paymentReturnButton.setOnClickListener(v -> {
            //if monetary, then show payment options dialog which will add a history item
            //else ask if you want to return nonmonetary items, add history item, and delete MainRecord & TableMainRecord instance
            if(record.isMonetary()) {
                EnterPaymentFragment enterPaymentFragment = new EnterPaymentFragment();
                enterPaymentFragment.setData(this, totalAmtOwed - paidAmt, getContext());
                FragmentManager manager = getFragmentManager();
                enterPaymentFragment.show(manager,"");
            }else{
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to set this debt as fully paid?\nThis will delete the record while retaining the history.")
                        .setTitle("Return items")
                        .setPositiveButton("Pay All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Payment().execute(0f);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        closeButton.setOnClickListener(v -> dismiss());

        backButton.setOnClickListener(view1 -> dismiss());

        modifyButton.setOnClickListener(v -> launchEditRecordFragment());

        phoneNumberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number = phoneNumbers.get(i);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setMessage("Call " + record.getPersonName() + " on " + number + "?")
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

        new GetInfoFromDb().execute();

        return view;
    }

    void launchEditRecordFragment(){
        AddEditRecordFragment fragment = new AddEditRecordFragment();
        fragment.setRecord(record, record.isCreditor());
        FragmentManager manager = getFragmentManager();
        fragment.setOnRecordEditListener(this);
        fragment.show(manager, "");
    }


    void findAllViews(View view){

        paymentReturnButton = view.findViewById(R.id.recordInfo_payAllButton);
        closeButton = view.findViewById(R.id.recordInfo_closeButton);
        modifyButton = view.findViewById(R.id.recordInfo_editButton);
        backButton = view.findViewById(R.id.recordInfo_BackImageButton);

        monetaryLinearLayout = view.findViewById(R.id.recordInfo_monetaryLinearLayout);
        nonmonetaryLinearLayout = view.findViewById(R.id.recordInfo_nonMonetaryLinearLayout);
        paymentsLinearLayout = view.findViewById(R.id.recordInfo_paymentsLinearLayout);

        nameTextView = view.findViewById(R.id.recordInfo_nameTextView);
        riskLevelTextView = view.findViewById(R.id.recordInfo_riskLevelTextView);
        debtReasonTextView = view.findViewById(R.id.recordInfo_debtReasonTextView);

        initialAmtTextView = view.findViewById(R.id.recordInfo_initialAmtTextView);
        interestTextView = view.findViewById(R.id.recordInfo_interestTextView);
        totalAmtTextView = view.findViewById(R.id.recordInfo_totalAmtTextView);

        paidSoFarTextView = view.findViewById(R.id.recordInfo_alreadyPaidTextView);
        stillOweTextView = view.findViewById(R.id.recordInfo_StillOweTextView);

        initialDateTextView = view.findViewById(R.id.recordInfo_initialDateTextView);
        dueDateTextView = view.findViewById(R.id.recordInfo_dueDateTextView);

        phoneNumberListView = view.findViewById(R.id.recordInfo_phoneNumbersListView);
        itemsBorrowedListView = view.findViewById(R.id.recordInfo_nonMonetaryListView);

        rootLayout = view.findViewById(R.id.recordInfo_root);
    }

    void displayAllInfo(){
        riskLevelTextView.setText(record.getPersonName().substring(0,1).toUpperCase());
        setRiskLevelBackground();
        nameTextView.setText(record.getPersonName());
        debtReasonTextView.setText(record.getDebtReason());

        if(record.isMonetary()){
            nonmonetaryLinearLayout.setVisibility(View.GONE);
            monetaryLinearLayout.setVisibility(View.VISIBLE);
            paymentReturnButton.setText(R.string.button_pay_all);

            boolean hasPaid = paidAmt > 0;
            acquiredInterest = 0;

            //interest
            //TODO: move to Util
            int unitsPassed = (int)getDaysBetweenDates(record.getInitialDate()); //replace with SELECT JULIANDATE() - JULIANDATE(initial_date) later on

            switch (record.getInterestType()){
                case "d": //daily
                    break;
                case "w": //weekly
                    unitsPassed /= 7;
                    break;
                case "bw": //bi-weekly
                    unitsPassed /=14;
                    break;
                case "m": //legalese monthly
                    unitsPassed /=30;
                    break;
                case "y": //annual
                    unitsPassed /=365;
                    break;
                case "n": //no interest
                default:
                    unitsPassed = 0;
            }

            //simple interest, skip if no-interest loan, or no interest acquired yet
            if(unitsPassed > 0){
                for (int i = 0; i < unitsPassed; i++){
                    acquiredInterest += record.getDebtAmount() * record.getInterestRate() / 100;
                }
            }
            totalAmtOwed = record.getDebtAmount() + acquiredInterest;

            initialAmtTextView.setText(String.format("$%.2f", record.getDebtAmount()));
            interestTextView.setText(String.format("$%.2f", acquiredInterest));
            totalAmtTextView.setText(String.format("$%.2f", totalAmtOwed));

            if(hasPaid){
                paidSoFarTextView.setText(String.format("$%.2f", paidAmt));
                stillOweTextView.setText(String.format("$%.2f", totalAmtOwed - paidAmt));
            } else{
                paidSoFarTextView.setText("N/A");
                stillOweTextView.setText(String.format("$%.2f", totalAmtOwed));
            }
        } else {
            paymentReturnButton.setText(R.string.button_return_all);
            nonmonetaryLinearLayout.setVisibility(View.VISIBLE);
            monetaryLinearLayout.setVisibility(View.GONE);
            nonMonetaryItems.clear();
            nonMonetaryItems.addAll(record.getNonMonetaryItems());
            itemsBorrowedListViewAdapter.notifyDataSetChanged();
        }

        initialDateTextView.setText(record.getInitialDate());
        dueDateTextView.setText(record.getDueDate());

    }

    public static long getDaysBetweenDates(String start) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = Calendar.getInstance().getTime();
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    void setRiskLevelBackground(){
        switch (record.getRiskLevel()){
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
    }

    @Override
    public void updateRecordInfo(MainRecord... record) {
        setMainRecord(record[0]);
        displayAllInfo();
        mListener.updateRecordInfo();
    }

    private class Payment extends AsyncTask<Float, Void, Integer>{

        @Override
        protected Integer doInBackground(Float... paymentAmt) {
            //add history item and delete MainRecord
            boolean isFinalPayment = false;

            if(!record.isMonetary()) {
                isFinalPayment = true;
                paymentAmt[0] = 0f;
            }else{
                //isFinalPayment = paymentAmt[0] >= totalAmtOwed - paidAmt;
                isFinalPayment = Float.compare(paymentAmt[0], totalAmtOwed - paidAmt)>= 0;
            }

            TableHistory tableHistory = new TableHistory();
            tableHistory.setFk_per_id(record.getPersonId());
            tableHistory.setDebt_reason(record.getDebtReason());
            tableHistory.setIs_creditor(record.isCreditor() ? 1 : 0);
            tableHistory.setIs_final(isFinalPayment ? 1 : 0);
            tableHistory.setIs_monetary(record.isMonetary() ? 1 : 0);
            tableHistory.setPayment_amount(paymentAmt[0]);
            tableHistory.setPayment_date(Util.GetTodaysDate());
            tableHistory.setRec_id(record.getRecordId());

            MainActivity.mainDatabase.personDao().addHistoryRecord(tableHistory);

            TableMainRecord tableMainRecord = new TableMainRecord();
            tableMainRecord.setRec_id(record.getRecordId());

            if(isFinalPayment) {
                MainActivity.mainDatabase.personDao().deleteRecord(tableMainRecord);
            }else{
                paidAmt += paymentAmt[0];

            }
            return isFinalPayment ? 1 : 0;
        }

        @Override
        protected void onPostExecute(Integer i){
            //call mListener.updateRecordInfo and dismiss fragment

            if(i==1) {
                mListener.updateRecordInfo();
                dismiss();
            }else{
                displayAllInfo();
            }
        }
    }

    private class GetInfoFromDb extends AsyncTask<Integer, Void, Integer>{
        @Override
        protected Integer doInBackground(Integer... integers) {
            phoneNumbers = MainActivity.mainDatabase.personDao().getPhoneNumbersFromRecId(record.getRecordId());
            paidAmt = MainActivity.mainDatabase.personDao().getTotalPayments(record.getRecordId());
            return 0;
        }

        @Override
        protected void onPostExecute(Integer i){
            phoneNumberListViewAdapter = new PhoneNumberListViewAdapter(getContext(), phoneNumbers);
            phoneNumberListView.setAdapter(phoneNumberListViewAdapter);
            phoneNumberListViewAdapter.notifyDataSetChanged();
            displayAllInfo();
        }
    }

    public void onPaymentEntered(float paidAmt){
        new Payment().execute(paidAmt);
    }
}
