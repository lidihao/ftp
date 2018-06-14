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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreHandle implements CommandHandle {
    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        String fileName = req.getReqMsg();
        UserInfo userInfo=req.getUserInfo();
        DataTransfer dataTransfer=req.createDataTransfer();
        res.setCode(ResponseCode.CONNECTION_OPEN);
        res.setMsg("Data connection already open transfer starting");
        res.flush();
        new Thread(new StoreTask(dataTransfer,userInfo.getWorkDir()+"/"+fileName)).start();
        return SocketState.READ;
    }
    class StoreTask extends TransferTask{
        private String fileName;
        private ExecutorService executor;
        public StoreTask(DataTransfer dataTransfer,String fileName){
            super(dataTransfer);
            this.fileName=fileName;
        }
        @Override
        protected void doRun() throws Exception {
            byte[] buff=new byte[1024];
            InputStream inputStream=dataTransfer.getInputStream();
            File file=new File(System.getProperty(ConfigConstant.ROOT_DIR),fileName);
            file.createNewFile();
            OutputStream outputStream=new FileOutputStream(file);
            BufferedOutputStream bufferOuputStream=new BufferedOutputStream(outputStream);
            int len=-1;
            while ((len=inputStream.read(buff))!=-1){
               bufferOuputStream.write(buff,0,len);
            }
        }
    }
}
