package sanguine.studio.loanshark;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {TablePerson.class, TablePhoneNumber.class, TableMainRecord.class, TableNonMonetaryItems.class, TableHistory.class}, version = 1)
public abstract class MainDatabase extends RoomDatabase {

    public abstract PersonDao personDao();

}
