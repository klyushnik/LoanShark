package sanguine.studio.loanshark;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "person")
public class TablePerson {

    @PrimaryKey(autoGenerate = true)
    private int per_id;

    private String per_fname;
    private String per_lname;
    private int per_risk_level;

    @Override
    public String toString(){
        return getPer_fname() + " " + getPer_lname();
    }

    public String getPer_fname() {
        return per_fname;
    }

    public void setPer_fname(String per_fname) {
        this.per_fname = per_fname;
    }

    public String getPer_lname() {
        return per_lname;
    }

    public void setPer_lname(String per_lname) {
        this.per_lname = per_lname;
    }

    public int getPer_risk_level() {
        return per_risk_level;
    }

    public void setPer_risk_level(int per_risk_level) {
        this.per_risk_level = per_risk_level;
    }

    public int getPer_id() {
        return per_id;
    }

    public void setPer_id(int per_id) {
        this.per_id = per_id;
    }
}
