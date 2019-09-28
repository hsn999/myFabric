package com.myfabric.beans.bo;

import java.util.List;

public class BlockTxInfo {
    private String blockNumber;
    private String channelId;
    private String channelName;
    private String dataHash;
    private String previousHash;
    private String envelopCount;


    private List<BlockEnvelop> envelops;


    private String key;
    private String proposalStatus;
    private String argCount;
    private String signature1;
    private String count;
    private String status;

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String dataHash) {
        this.dataHash = dataHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getEnvelopCount() {
        return envelopCount;
    }

    public void setEnvelopCount(String envelopCount) {
        this.envelopCount = envelopCount;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProposalStatus() {
        return proposalStatus;
    }

    public void setProposalStatus(String proposalStatus) {
        this.proposalStatus = proposalStatus;
    }

    public String getArgCount() {
        return argCount;
    }

    public void setArgCount(String argCount) {
        this.argCount = argCount;
    }

    public String getSignature1() {
        return signature1;
    }

    public void setSignature1(String signature1) {
        this.signature1 = signature1;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BlockEnvelop> getEnvelops() {
        return envelops;
    }

    public void setEnvelops(List<BlockEnvelop> envelops) {
        this.envelops = envelops;
    }
}
