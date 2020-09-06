package sanguine.studio.loanshark;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "nonMonetaryItems", foreignKeys = {
        @ForeignKey(entity = TableMainRecord.class,
                parentColumns = {"rec_id"},
                childColumns = {"fk_rec_id"},
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        indices = {@Index("fk_rec_id")})
public class TableNonMonetaryItems {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int fk_rec_id;

    private String itemDescription;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFk_rec_id() {
        return fk_rec_id;
    }

    public void setFk_rec_id(int fk_rec_id) {
        this.fk_rec_id = fk_rec_id;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
