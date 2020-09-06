package sanguine.studio.loanshark;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainRecordAdapter extends RecyclerView.Adapter<MainRecordAdapter.RecordHolder> {
    private ArrayList<MainRecord> testData;
    Context mContext;

    private Interfaces.IOnDataClickListener mOnDataClickListener;

    public class RecordHolder extends RecyclerView.ViewHolder{
        TextView nameTextView, riskLevelTextView, dueDateTextView, timeLabelTextView;
        TextView debtAmtTextView, percentageTextView, interestTypeTextView; //monetary
        TextView itemDescTextView, moreItemCountTextView; //nonmonetary

        LinearLayout monetaryLinearLayout, nonMonetaryLinearLayout;
        ConstraintLayout rootLayout;


        public RecordHolder(View view){
            super(view);

            rootLayout = view.findViewById(R.id.listItemRootLayout);

            monetaryLinearLayout = view.findViewById(R.id.monetaryLinearLayout);
            nonMonetaryLinearLayout = view.findViewById(R.id.nonMonetaryLinearLayout);

            nameTextView = view.findViewById(R.id.listItemNameTextView);
            riskLevelTextView = view.findViewById(R.id.listItemRiskLevelTextView);
            dueDateTextView = view.findViewById(R.id.listItemDueDateTextView);
            timeLabelTextView = view.findViewById(R.id.listItemTimeLabelTextView);

            debtAmtTextView = view.findViewById(R.id.listItemMonAmountTextView);
            percentageTextView = view.findViewById(R.id.listItemPercentTextView);
            interestTypeTextView = view.findViewById(R.id.listItemInterestTypeTextView);

            itemDescTextView = view.findViewById(R.id.listItemItemDescTextView);
            moreItemCountTextView = view.findViewById(R.id.listItemMoreItemCountTextView);
        }

    }

    public MainRecordAdapter(ArrayList<MainRecord> testData, Context context, Interfaces.IOnDataClickListener listener){
        this.testData = testData;
        mContext = context;
        mOnDataClickListener = listener;
    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recordView;
        recordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_debt, parent, false);

        return new RecordHolder((recordView));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordHolder holder, final int position) {
        final MainRecord record = testData.get(position);

        holder.nameTextView.setText(record.getPersonName());
        holder.riskLevelTextView.setText(record.getPersonName().substring(0,1).toUpperCase());
        switch (record.getRiskLevel()){
            case 1:
                holder.riskLevelTextView.setBackgroundTintList(mContext.getColorStateList(R.color.risk_1));
                break;
            case 2:
                holder.riskLevelTextView.setBackgroundTintList(mContext.getColorStateList(R.color.risk_2));
                break;
            case 3:
                holder.riskLevelTextView.setBackgroundTintList(mContext.getColorStateList(R.color.risk_3));
                break;
            case 4:
                holder.riskLevelTextView.setBackgroundTintList(mContext.getColorStateList(R.color.risk_4));
                break;
            case 5:
                holder.riskLevelTextView.setBackgroundTintList(mContext.getColorStateList(R.color.risk_5));
                break;
            default:
                holder.riskLevelTextView.setBackgroundTintList(mContext.getColorStateList(R.color.risk_5));
        }
        holder.dueDateTextView.setText(record.getDueDate());
        if(!record.getDueDate().startsWith("I") && Util.GetTodaysDate().compareTo(record.getDueDate()) > 0){
            holder.timeLabelTextView.setVisibility(View.VISIBLE);
            holder.riskLevelTextView.setBackgroundTintList(mContext.getColorStateList(R.color.risk_1));
        }else{
            holder.timeLabelTextView.setVisibility(View.INVISIBLE);
        }

        if(record.isMonetary()){
            SharedPreferences preferences = mContext.getSharedPreferences("appSettings", Context.MODE_PRIVATE);
            holder.nonMonetaryLinearLayout.setVisibility(View.GONE);
            if(preferences.getBoolean("showWithInterest", false)){
                float f = Util.GetTotalDebt(record.getDebtAmount(),
                        record.getInterestRate(),
                        0, record.getInterestType(),
                        record.getInitialDate(),
                        false);
                holder.debtAmtTextView.setText(String.format("$%.2f",f));
            }else{
                holder.debtAmtTextView.setText(String.format("$%.2f",record.getDebtAmount()));
            }
            holder.percentageTextView.setText(record.getInterestRate() + "%");
            switch (record.getInterestType()){ //{"w", "bw", "m", "y", "n"};
                case "w":
                    holder.interestTypeTextView.setText("Weekly");
                    break;
                case "bw":
                    holder.interestTypeTextView.setText("Bi-Weekly");
                    break;
                case "m":
                    holder.interestTypeTextView.setText("Monthly");
                    break;
                case "y":
                    holder.interestTypeTextView.setText("Annual");
                    break;
                case "n":
                    holder.interestTypeTextView.setText("No interest");
                    holder.percentageTextView.setText("");
                    break;
                default:
                    holder.interestTypeTextView.setText("No interest");
            }
        } else {
            holder.monetaryLinearLayout.setVisibility(View.GONE);
            //in case someone adds a nonmonetary record with an empty list
            try{
                holder.itemDescTextView.setText(record.getNonMonetaryItems().get(0));
                holder.moreItemCountTextView.setText("+ " + (record.getNonMonetaryItems().size() - 1) + " item(s)");
            } catch (Exception e){
                holder.itemDescTextView.setText("INVALID ITEM");
                holder.moreItemCountTextView.setText("");
            }

        }
        holder.rootLayout.setOnClickListener(v -> mOnDataClickListener.onDataClick(record));

    }

    @Override
    public int getItemCount() {
        return testData.size();
    }

    @Override
    public int getItemViewType(int position){
        return testData.get(position).isMonetary() ? 1 : 0;
    }


}
