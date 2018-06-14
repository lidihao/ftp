package com.hao.ftp.buff;

import com.hao.ftp.core.Constant.ConfigConstant;
import com.hao.ftp.entity.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NioInputBuffer extends BufferBase implements InputBuffer {
  //  int capacity;
    private SocketChannel sc;

    public NioInputBuffer(int capacity,SocketChannel sc){
        this(capacity,false,sc);
    }

    public NioInputBuffer(int capacity,boolean isDrect,SocketChannel sc){
        allocateBuff(capacity);
        this.isDirect=isDrect;
        this.sc=sc;
    }


    public SocketChannel getSc() {
        return sc;
    }

    public void setSc(SocketChannel sc) {
        this.sc = sc;
    }

    public int read(Packet packet) throws IOException {
        if(packet==null)
            throw new IllegalArgumentException("packet cannot be null");
        int nread=0;
        byte ch;
        byte[] buff=new byte[256];
        boolean tag=false;
        byteBuffer.flip();
        int index=0;
        do{
            if(byteBuffer.position()==byteBuffer.limit()){
                byteBuffer.clear();
                int i=readFromSocket();
                if(i==0) continue;
                else if(i==-1) throw new EOFException("eof when read socket");
                else {
                    byteBuffer.flip();
                }
            }
            nread++;
            ch=byteBuffer.get();
            if((char)ch=='\n'&&tag) break;
            else if ((char)ch=='\r') tag=true;
            else buff[index++]=ch;
        }while (true);
        byteBuffer.compact();
        packet.setContent(deCode(buff,index));
        return nread;
    }
    private String deCode(byte[] bytes,int len){
        return new String(bytes,0,len,Charset.forName(System.getProperty(ConfigConstant.CHARSET)));
    }
    private int readFromSocket() throws IOException {
        return sc.read(byteBuffer);
    }
    public void reset(){
        super.reset();
        sc=null;
    }

}
