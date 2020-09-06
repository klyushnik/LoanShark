package sanguine.studio.loanshark;

public final class Interfaces {
    public enum SortingCondition {
        NAME_ASC,
        NAME_DESC,
        AMOUNT_ASC,
        AMOUNT_DESC,
        DUE_DATE_ASC,
        DUE_DATE_DESC
    }

    public interface IOnRecordEditListener{
        void updateRecordInfo(MainRecord... record);
    }

    public interface INewPersonListener{
        void RefreshPersonList(PersonRecord... record);
    }

    public interface IOnDatePickedListener{
        void onDatePicked(String date);
    }

    public interface IChildFragmentOperations{

        void Search(String searchString);
        void Sort();
        void AddRecord();

    }

    public interface IOnDataClickListener {
        void onDataClick(MainRecord record);
    }

    public interface IOnPersonClickListener {
        void onPersonClick(PersonRecord record);
    }

    public interface IOnHistoryClickListener{
        void onHistoryClick(HistoryRecord record);
    }

    public interface IFragmentInteractionListener{ //this is for manipulating the recycler views inside fragments
        void AddRecord();
        void RemoveRecord();
        void ReloadListView();
        void SortListView();
        void FilterListView(String searchString);
    }

    public interface IOnPaymentListener{
        void onPaymentEntered(float paymentAmount);
    }
}
