package sanguine.studio.loanshark;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment implements
        Interfaces.IChildFragmentOperations
{
    ArrayList<HistoryRecord> displayList, displayListMaster;
    RecyclerView recyclerView;
    HistoryRecordAdapter adapter;
    TextView statusTextView;

    private int sortingCondition = 0;


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrowers, container, false);

        recyclerView = view.findViewById(R.id.borrowersRecyclerView);
        statusTextView = view.findViewById(R.id.borrowersStatusTextView);
        displayList = new ArrayList<>();
        displayListMaster = new ArrayList<>();
        adapter = new HistoryRecordAdapter(displayList, getContext());

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    Drawable background = new ColorDrawable(getResources().getColor(R.color.colorDialogBackground2, null));

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final RecyclerView.ViewHolder mViewHolder = viewHolder;
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.button_delete_record), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int position = mViewHolder.getAdapterPosition();
                                        new DeleteHistoryRecord().execute(position);
                                    }
                                })
                                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .setMessage(getString(R.string.message_delete_record_prompt))
                                .setTitle(getString(R.string.heading_delete_record))
                                .create();
                        dialog.show();
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

        populate();
        adapter.notifyDataSetChanged();

        return view;
    }

    public void populate(){
        new GetHistoryRecords().execute();
    }

    private class GetHistoryRecords extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            displayList.clear();
            displayListMaster.clear();

            List<TableHistory> tableHistoryList;
            List<TablePerson> tablePersonList;

            tableHistoryList = MainActivity.mainDatabase.personDao().getHistoryRecords();
            tablePersonList = MainActivity.mainDatabase.personDao().getPersons();

            for(TableHistory tH : tableHistoryList){
                String fName = "", lName = "";

                //get name
                for(TablePerson tP : tablePersonList){
                    if(tP.getPer_id() == tH.getFk_per_id()){
                        fName = tP.getPer_fname();
                        lName = tP.getPer_lname();
                        break;
                    }
                }

                //construct HistoryRecord and add to list
                HistoryRecord record = new HistoryRecord.HistoryRecordBuilder()
                        .setFname(fName)
                        .setlName(lName)
                        .setDebtReason(tH.getDebt_reason())
                        .setPaymentAmount(tH.getPayment_amount())
                        .setPaymentDate(tH.getPayment_date())
                        .setIsCreditor(tH.getIs_creditor() == 1)
                        .setIsFinal(tH.getIs_final() == 1)
                        .setIsMonetary(tH.getIs_monetary() == 1)
                        .setHist_id(tH.getHist_id())
                        .createHistoryRecord();
                displayList.add(record);
            }
            displayListMaster.addAll(displayList);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer i){
            adapter.notifyDataSetChanged();
            updateStatus();
        }
    }

    private class DeleteHistoryRecord extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            int position = integers[0];
            TableHistory tableHistory = new TableHistory();
            tableHistory.setHist_id(displayList.get(position).getHist_id());
            MainActivity.mainDatabase.personDao().deleteHistoryRecord(tableHistory);

            displayList.remove(position);
            displayListMaster.remove(position);

            return 0;
        }

        @Override
        protected void onPostExecute(Integer i){
            adapter.notifyDataSetChanged();
            updateStatus();
        }
    }

    void updateStatus(){
        String s = displayList.size() + " record(s)";
        statusTextView.setText(s);
    }

    @Override
    public void Search(String searchString) {
        displayList.clear();
        if(searchString.isEmpty()){
            displayList.addAll(displayListMaster);
        }else {
            displayList.clear();
            searchString = searchString.toLowerCase();
            for(HistoryRecord record : displayListMaster){
                if(record.toString().toLowerCase().contains(searchString)){
                    displayList.add(record);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void Sort() {
        ArrayList<ArrayList<HistoryRecord>> twoLists = new ArrayList<>();
        twoLists.add(displayList);
        twoLists.add(displayListMaster);

        for(ArrayList<HistoryRecord> list : twoLists){
            Collections.sort(list, (o1, o2) -> {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                //return o1.toString().compareTo(o2.toString());
                switch (sortingCondition){
                    case 0:
                        return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
                    case 1:
                        return Float.compare(o1.getPaymentAmount(), o2.getPaymentAmount());
                    case 2:
                        return o1.getPaymentDate().compareTo(o2.getPaymentDate());
                    case 3:
                        return o2.getPaymentDate().compareTo(o1.getPaymentDate());
                    case 4:
                        return Boolean.compare(o1.isCreditor(), o2.isCreditor());
                    default:
                        return 0;
                }
            });
        }

        twoLists = null;
        adapter.notifyDataSetChanged();
        //counter and toast
        switch(sortingCondition){
            case 0:
                sortingCondition++;
                Toast.makeText(getContext(), "Sorted by Name", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                sortingCondition++;
                Toast.makeText(getContext(), "Sorted by Amount", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                sortingCondition++;
                Toast.makeText(getContext(), "Sorted by Date Ascending", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                sortingCondition++;
                Toast.makeText(getContext(), "Sorted by Date Descending", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                sortingCondition= 0;
                Toast.makeText(getContext(), "Sorted by Payment type", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void AddRecord() {

    }
}
