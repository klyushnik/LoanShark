package sanguine.studio.loanshark;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "records", foreignKeys = {
        @ForeignKey(entity = TablePerson.class,
                parentColumns = {"per_id"},
                childColumns = {"fk_per_id"},
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        indices = {@Index("fk_per_id")})
public class TableMainRecord {

    @PrimaryKey(autoGenerate = true)
    private int rec_id;

    //this is the person who owes you

    private int fk_per_id;

    //the following fields are straight from the MainRecord
    //the info is split to avoid repetition and assembled at runtime

    private float debtAmount;
    private int isCreditor;
    private String initialDate;
    private String dueDate;
    private String debtReason;
    private int isMonetary;
    private float interestRate;
    private String interestType;


    public int getRec_id() {
        return rec_id;
    }

    public void setRec_id(int rec_id) {
        this.rec_id = rec_id;
    }

    public float getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(float debtAmount) {
        this.debtAmount = debtAmount;
    }

    public int getIsCreditor() {
        return isCreditor;
    }

    public void setIsCreditor(int isCreditor) {
        this.isCreditor = isCreditor;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDebtReason() {
        return debtReason;
    }

    public void setDebtReason(String debtReason) {
        this.debtReason = debtReason;
    }

    public int getIsMonetary() {
        return isMonetary;
    }

    public void setIsMonetary(int isMonetary) {
        this.isMonetary = isMonetary;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public int getFk_per_id() {
        return fk_per_id;
    }

    public void setFk_per_id(int fk_per_id) {
        this.fk_per_id = fk_per_id;
    }
}
