package sanguine.studio.loanshark;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "phoneNumbers", foreignKeys = {
        @ForeignKey(entity = TablePerson.class,
                parentColumns = {"per_id"},
                childColumns = {"per_id"},
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        indices = {@Index("per_id")})
public class TablePhoneNumber {
    //this is the table for storing phone numbers

    @PrimaryKey (autoGenerate = true)
    private int pn_id;

    private int per_id;

    private String phone_number;

    public int getPn_id() {
        return pn_id;
    }

    public void setPn_id(int pn_id) {
        this.pn_id = pn_id;
    }

    public int getPer_id() {
        return per_id;
    }

    public void setPer_id(int per_id) {
        this.per_id = per_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
