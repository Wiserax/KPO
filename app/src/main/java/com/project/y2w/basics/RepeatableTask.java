package com.project.y2w.basics;

public class RepeatableTask {

    private int childHash;
    private int parentHash;
    private int period;
    int hashKey;

    public RepeatableTask(int childHash, int parentHash, int period) {
        this.childHash = childHash;
        this.parentHash = parentHash;
        this.period = period;
        this.hashKey = hashCode();
    }

    public RepeatableTask() {
        this.hashKey = hashCode();
    }

    public int getChildHash() {
        return childHash;
    }

    public void setChildHash(int childHash) {
        this.childHash = childHash;
    }

    public int getParentHash() {
        return parentHash;
    }

    public void setParentHash(int parentHash) {
        this.parentHash = parentHash;
    }

    public int getHashKey() {
        return hashKey;
    }

    public void setHashKey(int hashKey) {
        this.hashKey = hashKey;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
