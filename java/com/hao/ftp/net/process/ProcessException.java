package com.hao.ftp.net.process;

public class ProcessException extends Exception {
    public ProcessException(){
    }
    public ProcessException(String msg){
        super(msg);
    }
    public ProcessException(Throwable t){
        super(t);
    }
}
