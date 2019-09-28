package com.myfabric.beans.bo;

public class BlockEnvelop {

    private String chaincode;
    private String chaincodeName;
    private String isValid;
    private String transactionActionInfoCount;
    private String transactionID;
    private String timestamp;
    private String version;

    public String getChaincode() {
        return chaincode;
    }

    public void setChaincode(String chaincode) {
        this.chaincode = chaincode;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getTransactionActionInfoCount() {
        return transactionActionInfoCount;
    }

    public void setTransactionActionInfoCount(String transactionActionInfoCount) {
        this.transactionActionInfoCount = transactionActionInfoCount;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
