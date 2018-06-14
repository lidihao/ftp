package com.hao.ftp.core;

public class LifeCycleException extends Exception {
    public LifeCycleException(){
        super();
    }
    public LifeCycleException(String msg){
        super(msg);
    }
    public LifeCycleException(Throwable tr){
        super(tr);
    }
    public LifeCycleException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
