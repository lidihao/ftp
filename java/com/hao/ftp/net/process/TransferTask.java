package com.hao.ftp.net.process;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.transfer.DataTransfer;

import java.io.IOException;

public abstract class TransferTask implements Runnable {
    protected DataTransfer dataTransfer;
    public TransferTask(DataTransfer dataTransfer){
        this.dataTransfer=dataTransfer;
    }
    @Override
    public void run() {
        boolean complete=false;
        try {
            doRun();
            complete=true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                handleComplete(complete);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    protected abstract void doRun() throws Exception;
    protected void handleComplete(boolean complete) throws IOException {
        Response response=dataTransfer.getResponse();
        if(complete){
            response.setCode(ResponseCode.TRANSFER_SUCCESS);
            response.setMsg("transfer complete");
            response.flush();
        }else {
            response.setCode(ResponseCode.SYNTAX_ERROR);
            response.setMsg("transfer failed");
            response.flush();
        }
        dataTransfer.close();
    }
}
