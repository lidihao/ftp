package com.hao.ftp.entity;

public class UserInfo {
    public enum TransferType{
        ASCII,BINARY
    }
    public enum TransferMode{
        PORT("port"),PAS("pas");
        String val;

        TransferMode(String val) {
            this.val=val;
        }
        String getVal(){
            return val;
        }
    }
    private boolean firstAccept;
    private String username;
    private String password;
    private String workDir="/";
    private TransferType transferType=TransferType.ASCII;
    private TransferMode transferMode;
    private int dataPort;
    private String ip;
    public boolean isFirstAccept() {
        return firstAccept;
    }

    public void setFirstAccept(boolean firstAccept) {
        this.firstAccept = firstAccept;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public TransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "firstAccept=" + firstAccept +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", workDir='" + workDir + '\'' +
                ", transferType=" + transferType +
                '}';
    }
}
