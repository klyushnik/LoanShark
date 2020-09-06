package sanguine.studio.loanshark;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterPaymentFragment extends DialogFragment {

    Interfaces.IOnPaymentListener listener;
    float finalPaymentAmount;
    Button payPartButton, payAllButton, cancelButton;
    EditText amountEditText;
    TextView owedTextView;
    Context mContext;
    String message = "This will mark debt as fully paid, create a history record, and delete the debt record. Do you want to proceed?";
    String title = "Set debt as fully paid";

    public EnterPaymentFragment() {
        // Required empty public constructor
    }

    public void setData(Interfaces.IOnPaymentListener listener, float finalPaymentAmount, Context context){
        this.listener = listener;
        this.finalPaymentAmount = finalPaymentAmount;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_payment, container, false);

        payPartButton = view.findViewById(R.id.enterpayment_payPartButton);
        payAllButton = view.findViewById(R.id.enterpayment_payAllButton);
        cancelButton = view.findViewById(R.id.enterpayment_cancelButton);
        amountEditText = view.findViewById(R.id.enterpayment_amountEditText);
        owedTextView = view.findViewById(R.id.enterpayment_owedTextView);

        owedTextView.setText("Owed on record: " + String.format("$%.2f", finalPaymentAmount));
        cancelButton.setOnClickListener(view1 -> dismiss());
        payAllButton.setOnClickListener(view1 -> {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setCancelable(true)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Pay All", ((dialogInterface, i) -> {
                        listener.onPaymentEntered(finalPaymentAmount);
                        dismiss();
                    }))
                    .create();
            alertDialog.show();
        });
        payPartButton.setOnClickListener(view1 -> {
            String amt = amountEditText.getText().toString();
            if(amt.isEmpty()){
                Toast.makeText(mContext, "Amount can't be empty", Toast.LENGTH_SHORT).show();
            }else{
                float amount = Float.valueOf(amt);
                if(Float.compare(finalPaymentAmount, amount) >=0){
                    listener.onPaymentEntered(amount);
                    dismiss();
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                            .setCancelable(true)
                            .setTitle(title)
                            .setMessage(message)
                            .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("Pay All", ((dialogInterface, i) -> {
                                listener.onPaymentEntered(finalPaymentAmount);
                                dismiss();
                            }))
                            .create();
                    alertDialog.show();
                }
            }
        });

        return view;
    }

}
