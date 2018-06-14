package com.hao.ftp.net.process;

import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;

import java.io.IOException;

public interface CommandHandle {
    SocketState doHandle(Request req, Response res) throws ProcessException,IOException;
}
