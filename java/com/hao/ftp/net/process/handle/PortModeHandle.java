package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.UserInfo;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;

import java.io.IOException;

public class PortModeHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        try {
            String[] ss = req.getReqMsg().split(",");
            int port;
            UserInfo userInfo = req.getUserInfo();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                builder.append(ss[i]);
                if (i != 3)
                    builder.append(".");
            }
            userInfo.setIp(builder.toString());
            port=Integer.parseInt(ss[4])*256+Integer.parseInt(ss[5]);
            userInfo.setDataPort(port);
            userInfo.setTransferMode(UserInfo.TransferMode.PORT);
        }catch (Exception e){
            res.setCode(ResponseCode.SYNTAX_ERROR);
            res.setMsg("syntax error :"+req.getReqMsg());
            res.flush();
            throw new ProcessException("process error :"+e);
        }
        res.setCode(ResponseCode.COMMAND_OK);
        res.setMsg(req.getCommand().toUpperCase()+" command successful");
        res.flush();
        return SocketState.READ;
    }
}
