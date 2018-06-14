package com.hao.ftp.buff;

import com.hao.ftp.core.Constant.ConfigConstant;
import com.hao.ftp.entity.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

public class NioOutputBuffer extends BufferBase implements OutputBuffer{
    private SocketChannel sc;

    public NioOutputBuffer(int outputBuffSize, SocketChannel channel) {
        allocateBuff(outputBuffSize);
        this.sc=channel;
    }

    public synchronized int write(Packet packet) throws IOException {
        String s=packet.getContext();
        byte[] bytes=s.getBytes(System.getProperty(ConfigConstant.CHARSET));
        if(byteBuffer.remaining()<bytes.length)
            flush();
        byteBuffer.limit(byteBuffer.capacity());
        byteBuffer.put(bytes);
        return bytes.length;
    }

    public synchronized void flush() throws IOException {
        byteBuffer.flip();
        while (byteBuffer.hasRemaining())
            sc.write(byteBuffer);
        byteBuffer.compact();
    }

    @Override
    public synchronized int write(ByteBuffer buffer) throws IOException {
        buffer.flip();
        int nwrite=buffer.remaining();
        while (byteBuffer.remaining()<buffer.remaining())
            flush();
        byteBuffer.limit(byteBuffer.capacity());
        byteBuffer.put(buffer);
        return nwrite;
    }
    public void reset(){
        super.reset();
        sc=null;
    }

    @Override
    public void setSc(SocketChannel sc) {
        this.sc=sc;
    }
}
