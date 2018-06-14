package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.UserInfo;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;

import java.io.IOException;

public class PwdHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws IOException {
        String dir=req.getUserInfo().getWorkDir();
        res.setCode(ResponseCode.WORK_DIR);
        res.setMsg(dir+" is current directory");
        res.flush();
        return SocketState.READ;
    }
}
