package com.hao.ftp.net;

import com.hao.ftp.buff.OutputBuffer;
import com.hao.ftp.core.Constant.ResponseCode;
import com.hao.ftp.entity.Packet;

import java.io.IOException;

public class Response {
    private int code=ResponseCode.NULL_STATE;
    private String msg;
    private OutputBuffer outputBuffer;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public OutputBuffer getOutputBuffer() {
        return outputBuffer;
    }
    public void setOutputBuffer(OutputBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }
    public void flush() throws IOException {
        write(toPacket());
        outputBuffer.flush();
    }
    private void write(Packet packet) throws IOException{
        outputBuffer.write(packet);
    }
    private Packet toPacket(){
        String s;
        if (code==ResponseCode.NULL_STATE) return null;
        else if(msg!=null){
            s=code+" "+msg+"\r\n";
        }else {
            s=code+"\r\n";
        }
        Packet packet=new Packet();
        packet.setContent(s);
        return packet;
    }
}
