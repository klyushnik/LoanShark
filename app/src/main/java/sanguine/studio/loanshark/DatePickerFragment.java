package sanguine.studio.loanshark;



import android.os.Bundle;
import android.app.Fragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements CalendarView.OnDateChangeListener, View.OnClickListener{
    Button okButton, cancelButton;
    CalendarView calendarView;
    private Interfaces.IOnDatePickedListener listener;
    String outDate;
    SimpleDateFormat simpleDateFormat;

    public DatePickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        calendarView = view.findViewById(R.id.datePickerCalendarView);
        okButton = view.findViewById(R.id.datePickerOkButton);
        cancelButton = view.findViewById(R.id.datePickerCancelButton);
        calendarView.setOnDateChangeListener(this);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        outDate = simpleDateFormat.format(date);


        return view;
    }


    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
       outDate = simpleDateFormat.format(calendar.getTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.datePickerOkButton:
                listener.onDatePicked(outDate);
                dismiss();
                break;
            case R.id.datePickerCancelButton:
                dismiss();
                break;
        }
    }

    public void setOnDatePickedListener(Interfaces.IOnDatePickedListener listener) {
        this.listener = listener;
    }


}
