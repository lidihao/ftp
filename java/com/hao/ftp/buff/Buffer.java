package com.hao.ftp.buff;

import java.net.Socket;
import java.nio.channels.SocketChannel;

public interface Buffer {
    void reset();
    boolean isDirect();
    void allocateBuff(int capacity);
    void setSc(SocketChannel sc);
}
