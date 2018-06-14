package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class PassWordAuthenHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res)throws IOException{
        String pwd=req.getReqMsg();
        Properties userInfo=new Properties();
        String userName=req.getUserInfo().getUsername();
        String realPwd=null;
        userInfo.load(new FileReader(new File(System.getProperty("serverBase"),"conf/user.properties")));
        if(userName==null){
            res.setCode(ResponseCode.BAD_SEQ_CMD);
            res.setMsg("Bad sequence of commands,username is null");
            res.flush();
            return SocketState.CLOSE;
        }
        realPwd=userInfo.getProperty(userName);
        if(realPwd==null||!realPwd.equals(pwd)){
            res.setCode(ResponseCode.NOT_LOG_IN);
            res.setMsg("user can not log in");
            res.flush();
            return SocketState.CLOSE;
        }else {
            res.setCode(ResponseCode.USER_LOG_IN);
            res.setMsg("User logged in,proceed");
            res.flush();
            return SocketState.READ;
        }
    }
}
