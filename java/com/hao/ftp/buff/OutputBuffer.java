package com.hao.ftp.buff;


import com.hao.ftp.entity.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface OutputBuffer extends Buffer{
    int write(Packet packet) throws IOException;
    void flush() throws IOException;
    int write(ByteBuffer buffer)throws IOException;
}
