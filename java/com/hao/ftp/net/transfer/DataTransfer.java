package com.hao.ftp.net.transfer;

import com.hao.ftp.net.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface DataTransfer {
    OutputStream getOutputStream() throws IOException;
    InputStream getInputStream() throws IOException;
    void close() throws IOException;
    Response getResponse();
    void setResponse(Response response);
}
