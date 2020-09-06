package sanguine.studio.loanshark;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class PersonRecordAdapter extends RecyclerView.Adapter<PersonRecordAdapter.PersonRecordHolder> {
    private ArrayList<PersonRecord> contacts;
    Context mContext;
    Interfaces.IOnPersonClickListener listener;

    public PersonRecordAdapter(ArrayList<PersonRecord> contacts, Context context, Interfaces.IOnPersonClickListener listener){
        this.contacts = contacts;
        mContext = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public PersonRecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_person, parent, false);
        return new PersonRecordHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonRecordHolder holder, int position) {
        final PersonRecord record = contacts.get(position);
        String name = record.toString();
        holder.name.setText(name);
        try {
            holder.phoneNumber.setText(record.getPhoneNumbers().get(0));
            holder.additionalPhoneNumbersText.setText(
                    record.getPhoneNumbers().size() > 1
                            ? "+" + (record.getPhoneNumbers().size() - 1) + " others"
                            : "");
        }catch (Exception e){
            Log.e("PRA.onBindViewHolder:", "PersonRecordAdapter.onBindViewHolder: Empty phone numbers list!");
            holder.phoneNumber.setText("INVALID");
        }
        holder.ratingBar.setRating(record.getPer_risk_level());
        holder.riskLevel.setText(name.substring(0,1).toUpperCase());
        switch (record.getPer_risk_level()){
            case 1:
                holder.riskLevel.setBackgroundTintList(mContext.getColorStateList(R.color.risk_1));
                break;
            case 2:
                holder.riskLevel.setBackgroundTintList(mContext.getColorStateList(R.color.risk_2));
                break;
            case 3:
                holder.riskLevel.setBackgroundTintList(mContext.getColorStateList(R.color.risk_3));
                break;
            case 4:
                holder.riskLevel.setBackgroundTintList(mContext.getColorStateList(R.color.risk_4));
                break;
            case 5:
            default:
                holder.riskLevel.setBackgroundTintList(mContext.getColorStateList(R.color.risk_5));
                break;
        }

        holder.rootLayout.setOnClickListener( view -> listener.onPersonClick(record));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class PersonRecordHolder extends RecyclerView.ViewHolder{
        TextView name, phoneNumber, additionalPhoneNumbersText, riskLevel;
        RatingBar ratingBar;
        ConstraintLayout rootLayout;
        public PersonRecordHolder(View view){
            super(view);
            name = view.findViewById(R.id.listPerson_nameTextView);
            phoneNumber = view.findViewById(R.id.listPerson_phoneNumber);
            additionalPhoneNumbersText = view.findViewById(R.id.listPerson_othersText);
            riskLevel = view.findViewById(R.id.listPerson_riskLevelTextView);
            ratingBar = view.findViewById(R.id.listPerson_rating);
            rootLayout = view.findViewById(R.id.listPerson_rootLayout);
        }
    }

}
