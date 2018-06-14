package com.hao.ftp.net.protocol;

import java.nio.channels.SelectionKey;

public interface Protocol {
    void process(SelectionKey key);
}
