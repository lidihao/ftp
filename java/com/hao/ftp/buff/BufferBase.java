package com.hao.ftp.buff;

import java.nio.ByteBuffer;

public abstract class BufferBase implements Buffer{
    protected ByteBuffer byteBuffer;
    protected boolean isDirect;

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public void reset() {
        byteBuffer.clear();
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void allocateBuff(int capacity) {
        if (isDirect){
            byteBuffer=ByteBuffer.allocateDirect(capacity);
        }else {
            byteBuffer=ByteBuffer.allocate(capacity);
        }
    }
}
