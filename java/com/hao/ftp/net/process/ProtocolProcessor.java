package com.hao.ftp.net.process;

import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface ProtocolProcessor {
    SocketState doProcess(SelectionKey selectionKey) throws ProcessException, IOException;
    void reset();
    Response getRespose();
}
