package com.hao.ftp.core;

import java.util.logging.Logger;

public abstract class LifeCycleBase implements LifeCycle {
    private Logger logger=Logger.getLogger(LifeCycle.class.getName());
    protected enum LifeCycleState{
        New,
        Initing,
        Inited,
        Starting,
        Started,
        Stoping,
        Stoped,
        Fail;
        public boolean greatThan(LifeCycleState v){
            return this.ordinal()>v.ordinal();
        }
        public boolean lessThan(LifeCycleState v){
            return this.ordinal()<v.ordinal();
        }
    }
    //保证内存的可见性
    protected volatile LifeCycleState state=LifeCycleState.New;
    public void init() throws LifeCycleException {
        if(!state.equals(LifeCycleState.New)){
            throw new LifeCycleException("invalid state "+state.toString());
        }
        try {
            this.state=LifeCycleState.Initing;
            initInternal();
            this.state=LifeCycleState.Inited;
        }catch (Throwable t){
            this.state=LifeCycleState.Fail;
            throw new LifeCycleException("init fail");
        }
    }

    public void start() throws LifeCycleException {
        if(state.equals(LifeCycleState.Started)||state.equals(LifeCycleState.Starting)){
            logger.warning("component has already start");
            return;
        }
        if(state.equals(LifeCycleState.New)){
            init();
        }else if(state.equals(LifeCycleState.Fail)){
            stop();
        }else if(!state.equals(LifeCycleState.Inited)&&!state.equals(LifeCycleState.Stoped)){
            throw new LifeCycleException("invalid state"+this.state);
        }
        try {
            this.state=LifeCycleState.Starting;
            startInternal();
            this.state=LifeCycleState.Started;
        }catch (Throwable t){
            this.state=LifeCycleState.Fail;
            throw new LifeCycleException("start fail "+t);
        }
    }

    public void stop() throws LifeCycleException {
        if(state.equals(LifeCycleState.New)){
            throw new LifeCycleException("invalid state"+state);
        }
        try {
            this.state=LifeCycleState.Stoping;
            stopInternal();
            this.state=LifeCycleState.Stoped;
        }catch (Throwable t){
            this.state=LifeCycleState.Fail;
            throw new LifeCycleException("stop fail "+t);
        }
    }

    /**
     * 模版方法，由子类实现
     */
    protected abstract void initInternal() throws LifeCycleException;
    protected abstract void startInternal() throws LifeCycleException;
    protected abstract void stopInternal() throws LifeCycleException;
}
