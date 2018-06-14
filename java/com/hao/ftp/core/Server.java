package com.hao.ftp.core;

import com.hao.ftp.core.Constant.ConfigConstant;
import com.hao.ftp.net.Connector;
import com.hao.ftp.net.protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Logger;

public class Server implements LifeCycle{
    private static String configLocation=null;
    private static Properties config;
    private Logger logger=Logger.getLogger(Server.class.getName());
    private Connector connect;
    private Protocol protocol;
    private String serverBase;
    //private String root;
    public Server(){
        serverBase=System.getProperty("serverBase",System.getProperty("user.dir"));
        System.setProperty("serverBase",serverBase);
        config=new Properties();
    }
    @Override
    public void init() throws LifeCycleException{
        if(configLocation==null)
            configLocation=System.getProperty("server.config","conf\\server.properties");
        try {
            File file=new File(System.getProperty(serverBase), configLocation);
           // System.out.println(file.exists());
            Reader reader = new FileReader(file);
            config.load(reader);
            System.setProperty(ConfigConstant.ROOT_DIR,config.getProperty(ConfigConstant.ROOT_DIR,"D:\\pdf"));
            System.setProperty(ConfigConstant.CHARSET,config.getProperty(ConfigConstant.CHARSET,"utf8"));
            String connectClass=config.getProperty(ConfigConstant.CONNECT_CLASS);
            connect= (Connector) Class.forName(connectClass).newInstance();
            connect.setConfig(config);
            connect.init();
        }catch (Exception e){
            logger.warning("server init fail"+e);
            throw new LifeCycleException(e);
        }
    }

    @Override
    public void start() throws LifeCycleException {
        connect.start();
    }

    @Override
    public void stop() throws LifeCycleException {

    }
    public static void main(String[] args) throws Exception {
        Server server=new Server();
        if(args.length>0)
            configLocation=args[0];
        server.init();
        server.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
