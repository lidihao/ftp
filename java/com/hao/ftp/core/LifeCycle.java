package com.hao.ftp.core;

import java.io.FileNotFoundException;

/**
 *生命周期接口，管理组件的init,start,
 * @author lidihao
 */
public interface LifeCycle {
    /**
     * 初始化组件
     */
    void init() throws LifeCycleException, FileNotFoundException;
    /**
     * 启动组件
     */
    void start()throws LifeCycleException;
    /**
     * 停止组件
     */
    void stop()throws LifeCycleException;
}
