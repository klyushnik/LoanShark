package sanguine.studio.loanshark;

public class HistoryRecord {
    private String fName;
    private String lName;
    private String debtReason;
    private String paymentDate;
    private int hist_id;
    private float paymentAmount;
    private boolean isCreditor;
    private boolean isMonetary;
    private boolean isFinal;

    public HistoryRecord(String fname,
                         String lName,
                         String debtReason,
                         String paymentDate,
                         int hist_id,
                         float paymentAmount,
                         boolean isCreditor,
                         boolean isMonetary,
                         boolean isFinal){
        this.fName = fname;
        this.lName = lName;
        this.debtReason = debtReason;
        this.paymentDate = paymentDate;
        this.hist_id = hist_id;
        this.paymentAmount = paymentAmount;
        this.isCreditor = isCreditor;
        this.isMonetary = isMonetary;
        this.isFinal = isFinal;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getDebtReason() {
        return debtReason;
    }

    public void setDebtReason(String debtReason) {
        this.debtReason = debtReason;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getHist_id() {
        return hist_id;
    }

    public void setHist_id(int hist_id) {
        this.hist_id = hist_id;
    }

    public float getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public boolean isCreditor() {
        return isCreditor;
    }

    public void setCreditor(boolean creditor) {
        isCreditor = creditor;
    }

    public boolean isMonetary() {
        return isMonetary;
    }

    public void setMonetary(boolean monetary) {
        isMonetary = monetary;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    @Override
    public String toString(){
        return fName + " " + lName;
    }

    public static class HistoryRecordBuilder{

        private String fname;
        private String lName;
        private String debtReason;
        private String paymentDate;
        private int hist_id;
        private float paymentAmount;
        private boolean isCreditor;
        private boolean isMonetary;
        private boolean isFinal;

        public HistoryRecordBuilder setFname(String fname) {
            this.fname = fname;
            return this;
        }

        public HistoryRecordBuilder setlName(String lName) {
            this.lName = lName;
            return this;
        }

        public HistoryRecordBuilder setDebtReason(String debtReason) {
            this.debtReason = debtReason;
            return this;
        }

        public HistoryRecordBuilder setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public HistoryRecordBuilder setHist_id(int hist_id) {
            this.hist_id = hist_id;
            return this;
        }

        public HistoryRecordBuilder setPaymentAmount(float paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }

        public HistoryRecordBuilder setIsCreditor(boolean isCreditor) {
            this.isCreditor = isCreditor;
            return this;
        }

        public HistoryRecordBuilder setIsMonetary(boolean isMonetary) {
            this.isMonetary = isMonetary;
            return this;
        }

        public HistoryRecordBuilder setIsFinal(boolean isFinal) {
            this.isFinal = isFinal;
            return this;
        }

        public HistoryRecord createHistoryRecord() {
            return new HistoryRecord(fname, lName, debtReason, paymentDate, hist_id, paymentAmount, isCreditor, isMonetary, isFinal);
        }
    }
}
