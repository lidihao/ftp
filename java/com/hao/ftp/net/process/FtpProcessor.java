package com.hao.ftp.net.process;

import com.hao.ftp.buff.InputBuffer;
import com.hao.ftp.buff.OutputBuffer;
import com.hao.ftp.core.Constant.Command;
import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.Packet;
import com.hao.ftp.entity.SocketAttachment;
import com.hao.ftp.net.Request;
import com.hao.ftp.net.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpProcessor implements ProtocolProcessor {
    private Request req;
    private Response response;
    private DefaultDispatcher dispatcher;
    private Logger logger=Logger.getLogger(FtpProcessor.class.getSimpleName());

    public FtpProcessor(){
        dispatcher=DefaultDispatcher.getSingleton();
    }
    public Request getReq() {
        return req;
    }

    public void setReq(Request req) {
        this.req = req;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public SocketState doProcess(SelectionKey selectionKey) throws ProcessException, IOException {
        SocketAttachment attachment= (SocketAttachment) selectionKey.attachment();
        if(selectionKey.isWritable()&&attachment.getUserInfo().isFirstAccept()){
            attachment.getUserInfo().setFirstAccept(false);
            processFirstReq();
        }else {
            createReq(attachment.getInputBuffer());
            req.setUserInfo(attachment.getUserInfo());
        }

        createRes(attachment.getOutputBuffer());
        if(logger.isLoggable(Level.FINE))
            logger.fine("receive the msg :"+req);
        req.setResponse(response);
        return dispatcher.dispatchReq(req,response);
    }

    @Override
    public void reset() {
        if(response!=null) {
            response.setOutputBuffer(null);
            response.setCode(ResponseCode.NULL_STATE);
            response.setMsg(null);
        }
        if(req!=null){
            req.setUserInfo(null);
            req.setReqMsg(null);
            req.setCommand(null);
        }
    }

    @Override
    public Response getRespose() {
        return response;
    }

    void createRes(OutputBuffer outputBuffer){
        if(response==null)
            response=new Response();
        response.setOutputBuffer(outputBuffer);
        response.setMsg(null);
        response.setCode(ResponseCode.NULL_STATE);
    }
    void createReq(InputBuffer buffer) throws ProcessException {
        Packet packet=new Packet();
        try {
            buffer.read(packet);
        }catch (IOException e){
            e.printStackTrace();
            throw new ProcessException(e);
        }
        if (req==null){
            req=new Request();
        }
        String content=packet.getContext();
        int index=content.indexOf(' ');
        if(index!=-1) {
            req.setCommand(content.substring(0, index).toLowerCase());
            req.setReqMsg(content.substring(index + 1));
        }else if(content!=null||!content.equals("")){
            req.setCommand(content.toLowerCase());
            req.setReqMsg(null);
        }
    }
    void processFirstReq(){
        if(req==null)
            req=new Request();
        req.setCommand(Command.FIRSTREQ);
        req.setReqMsg(null);
    }
}
