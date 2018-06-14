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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DirHandle implements CommandHandle {
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String Separator="        ";
    @Override
    public SocketState doHandle(Request req, Response res) throws ProcessException, IOException {
        String base=System.getProperty(ConfigConstant.ROOT_DIR,System.getProperty("user.dir"));
        UserInfo userInfo=req.getUserInfo();
        File dir=new File(base,userInfo.getWorkDir());
        if(!dir.exists()){
            res.setCode(ResponseCode.SYNTAX_ERROR);
            res.setMsg("thi dir is not exists");
            res.flush();
        }
        File[] files=dir.listFiles();
        StringBuilder fileInfo=new StringBuilder("");
        if(files!=null) {
            for (File f : files) {
                fileInfo.append(longToDate(f.lastModified()));
                if (f.isDirectory()) {
                    fileInfo.append(Separator).append("<DIR>").append(Separator);
                } else {
                    fileInfo.append(Separator).append("   ").append(f.length()).append("   ");
                }
                fileInfo.append(f.getName()).append("\r\n");
            }
        }
        DataTransfer dataTransfer=req.createDataTransfer();
        res.setCode(ResponseCode.CONNECTION_OPEN);
        res.setMsg("Data connection already open transfer starting");
        res.flush();
        new Thread(new DirTransferTask(dataTransfer,fileInfo.toString())).start();
        return SocketState.READ;
    }
    private String longToDate(long millSec){
        Date date=new Date(millSec);
        return simpleDateFormat.format(date);
    }

    class DirTransferTask extends TransferTask {
        String data;
        public DirTransferTask(DataTransfer dataTransfer,String data){
            super(dataTransfer);
            this.data=data;
        }
        @Override
        protected void doRun() throws Exception {
            OutputStream outputStream=dataTransfer.getOutputStream();
            outputStream.write(data.getBytes(System.getProperty(ConfigConstant.CHARSET)));
        }
    }
}
