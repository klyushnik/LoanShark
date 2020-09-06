package sanguine.studio.loanshark;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName="history", foreignKeys = {
        @ForeignKey(entity = TablePerson.class,
                parentColumns = {"per_id"},
                childColumns = {"fk_per_id"},
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        indices = {@Index("fk_per_id")})

public class TableHistory {
    @PrimaryKey(autoGenerate = true)
    private int hist_id;

    //fk person id
    private int fk_per_id; //deletes record if person is deleted

    private int rec_id; //used for calculations

    private float payment_amount;

    private int is_creditor; //display purposes

    private String payment_date; //display purposes

    private int is_monetary; //if so, just display "nonmonetary debt" or something

    private int is_final; //display "paid off" in history

    private String debt_reason;

    public int getHist_id() {
        return hist_id;
    }

    public void setHist_id(int hist_id) {
        this.hist_id = hist_id;
    }

    public int getFk_per_id() {
        return fk_per_id;
    }

    public void setFk_per_id(int fk_per_id) {
        this.fk_per_id = fk_per_id;
    }

    public int getRec_id() {
        return rec_id;
    }

    public void setRec_id(int rec_id) {
        this.rec_id = rec_id;
    }

    public float getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(float payment_amount) {
        this.payment_amount = payment_amount;
    }

    public int getIs_creditor() {
        return is_creditor;
    }

    public void setIs_creditor(int is_creditor) {
        this.is_creditor = is_creditor;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public int getIs_monetary() {
        return is_monetary;
    }

    public void setIs_monetary(int is_monetary) {
        this.is_monetary = is_monetary;
    }

    public int getIs_final() {
        return is_final;
    }

    public void setIs_final(int is_final) {
        this.is_final = is_final;
    }

    public String getDebt_reason() {
        return debt_reason;
    }

    public void setDebt_reason(String debt_reason) {
        this.debt_reason = debt_reason;
    }
}
