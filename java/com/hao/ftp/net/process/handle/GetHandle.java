package com.hao.ftp.net.process.handle;

import com.hao.ftp.core.Constant.ConfigConstant;
import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.UserInfo;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.CommandHandle;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.SocketState;
import com.hao.ftp.net.process.TransferTask;
import com.hao.ftp.net.transfer.DataTransfer;

import java.io.*;

public class GetHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        UserInfo userInfo=req.getUserInfo();
        File file=new File(System.getProperty(ConfigConstant.ROOT_DIR),userInfo.getWorkDir()+"/"+req.getReqMsg());
        if(!file.exists()){
            res.setCode(ResponseCode.SYNTAX_ERROR);
            res.setMsg("file is not exists");
            res.flush();
            return SocketState.READ;
        }
        DataTransfer dataTransfer=req.createDataTransfer();
        res.setCode(ResponseCode.CONNECTION_OPEN);
        res.setMsg("Data connection already open transfer starting");
        res.flush();
        new Thread(new GetTask(dataTransfer,file)).start();
        return SocketState.READ;
    }
    class GetTask extends TransferTask {
        private File file;
        public GetTask(DataTransfer dataTransfer,File file){
            super(dataTransfer);
            this.file=file;
        }

        @Override
        protected void doRun() throws Exception {
            OutputStream outputStream=dataTransfer.getOutputStream();
            InputStream inputStream=new FileInputStream(file);
            byte[] buff=new byte[1024];
            int len=-1;
            while((len=inputStream.read(buff))!=-1){
                outputStream.write(buff);
            }
        }
    }
}
