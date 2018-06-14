package com.hao.ftp.net.transfer;

import com.hao.ftp.net.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PasDataTransfer implements DataTransfer {
    private ServerSocket serverSocket;
    private int DEFAULT_PORT=20;
    private Socket socket;
    private Response response;
    public PasDataTransfer() throws IOException {
        bind();
        listen();
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
        serverSocket.close();
    }

    @Override
    public Response getResponse() {
        return null;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    protected void bind() throws IOException {
        serverSocket=new ServerSocket(DEFAULT_PORT);
    }
    protected void listen() throws IOException {
        socket=serverSocket.accept();
    }

}
