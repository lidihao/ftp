package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;

import java.io.IOException;

public class QuitHandle implements CommandHandle {

    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        res.setCode(ResponseCode.CLOSE);
        res.setMsg("bye");
        res.flush();
        return SocketState.CLOSE;
    }
}
