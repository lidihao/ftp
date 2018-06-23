package com.hao.ftp.net.protocol;

import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.SocketAttachment;
import com.hao.ftp.net.Response;
import com.hao.ftp.net.process.FtpProcessor;
import com.hao.ftp.net.process.ProcessException;
import com.hao.ftp.net.process.ProtocolProcessor;
import com.hao.ftp.net.process.SocketState;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpProtocol implements Protocol {
    private ConcurrentLinkedQueue<SoftReference<ProtocolProcessor>> processorCache=new ConcurrentLinkedQueue<SoftReference<ProtocolProcessor>>();
    private Map<SelectionKey,ProtocolProcessor> conntections=new HashMap<SelectionKey, ProtocolProcessor>();
    private Logger log=Logger.getLogger(Protocol.class.getName());
    public FtpProtocol(){

    }
    public void process(SelectionKey key) {
        SocketAttachment socketAttachment= (SocketAttachment) key.attachment();
        ProtocolProcessor protocolProcessor=null;
        protocolProcessor=conntections.get(key);
        if(protocolProcessor==null)
            protocolProcessor=getProcessor();
        socketAttachment.init();
        SocketState state;
        try {
            if(log.isLoggable(Level.FINE))
                log.fine("protocol process the socket,interest is "+key.interestOps());
            state = protocolProcessor.doProcess(key);
        }catch (ClosedChannelException e){
            log.warning("socket has close,sc="+socketAttachment.getKey().channel());
            return;
        }catch(ProcessException e){
            e.printStackTrace();
            log.warning("protocol resolve fail ");
            handleException(protocolProcessor);
            state=SocketState.CLOSE;
        }catch (Exception e){
            //返回错误码
           handleException(protocolProcessor);
            e.printStackTrace();
            state=SocketState.CLOSE;
        }

        if(state.equals(SocketState.CLOSE)){
            socketAttachment.getPoller().cancelKey(key);
            recycleProcess(protocolProcessor);
        }else if(state.equals(SocketState.WRITE)){
            key.interestOps(key.interestOps()|SelectionKey.OP_WRITE);
            //key.selector().wakeup();
            conntections.put(key,protocolProcessor);
        }else if(state.equals(SocketState.READ)){
            key.interestOps(key.interestOps()|SelectionKey.OP_READ);
            key.selector().wakeup();
            conntections.put(key,protocolProcessor);
        }
    }
    ProtocolProcessor getProcessor(){
        ProtocolProcessor processor=null;
        SoftReference<ProtocolProcessor> reference=processorCache.poll();
        if(reference!=null)
            processor=reference.get();
        else
            processor=new FtpProcessor();
        return processor;
    }
    void recycleProcess(ProtocolProcessor processor){
        processor.reset();
        SoftReference<ProtocolProcessor> softReference=new SoftReference<ProtocolProcessor>(processor);
        processorCache.offer(softReference);
    }

    private void handleException(ProtocolProcessor processor){
        Response res=processor.getRespose();
        if(res!=null){
            res.setCode(ResponseCode.SYNTAX_ERROR);
            res.setMsg("servers error");
            try {
                res.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
