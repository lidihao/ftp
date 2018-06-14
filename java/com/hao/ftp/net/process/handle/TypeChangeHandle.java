package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.UserInfo;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;

import java.io.IOException;

public class TypeChangeHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        UserInfo userInfo=req.getUserInfo();
        String type=req.getReqMsg();
        if (type.equalsIgnoreCase("i")){
            res.setCode(ResponseCode.COMMAND_OK);
            res.setMsg("Type set to I");
            userInfo.setTransferType(UserInfo.TransferType.BINARY);
        }else if(type.equalsIgnoreCase("a")){
            res.setCode(ResponseCode.COMMAND_OK);
            res.setMsg("Type set to A");
            userInfo.setTransferType(UserInfo.TransferType.ASCII);
        }else {
            res.setCode(ResponseCode.SYNTAX_ERROR);
            res.setMsg("command syntax error,type binary or type ascii");

        }
        res.flush();
        return SocketState.READ;
    }
}
