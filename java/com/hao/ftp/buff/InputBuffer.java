package com.hao.ftp.buff;

import com.hao.ftp.entity.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface InputBuffer extends Buffer{
    int read(Packet packet) throws IOException;
}
