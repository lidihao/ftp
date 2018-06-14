package com.hao.ftp.entity;

public class SocketConfig {
    private int inputBuffSize=1024;
    private int outputBuffSize=1024;
    private int backlog=100;
    public int getInputBuffSize() {
        return inputBuffSize;
    }

    public void setInputBuffSize(int inputBuffSize) {
        this.inputBuffSize = inputBuffSize;
    }

    public int getOutputBuffSize() {
        return outputBuffSize;
    }

    public void setOutputBuffSize(int outputBuffSize) {
        this.outputBuffSize = outputBuffSize;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    @Override
    public String toString() {
        return "SocketConfig{" +
                "inputBuffSize=" + inputBuffSize +
                ", outputBuffSize=" + outputBuffSize +
                '}';
    }
}
