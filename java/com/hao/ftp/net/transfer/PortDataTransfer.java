package com.hao.ftp.net.transfer;

import com.hao.ftp.net.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PortDataTransfer implements DataTransfer{
    private int port;
    private String ip;
    private Socket socket;
    private Response response;
    public PortDataTransfer(String ip,int port) throws IOException {
        this.ip=ip;
        this.port=port;
        connect();
    }
    @Override
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    protected void connect() throws IOException {
        socket=new Socket(ip,port);
    }
}
