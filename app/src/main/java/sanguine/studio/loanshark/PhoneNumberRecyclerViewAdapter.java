package sanguine.studio.loanshark;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PhoneNumberRecyclerViewAdapter extends RecyclerView.Adapter<PhoneNumberRecyclerViewAdapter.PhoneNumberHolder>{
    private ArrayList<String> numbers;
    Context mContext;

    public PhoneNumberRecyclerViewAdapter(ArrayList<String> phoneNumbers, Context context){
        numbers = phoneNumbers;
        mContext = context;
    }

    @NonNull
    @Override
    public PhoneNumberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_number_listview_item, parent, false);
        return new PhoneNumberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneNumberHolder holder, int position) {
        final String number = numbers.get(position);
        holder.phoneNumberText.setText(number);
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    //view holder
    public class PhoneNumberHolder extends RecyclerView.ViewHolder {
        TextView phoneNumberText;
        public PhoneNumberHolder(View itemView) {
            super(itemView);
            phoneNumberText = itemView.findViewById(R.id.item_phoneNumberTextView);
        }
    }
}
