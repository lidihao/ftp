package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;

import java.io.IOException;

public class UserAuthenHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws IOException {
        String userName=req.getReqMsg();
        req.getUserInfo().setUsername(userName);
        res.setCode(ResponseCode.NEET_PASSWORD);
        res.setMsg("User name okay,need password");
        res.flush();
        return SocketState.READ;
    }
}
