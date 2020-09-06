package sanguine.studio.loanshark;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TransactionListDialogFragment extends DialogFragment {

    ArrayList<TableHistory> historyList;
    ListView listView;
    Button closeButton;
    int mode, key;
    TransactionListViewAdapter adapter;

    public void setData(int mode, int key){
        this.mode = mode;
        this.key = key;
    }

    public TransactionListDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_list_dialog, container, false);
        listView = view.findViewById(R.id.transactions_recordsListView);
        closeButton = view.findViewById(R.id.transactions_closeButton);
        closeButton.setOnClickListener(view1 -> dismiss());
        historyList = new ArrayList<>();
        adapter = new TransactionListViewAdapter(getContext(), 0, historyList);
        listView.setAdapter(adapter);
        new LoadRecords().execute(mode);

        return view;
    }

    private class LoadRecords extends AsyncTask<Integer, Void, List<TableHistory>>{

        @Override
        protected List<TableHistory> doInBackground(Integer... ints) {
            List<TableHistory> list;
            if(mode == 0){ //get record transactions
                list = MainActivity.mainDatabase.personDao().getRecordTransactions(key);
            }else{ //get person transactions
                list = MainActivity.mainDatabase.personDao().getPersonTransactions(key);
            }
            Log.e("list", String.valueOf(list.size()));
            return list;
        }

        @Override
        protected void onPostExecute(List<TableHistory> tableHistoryList){
            historyList.addAll(tableHistoryList);
            adapter.notifyDataSetChanged();
        }
    }

    private class TransactionListViewAdapter extends ArrayAdapter<TableHistory> {
        ArrayList<TableHistory> data;

        public TransactionListViewAdapter(@NonNull Context context, int resource, ArrayList<TableHistory> data) {
            super(context, resource, data);
            this.data = data;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if(view == null)
                view = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, viewGroup, false);
            TextView debtTypeTextView, paymentAmountTextView, dateTextView;
            debtTypeTextView = view.findViewById(R.id.transactionItem_debtTypeTextView);
            paymentAmountTextView = view.findViewById(R.id.transactionItem_amountTextView);
            dateTextView = view.findViewById(R.id.transactionItem_dateTextView);

            boolean isMonetary = data.get(i).getIs_monetary() == 1;

            if(!isMonetary){
                debtTypeTextView.setText("Nonmonetary return");
                debtTypeTextView.setTextColor(getContext().getColor(R.color.colorOrange));
                paymentAmountTextView.setVisibility(View.GONE);
            }else{
                if(data.get(i).getIs_creditor() == 0){
                    debtTypeTextView.setText("Paid:");
                    debtTypeTextView.setTextColor(getContext().getColor(R.color.colorGreen));
                }else{
                    debtTypeTextView.setText("Paid:");
                    debtTypeTextView.setTextColor(getContext().getColor(R.color.colorRed));
                }
                paymentAmountTextView.setText(String.format("$%.2f", data.get(i).getPayment_amount()));
            }
            dateTextView.setText(data.get(i).getPayment_date());
            return view;
        }
    }


}
