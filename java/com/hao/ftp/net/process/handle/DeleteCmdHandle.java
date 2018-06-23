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

public class DeleteCmdHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        String fileName=req.getReqMsg();
        UserInfo userInfo=req.getUserInfo();
        File file=new File(System.getProperty(ConfigConstant.ROOT_DIR)+userInfo.getWorkDir(),fileName);
        if (file.exists()){
            if (deleteFileOrDir(file)){
                res.setCode(ResponseCode.COMMAND_OK);
                res.setMsg("delete the file<"+fileName+"> is successful");
            }else {
                res.setCode(ResponseCode.COMMAND_FAIL);
                res.setMsg("delete the file<"+fileName+"> failed");
            }
        }else {
            res.setCode(ResponseCode.COMMAND_FAIL);
            res.setMsg("no such file");
        }
        res.flush();
        return SocketState.READ;
    }
    private boolean deleteFileOrDir(File file){
        if (file==null)
            return false;
        if (file.isFile()){
            return file.delete();
        }else{
            for (File f:file.listFiles()){
                if(!deleteFileOrDir(f)){
                    return false;
                }
            }
        }
        return file.delete();
    }
}
