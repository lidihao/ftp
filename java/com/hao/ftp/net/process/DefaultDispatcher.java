package com.hao.ftp.net.process;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class DefaultDispatcher {
    private Logger logger=Logger.getLogger(DefaultDispatcher.class.getName());
    private static volatile DefaultDispatcher dispatcher=null;
    private static Properties properties;
    private ConcurrentHashMap<String,CommandHandle> handlePool=new ConcurrentHashMap<>();
    private DefaultDispatcher(){
        properties=new Properties();
        init();
    }
    public static DefaultDispatcher getSingleton(){
        if(dispatcher==null){
            synchronized (DefaultDispatcher.class){
                if (dispatcher==null){
                    dispatcher=new DefaultDispatcher();
                }
            }
        }
        return dispatcher;
    }
    private void init(){
        String location=System.getProperty("dispatcher", "conf/dispatcher.properties");
        File file=new File(System.getProperty("serverBase"),location);
        InputStream stream= null;
        try {
            stream = new FileInputStream(file);
            properties.load(stream);
        } catch (Exception e) {
            logger.warning("dispatche init fail"+e);
            throw new RuntimeException(e);
        }

    }
    public SocketState dispatchReq(Request req, Response res) throws ProcessException, IOException {
        CommandHandle handle=handlePool.get(req.getCommand());
        if (handle==null){
            String className=properties.getProperty(req.getCommand());
            if(className==null){
                try {
                    res.setCode(ResponseCode.COMMAND_NOT_IMPL);
                    res.setMsg("server don't implements this command");
                    res.flush();
                    return SocketState.READ;
                }catch (IOException e){
                    logger.warning("response flush fail "+e);
                    throw new ProcessException(e);
                }
            }
            try {
                handle=(CommandHandle)Class.forName(className).newInstance();
            } catch (Exception e) {
                logger.warning("create handle fail "+e);
                throw new ProcessException(e);
            }
            handlePool.put(req.getCommand(),handle);
        }
        return handle.doHandle(req,res);
    }
}
