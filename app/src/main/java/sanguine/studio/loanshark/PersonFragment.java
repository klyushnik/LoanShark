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
public class PersonFragment extends Fragment implements
        Interfaces.IOnPersonClickListener,
        Interfaces.IChildFragmentOperations,
        Interfaces.INewPersonListener{

    //two lists for search function
    ArrayList<PersonRecord> displayList;
    ArrayList<PersonRecord> displayListMaster;

    RecyclerView recyclerView;
    PersonRecordAdapter adapter;
    TextView statusTextView;

    private int sortingCondition = 0;

    public PersonFragment() {
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
        adapter = new PersonRecordAdapter(displayList, getContext(), this);

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
                                new DeletePersonRecord().execute(position);
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

    void populate(){
        //fetch records from the db
        new GetPersonRecords().execute();
    }

    private class GetPersonRecords extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            displayList.clear();
            displayListMaster.clear();
            List<TablePerson> personList;
            personList = MainActivity.mainDatabase.personDao().getPersons();
            for(TablePerson t : personList){
                PersonRecord personRecord = new PersonRecord.PersonRecordBuilder()
                        .setFirstName(t.getPer_fname())
                        .setLastName(t.getPer_lname())
                        .setPer_id(t.getPer_id())
                        .setPer_risk_level(t.getPer_risk_level())
                        .setPhoneNumbers(new ArrayList<>())
                        .createPersonRecord();
                personRecord.phoneNumbers.addAll(MainActivity.mainDatabase.personDao().getPhoneNumbersFromPerId(t.getPer_id()));
                displayList.add(personRecord);
            }
            displayListMaster.addAll(displayList);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer i){
            adapter.notifyDataSetChanged();
            statusTextView.setText(displayList.size() + " contact(s)");
        }
    }

    private class DeletePersonRecord extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            int position = integers[0];

            TablePerson tablePerson = new TablePerson();
            tablePerson.setPer_id(displayList.get(position).getPer_id());
            MainActivity.mainDatabase.personDao().deletePerson(tablePerson);

            displayList.remove(position);
            displayListMaster.remove(position);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer i){
            adapter.notifyDataSetChanged();
            statusTextView.setText(displayList.size() + " contact(s)");
        }
    }

    @Override
    public void onPersonClick(PersonRecord record) {
        ContactInfoDialogFragment fragment = new ContactInfoDialogFragment();
        FragmentManager manager = getFragmentManager();
        fragment.SetPersonRecord(record);
        fragment.setListener(this);
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
            for(PersonRecord record : displayListMaster){
                if(record.toString().toLowerCase().contains(searchString)){
                    displayList.add(record);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void Sort() {
        ArrayList<ArrayList<PersonRecord>> twoLists = new ArrayList<>();
        twoLists.add(displayList);
        twoLists.add(displayListMaster);

        for(ArrayList<PersonRecord> list : twoLists){
            Collections.sort(list, (o1, o2) -> {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                //return o1.toString().compareTo(o2.toString());
                switch (sortingCondition){
                    case 0:
                        return o1.getFirstName().toLowerCase().compareTo(o2.getFirstName().toLowerCase());
                    case 1:
                        return o1.getLastName().toLowerCase().compareTo(o2.getLastName().toLowerCase());
                    case 2:
                        return Integer.compare(o2.getPer_risk_level(), o1.getPer_risk_level());
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
                Toast.makeText(getContext(), "Sorted by First Name", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                sortingCondition++;
                Toast.makeText(getContext(), "Sorted by Last Name", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                sortingCondition = 0;
                Toast.makeText(getContext(), "Sorted by Rating", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void AddRecord() {
        FragmentManager manager = getFragmentManager();
        AddEditPersonFragment addEditPersonFragment = new AddEditPersonFragment();
        addEditPersonFragment.setNewPersonListener(this);
        addEditPersonFragment.show(manager, "");
    }

    @Override
    public void RefreshPersonList(PersonRecord... record) {
        populate();
    }
}
