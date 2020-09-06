package sanguine.studio.loanshark;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PersonDao {

    @Insert
    void addPerson(TablePerson person);
    @Update
    void updatePerson(TablePerson person);
    @Delete
    void deletePerson(TablePerson person);

    @Insert
    void addPhoneNumber(TablePhoneNumber... phoneNumberTable);
    @Update
    void updatePhoneNumber(TablePhoneNumber... phoneNumberTable);
    @Delete
    void deletePhoneNumber(TablePhoneNumber... phoneNumberTable);

    @Insert
    void addRecord(TableMainRecord record);
    @Update
    void updateRecord(TableMainRecord record);
    @Delete
    void deleteRecord(TableMainRecord record);

    @Insert
    void addNonMonetaryItems(TableNonMonetaryItems... items);
    @Update
    void updateNonMonetaryItems(TableNonMonetaryItems... items);
    @Delete
    void deleteNonMonetaryItems(TableNonMonetaryItems... items);

    @Insert
    void addHistoryRecord(TableHistory... items);
    @Delete
    void deleteHistoryRecord(TableHistory... items);


    @Query("SELECT phone_number FROM phoneNumbers WHERE per_id = (SELECT fk_per_id FROM records WHERE rec_id = :recordId);")
    List<String> getPhoneNumbersFromRecId(int recordId);

    @Query("SELECT phone_number FROM phoneNumbers WHERE per_id = :per_id;")
    List<String> getPhoneNumbersFromPerId(int per_id);

    @Query("DELETE FROM phoneNumbers WHERE per_id = :per_id;")
    void deletePhoneNumbersById(int per_id);

    @Query("SELECT * FROM person;")
    List<TablePerson> getPersons();

    @Query("SELECT * FROM person WHERE per_id = :person_id;")
    TablePerson getSinglePerson(int person_id);

    @Query("SELECT max(per_id) FROM person;")
    int getLastInsertedPersonID();

    @Query("SELECT max(rec_id) FROM records;")
    int getLastInsertedRecordId();

    @Query("SELECT * FROM records WHERE isCreditor = 0;")
    List<TableMainRecord> getBorrowerRecords();

    @Query("SELECT * FROM records WHERE isCreditor = 1;")
    List<TableMainRecord> getCreditorRecords();

    @Query("SELECT itemDescription FROM nonMonetaryItems WHERE fk_rec_id = :record_id;")
    List<String> getNonMonetaryItems(int record_id);

    @Query("DELETE FROM nonMonetaryItems WHERE fk_rec_id = :recordId;")
    void clearNonMonetaryItems(int recordId);

    @Query("SELECT sum(debtAmount) FROM records WHERE fk_per_id = :per_id AND isCreditor = 0;")
    float getTotalBorrowerDebt(int per_id);

    @Query("SELECT sum(debtAmount) FROM records WHERE fk_per_id = :per_id AND isCreditor = 1;")
    float getTotalCreditorDebt(int per_id);

    @Query("SELECT * FROM history;")
    List<TableHistory> getHistoryRecords();

    @Query("SELECT sum(payment_amount) FROM history WHERE rec_id = :rec_id;")
    float getTotalPayments(int rec_id);

    @Query("SELECT * FROM history WHERE rec_id = :rec_id;")
    List<TableHistory> getRecordTransactions(int rec_id);

    @Query("SELECT * FROM history WHERE fk_per_id = :fk_per_id;")
    List<TableHistory> getPersonTransactions(int fk_per_id);
}
