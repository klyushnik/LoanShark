package sanguine.studio.loanshark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsBorrowedListViewAdapter extends ArrayAdapter<String> {
    boolean isEditMode;
    public ItemsBorrowedListViewAdapter(Context context, ArrayList<String> values, boolean isEditMode){
        super(context, 0, values);
        this.isEditMode = isEditMode;
    }
    public ItemsBorrowedListViewAdapter(Context context, boolean isEditMode){
        super(context, 0, new ArrayList<String>());
        this.isEditMode = isEditMode;
    }

    public View getView(int index, View view, ViewGroup viewGroup){
        String item = getItem(index);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.items_borrowed_listview_item, viewGroup, false);
        }

        TextView textView = view.findViewById(R.id.item_itemBorrowedTextView);
        textView.setText(item);

        return view;
    }
}
