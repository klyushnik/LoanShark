package sanguine.studio.loanshark;


import android.app.AlertDialog;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MainRecordFragment extends Fragment implements
        Interfaces.IOnDataClickListener,
        Interfaces.IChildFragmentOperations,
        Interfaces.IOnRecordEditListener {


    RecyclerView recyclerView;
    ArrayList<MainRecord> displayList;
    ArrayList<MainRecord> displayListMaster;
    MainRecordAdapter adapter;
    TextView statusTextView;
    //async workers

    int mode = 0;

    int sortingCondition = 0;

    public MainRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_borrowers, container, false);
        recyclerView = view.findViewById(R.id.borrowersRecyclerView);
        statusTextView = view.findViewById(R.id.borrowersStatusTextView);

        mode = getArguments().getInt("mode", 0);

        //if this is a MainRecord list
        displayList = new ArrayList<>();
        displayListMaster = new ArrayList<>();
        adapter = new MainRecordAdapter(displayList, getContext(), this); //we need to pass this as ondataclicklistener

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            Drawable background = new ColorDrawable(getResources().getColor(R.color.colorDialogBackground2, null));
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
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
                                new DeleteRecord().execute(position);
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
                //background.setBounds(0, itemView.getTop(),   itemView.getLeft() + (int)dX, itemView.getBottom());

                background.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        populateRecords();

        adapter.notifyDataSetChanged();


        return view;
    }

    void populateRecords(){
        new GetRecords().execute();
    }

    void updateStatus(){
        float totalAmountOwed = 0;
        for(MainRecord r : displayList){
            totalAmountOwed += r.getDebtAmount();
        }
        String status = "Total debt: " + String.format("$%.2f", totalAmountOwed);
        statusTextView.setText(status);
    }


    public void onDataClick(MainRecord record){
        RecordInfoDialogFragment fragment = new RecordInfoDialogFragment();
        FragmentManager manager = getFragmentManager();
        fragment.setMainRecord(record);
        fragment.setOnRecordEditListener(this);
        fragment.show(manager,"");
    }

    @Override
    public void Search(String searchString) {
        displayList.clear();
        if(searchString.isEmpty()){
            displayList.addAll(displayListMaster);
        }else {
            displayList.clear();
            searchString = searchString.toLowerCase();
            for(MainRecord record : displayListMaster){
                if(record.toString().toLowerCase().contains(searchString)){
                    displayList.add(record);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void Sort() {

        ArrayList<ArrayList<MainRecord>> twoLists = new ArrayList<>();
        twoLists.add(displayList);
        twoLists.add(displayListMaster);

        for(ArrayList<MainRecord> list : twoLists){
            Collections.sort(list, (o1, o2) -> {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                //return o1.toString().compareTo(o2.toString());
                switch (sortingCondition){
                    case 0:
                        return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
                    case 1:
                        return Float.compare(o2.getDebtAmount(), o1.getDebtAmount());
                    case 2:
                        return o1.getDueDate().compareTo(o2.getDueDate());
                    case 3:
                        return o1.getInitialDate().compareTo(o2.getInitialDate());
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
                Toast.makeText(getContext(), "Sorted by Due date", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                sortingCondition= 0;
                Toast.makeText(getContext(), "Sorted by Initial date", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void AddRecord() {
        AddEditRecordFragment fragment = new AddEditRecordFragment();
        FragmentManager manager = getFragmentManager();
        fragment.setIsCreditor(mode == 1);
        fragment.setOnRecordEditListener(this);
        fragment.show(manager, null);
    }

    @Override
    public void updateRecordInfo(MainRecord... record) {
        populateRecords();
    }

    private class GetRecords extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            List<TableMainRecord> tableRecordPersonList;

            if(mode == 0)
                tableRecordPersonList = MainActivity.mainDatabase.personDao().getBorrowerRecords();
            else
                tableRecordPersonList = MainActivity.mainDatabase.personDao().getCreditorRecords();

            displayList.clear();
            displayListMaster.clear();

            for (TableMainRecord rec : tableRecordPersonList) {
                TablePerson person = MainActivity.mainDatabase.personDao().getSinglePerson(rec.getFk_per_id());
                MainRecord record = new MainRecord.MainRecordBuilder()
                        .setRecordId(rec.getRec_id())
                        .setDebtAmount(rec.getDebtAmount())
                        .setDebtReason(rec.getDebtReason())
                        .setDueDate(rec.getDueDate())
                        .setInitialDate(rec.getInitialDate())
                        .setInterestRate(rec.getInterestRate())
                        .setInterestType(rec.getInterestType())
                        .setIsCreditor(rec.getIsCreditor() == 1)
                        .setIsMonetary(rec.getIsMonetary() == 1)
                        .setNonMonetaryItems(new ArrayList<String>())
                        .setPersonName(person.toString())
                        .setRiskLevel(person.getPer_risk_level())
                        .setPersonId(rec.getFk_per_id())
                        .createMainRecord();
                if (!record.isMonetary()) {
                    record.nonMonetaryItems.addAll(MainActivity.mainDatabase.personDao().getNonMonetaryItems(record.getRecordId()));
                }
                displayList.add(record);
            }
            displayListMaster.addAll(displayList);
            return "";
        }
        @Override
        protected void onPostExecute(String s){
            adapter.notifyDataSetChanged();
            updateStatus();
        }
    }

    private class DeleteRecord extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            int position = integers[0];
            TableMainRecord tableMainRecord = new TableMainRecord();
            tableMainRecord.setRec_id(displayList.get(position).getRecordId());
            MainActivity.mainDatabase.personDao().deleteRecord(tableMainRecord);
            displayList.remove(position);
            displayListMaster.remove(position);
            return position;
        }

        @Override
        protected void onPostExecute(Integer i){
            adapter.notifyDataSetChanged();
            updateStatus();
        }
    }

}
