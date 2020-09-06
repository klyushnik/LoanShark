package sanguine.studio.loanshark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PhoneNumberListViewAdapter extends ArrayAdapter<String> {
    public PhoneNumberListViewAdapter(Context context, List<String> values){
        super(context, 0, values);
    }

    public View getView(int index, View view, ViewGroup viewGroup){
        String item = getItem(index);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.phone_number_listview_item, viewGroup, false);
        }

        TextView textView = view.findViewById(R.id.item_phoneNumberTextView);
        textView.setText(item);

        return view;
    }
}
