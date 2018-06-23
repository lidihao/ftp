package com.hao.ftp.entity;

import com.hao.ftp.buff.InputBuffer;
import com.hao.ftp.buff.NioInputBuffer;
import com.hao.ftp.buff.NioOutputBuffer;
import com.hao.ftp.buff.OutputBuffer;
import com.hao.ftp.net.NioConnector;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketAttachment {
    private UserInfo userInfo;
    private InputBuffer inputBuffer;
    private OutputBuffer outputBuffer;
    private SocketConfig socketConfig;
    private NioConnector.Poller poller;
    private SelectionKey key;

    public SocketAttachment(SocketConfig config, NioConnector.Poller poller){
        userInfo=new UserInfo();
        this.poller=poller;
        socketConfig=config;
    }
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public InputBuffer getInputBuffer() {
        return inputBuffer;
    }

    public void setInputBuffer(InputBuffer inputBuffer) {
        this.inputBuffer = inputBuffer;
    }

    public OutputBuffer getOutputBuffer() {
        return outputBuffer;
    }

    public void setOutputBuffer(OutputBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    public SocketConfig getSocketConfig() {
        return socketConfig;
    }

    public void setSocketConfig(SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
    }

    public NioConnector.Poller getPoller() {
        return poller;
    }

    public void setPoller(NioConnector.Poller poller) {
        this.poller = poller;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "SocketAttachment{" +
                "userInfo=" + userInfo +
                ", inputBuffer=" + inputBuffer +
                ", outputBuffer=" + outputBuffer +
                ", socketConfig=" + socketConfig +
                ", poller=" + poller +
                ", key=" + key +
                '}';
    }

    public void init(){
        if(inputBuffer==null)
            inputBuffer=new NioInputBuffer(socketConfig.getInputBuffSize(), (SocketChannel) key.channel());
        else inputBuffer.setSc((SocketChannel) key.channel());
        if(outputBuffer==null)
            outputBuffer=new NioOutputBuffer(socketConfig.getOutputBuffSize(), (SocketChannel) key.channel());
        else outputBuffer.setSc((SocketChannel) key.channel());
    }

    public void reset(){
        userInfo=new UserInfo();
        if(inputBuffer!=null) inputBuffer.reset();
        if(outputBuffer!=null) outputBuffer.reset();
        socketConfig=null;
        poller=null;
        key=null;
    }

}
