/*
*The following classes are for display/UI purposes only. They only contain information needed for display, EXCEPT
*the record ID or a person ID. The IDs will be used to delete the record from the database. If a record is
* modified, the changes are immediately committed to the database and RecyclerView reload is triggered.

* MainRecord is the main class that gets displayed in the borrowers/creditors view and in the detailed info
* fragment. It can be modified via Add/Modify dialog or swiped away to delete a single record from DB.
*
* The HistoryRecord class deals with transaction history and is entirely independent from MainRecord.
* It depends on the Person table, though.
*
* The Person class deals with people that are saved from contacts or are added manually. If a person
* is deleted, all records containing the person's ID are deleted from the database (ON DELETE CASCADE),
* and RecyclerView reload is triggered. Deleted records do not exist and thus will not be loaded.
* */

package sanguine.studio.loanshark;

import java.util.ArrayList;

//TODO: restructure person info - what is necessary for display and what gets fetched from db when the info fragment shows up
//TODO: might just have a person_id, name and risk level; address and phone numbers will be fetched from db when info fragment is up

public class MainRecord {
    public MainRecord(ArrayList<String> nonMonetaryItems, String personName,
                      int riskLevel, int recordId, int personId, String interestType, String initialDate,
                      String dueDate, String debtReason, boolean isMonetary, boolean isCreditor,
                      float debtAmount, float interestRate) {
        this.nonMonetaryItems = nonMonetaryItems;
        this.personName = personName;
        this.riskLevel = riskLevel;
        this.recordId = recordId;
        this.interestType = interestType;
        this.initialDate = initialDate;
        this.dueDate = dueDate;
        this.debtReason = debtReason;
        this.isMonetary = isMonetary;
        this.isCreditor = isCreditor;
        this.debtAmount = debtAmount;
        this.interestRate = interestRate;
        this.personId = personId;
    }

    //nonmonetary
    public ArrayList<String> nonMonetaryItems; //either debt amount OR a list of non-monetary items, but not both because fuck you

    public ArrayList<String> getNonMonetaryItems() {
        return nonMonetaryItems;
    }

    public String getPersonName() {
        return personName;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getInterestType() {
        return interestType;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDebtReason() {
        return debtReason;
    }

    public boolean isMonetary() {
        return isMonetary;
    }

    public boolean isCreditor() {
        return isCreditor;
    }

    public float getDebtAmount() {
        return debtAmount;
    }

    public float getInterestRate() {
        return interestRate;
    }

    //person details
    private String personName;
    private int riskLevel;

    //record
    private int recordId;
    private int personId;
    private String interestType, initialDate, dueDate, debtReason;
    private boolean isMonetary, isCreditor;
    private float debtAmount;
    private float interestRate;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }


    public static class MainRecordBuilder{

        private ArrayList<String> nonMonetaryItems;
        private String personName;
        private int riskLevel;
        private int recordId;
        private int personId;
        private String interestType;
        private String initialDate;
        private String dueDate;
        private String debtReason;
        private boolean isMonetary;
        private boolean isCreditor;
        private float debtAmount;
        private float interestRate;

        public MainRecordBuilder setNonMonetaryItems(ArrayList<String> nonMonetaryItems) {
            this.nonMonetaryItems = nonMonetaryItems;
            return this;
        }

        public MainRecordBuilder setPersonName(String personName) {
            this.personName = personName;
            return this;
        }

        public MainRecordBuilder setRiskLevel(int riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public MainRecordBuilder setPersonId(int personId) {
            this.personId = personId;
            return this;
        }

        public MainRecordBuilder setRecordId(int recordId) {
            this.recordId = recordId;
            return this;
        }

        public MainRecordBuilder setInterestType(String interestType) {
            this.interestType = interestType;
            return this;
        }

        public MainRecordBuilder setInitialDate(String initialDate) {
            this.initialDate = initialDate;
            return this;
        }

        public MainRecordBuilder setDueDate(String dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public MainRecordBuilder setDebtReason(String debtReason) {
            this.debtReason = debtReason;
            return this;
        }

        public MainRecordBuilder setIsMonetary(boolean isMonetary) {
            this.isMonetary = isMonetary;
            return this;
        }

        public MainRecordBuilder setIsCreditor(boolean isCreditor) {
            this.isCreditor = isCreditor;
            return this;
        }

        public MainRecordBuilder setDebtAmount(float debtAmount) {
            this.debtAmount = debtAmount;
            return this;
        }

        public MainRecordBuilder setInterestRate(float interestRate) {
            this.interestRate = interestRate;
            return this;
        }


        public MainRecord createMainRecord() {
            return new MainRecord(nonMonetaryItems, personName, riskLevel, recordId, personId,
                    interestType, initialDate, dueDate, debtReason, isMonetary, isCreditor,
                    debtAmount, interestRate);
        }
    }


    public TableMainRecord generateTableMainRecord(Boolean isNewRecord){
        //this generates a TableMainRecord from existing information

        TableMainRecord record = new TableMainRecord();
        if(!isNewRecord)
            record.setRec_id(this.getRecordId());

        record.setInterestType(interestType);
        record.setIsCreditor(isCreditor ? 1 : 0);
        record.setIsMonetary(isMonetary ? 1 : 0);
        record.setFk_per_id(personId);
        record.setInterestRate(interestRate);
        record.setInitialDate(initialDate);
        record.setDueDate(dueDate);
        record.setDebtReason(debtReason);
        record.setDebtAmount(debtAmount);


        return record;
    }

    public TableNonMonetaryItems generateNonMonetaryItem(int recordId, String item){
        TableNonMonetaryItems tableNonMonetaryItems = new TableNonMonetaryItems();
        tableNonMonetaryItems.setFk_rec_id(recordId);
        tableNonMonetaryItems.setItemDescription(item);
        return tableNonMonetaryItems;
    }

    @Override
    public String toString(){
        return personName;
    }
}
