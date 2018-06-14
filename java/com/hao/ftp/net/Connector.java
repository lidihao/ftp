package com.hao.ftp.net;

import com.hao.ftp.core.LifeCycle;
import com.hao.ftp.net.protocol.Protocol;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 *connector接口
 * @author lidihao
 */
public interface Connector extends LifeCycle {
    /**
     * 绑定特定的端口
     */
    void bind() throws Exception;
    /**
     * 取消绑定
     */
    void unbind() throws IOException;

    Executor getExecutor();

  //  void processReq(Protocol protocol);
  void setConfig(Properties config);
}
