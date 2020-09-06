package sanguine.studio.loanshark;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryRecordAdapter extends RecyclerView.Adapter<HistoryRecordAdapter.HistoryRecordHolder>{
    ArrayList<HistoryRecord> records;
    Context mContext;


    public HistoryRecordAdapter(ArrayList<HistoryRecord> records, Context context){
        this.records = records;
        mContext = context;
    }

    public class HistoryRecordHolder extends RecyclerView.ViewHolder{
        TextView nameTextView, amountTextView, dateTextView, finalTextView, reasonTextView;
        ImageView debtTypeImageView;
        ConstraintLayout rootLayout;
        public HistoryRecordHolder(View view){
            super(view);
            nameTextView = view.findViewById(R.id.hist_nameTextView);
            amountTextView = view.findViewById(R.id.hist_debtAmountTextView);
            dateTextView = view.findViewById(R.id.hist_dateTextView);
            finalTextView = view.findViewById(R.id.hist_finalTextView);
            rootLayout = view.findViewById(R.id.hist_rootLayout);
            debtTypeImageView = view.findViewById(R.id.hist_debtTypeImageView);
            reasonTextView = view.findViewById(R.id.hist_reasonTextView);
        }
    }

    @NonNull
    @Override
    public HistoryRecordAdapter.HistoryRecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_history, parent, false);
        return new HistoryRecordAdapter.HistoryRecordHolder(v);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecordHolder holder, int position) {
        final HistoryRecord record = records.get(position);
        holder.nameTextView.setText(record.getfName() + " " + record.getlName());
        holder.amountTextView.setText(record.isMonetary() ? String.format("$%.2f", record.getPaymentAmount()) : mContext.getString(R.string.text_nonmonetary));
        if(record.isFinal() || !record.isMonetary()){
            holder.finalTextView.setVisibility(View.VISIBLE);
        }else{
            holder.finalTextView.setVisibility(View.GONE);
        }
        holder.dateTextView.setText(record.getPaymentDate());
        if(record.isCreditor()){
            holder.debtTypeImageView.setImageResource(R.drawable.ic_currency_usd_off_white_48dp);
            holder.debtTypeImageView.setColorFilter(mContext.getColor(R.color.colorRed));
        }else{
            holder.debtTypeImageView.setImageResource(R.drawable.ic_currency_usd_white_48dp);
            holder.debtTypeImageView.setColorFilter(mContext.getColor(R.color.colorGreen));
        }
        holder.reasonTextView.setText(record.getDebtReason().isEmpty() ? "N/A" : record.getDebtReason());
    }
}
