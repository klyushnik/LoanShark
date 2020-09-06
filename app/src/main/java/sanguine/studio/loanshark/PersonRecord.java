package sanguine.studio.loanshark;

import java.util.ArrayList;

public class PersonRecord {
    private String firstName;
    private String lastName;
    public ArrayList<String> phoneNumbers;
    private int per_risk_level;
    private int per_id;


    PersonRecord(
            String firstName,
            String lastName,
            int per_risk_level,
            int per_id,
            ArrayList<String> phoneNumbers)
    {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPer_id(per_id);
        this.setPer_risk_level(per_risk_level);
        this.setPhoneNumbers(phoneNumbers);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
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

    @Override
    public String toString(){
        return firstName + " " + lastName;
    }


    public static class PersonRecordBuilder {
        private String firstName;
        private String lastName;
        private int per_risk_level;
        private int per_id;
        private ArrayList<String> phoneNumbers;

        public PersonRecordBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public PersonRecordBuilder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public PersonRecordBuilder setPer_risk_level(int per_risk_level) {
            this.per_risk_level = per_risk_level;
            return this;
        }

        public PersonRecordBuilder setPer_id(int per_id) {
            this.per_id = per_id;
            return this;
        }

        public PersonRecordBuilder setPhoneNumbers(ArrayList<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        public PersonRecord createPersonRecord() {
            return new PersonRecord(firstName, lastName, per_risk_level, per_id, phoneNumbers);
        }
    }
}
