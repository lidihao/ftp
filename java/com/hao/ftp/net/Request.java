package com.hao.ftp.net;

import com.hao.ftp.entity.UserInfo;
import com.hao.ftp.net.transfer.DataTransfer;
import com.hao.ftp.net.transfer.PasDataTransfer;
import com.hao.ftp.net.transfer.PortDataTransfer;

import java.io.IOException;

public class Request {
    private String command;
    private String reqMsg;
    private UserInfo userInfo;
    private Response response;
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public DataTransfer createDataTransfer() throws IOException {
        DataTransfer dataTransfer=null;
        if (userInfo.getTransferMode().equals(UserInfo.TransferMode.PAS)){
            dataTransfer=new PasDataTransfer();
        }
        if(userInfo.getTransferMode().equals(UserInfo.TransferMode.PORT)){
            dataTransfer=new PortDataTransfer(userInfo.getIp(),userInfo.getDataPort());
        }
        if(dataTransfer!=null)
            dataTransfer.setResponse(response);
        return dataTransfer;
    }

    @Override
    public String toString() {
        return "Request{" +
                "command='" + command + '\'' +
                ", reqMsg='" + reqMsg + '\'' +
                ", userInfo=" + userInfo +
                ", response=" + response +
                '}';
    }
}
