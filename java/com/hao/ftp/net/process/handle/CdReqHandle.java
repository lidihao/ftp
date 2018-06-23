package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ConfigConstant;
import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.UserInfo;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;

import java.io.File;
import java.io.IOException;

public class CdReqHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        String dir=req.getReqMsg();
        UserInfo userInfo=req.getUserInfo();
        String root=System.getProperty(ConfigConstant.ROOT_DIR);
        File file=null;
        if(dir.startsWith("/")){
            file=new File(root,dir);//绝对路径
        }else {
            file=new File(root+userInfo.getWorkDir(),dir);
        }
        if(file.exists()){
            String newWorkDir=file.getCanonicalPath().substring(root.length()-1).replace("\\","/");
            req.getUserInfo().setWorkDir(newWorkDir);
            res.setCode(ResponseCode.COMMAND_OK);
            res.setMsg("command is ok");
            res.flush();
        }else {
            res.setCode(ResponseCode.COMMAND_FAIL);
            res.setMsg("no such dir,command fail");
            res.flush();
        }
        return SocketState.READ;
    }
}
