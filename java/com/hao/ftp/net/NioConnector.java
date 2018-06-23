package com.hao.ftp.net;

import com.hao.ftp.core.Constant.ConfigConstant;
import com.hao.ftp.core.LifeCycleBase;
import com.hao.ftp.core.LifeCycleException;
import com.hao.ftp.entity.SocketAttachment;
import com.hao.ftp.entity.SocketConfig;
import com.hao.ftp.net.protocol.Protocol;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NioConnector extends LifeCycleBase implements Connector{

    private Logger logger=Logger.getLogger(Connector.class.getName());
    private ServerSocketChannel serverSocketChannel;
    private int port;//端口号，默认为21
    private InetAddress address; //config 绑定地址
    private Acceptor[] acceptors; //config 接受器，接受请求，注册到Poller
    private ExecutorService executor; //config 线程池，处理请求的线程
    private Poller[] pollers;//config 轮询器，处理注册到selector上的请求
    private int acceptorCount; //config
    private int pollerCount; //config
    private AtomicInteger pollerIndex=new AtomicInteger(-1);
    private long selectTimeout=1000;//config
    private ConcurrentLinkedQueue<SoftReference<SocketAttachment>> socketAttachmentCache=new ConcurrentLinkedQueue<SoftReference<SocketAttachment>>();//做一个缓存
    private SocketConfig socketConfig=new SocketConfig();
    private Protocol protocol;//config 协议处理,这里是ftp协议
    private Properties config;
    //-----------executor config------
    private int maxWorkThread=100; //config
    private int coreWorkThread=20; //config
    //-----constructor----


    //--------get,set----------

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public ExecutorService getExecutor() {
        if(executor==null);
            createExecutor();
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public Acceptor[] getAcceptors() {
        return acceptors;
    }

    public Poller[] getPollers() {
        return pollers;
    }

    public Poller getPoller(){
        int idx=Math.abs(pollerIndex.incrementAndGet())%getPollerCount();
        return pollers[idx];
    }

    public int getAcceptorCount() {
        if(acceptorCount==0)
            acceptorCount=1;
        return acceptorCount;
    }

    public void setAcceptorCount(int acceptorCount) {
        this.acceptorCount = acceptorCount;
    }

    public int getPollerCount() {
        return pollerCount;
    }

    public void setPollerCount(int pollerCount) {
        if(pollerCount==0)
            pollerCount=1;
        this.pollerCount = pollerCount;
    }

    public long getSelectTimeout() {
        return selectTimeout;
    }

    public void setSelectTimeout(long selectTimeout) {
        this.selectTimeout = selectTimeout;
    }

    public SocketConfig getSocketConfig() {
        return socketConfig;
    }

    public void setSocketConfig(SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getMaxWorkThread() {
        return maxWorkThread;
    }

    public void setMaxWorkThread(int maxWorkThread) {
        this.maxWorkThread = maxWorkThread;
    }

    public int getCoreWorkThread() {
        return coreWorkThread;
    }

    public void setCoreWorkThread(int coreWorkThread) {
        this.coreWorkThread = coreWorkThread;
    }

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    //-----public method-------
    /**
     * @throws IOException
     */
    public void bind() throws IOException {
        serverSocketChannel=ServerSocketChannel.open();
        InetSocketAddress socketAddress=getAddress()==null?new InetSocketAddress(getAddress(),getPort()):new InetSocketAddress(getPort());
        serverSocketChannel.socket().bind(socketAddress,socketConfig.getBacklog());
        serverSocketChannel.configureBlocking(true);//阻塞接收请求
        if (logger.isLoggable(Level.INFO))
            logger.info("bind ip :"+socketAddress.getAddress()+",bind port :"+getPort());
    }

    public void createExecutor(){
        executor=new ThreadPoolExecutor(getCoreWorkThread(),getMaxWorkThread(),1000,
                TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(10),new DefaultThreadFactory("work")
                );
    }
    /**
     *
     */
    public void unbind() throws IOException {
        serverSocketChannel.close();
    }
    //设置各种参数
    protected void initInternal() throws LifeCycleException {
        try {
        String value=null;
        value=config.getProperty(ConfigConstant.ACCEPT_COUNT);
        if(value!=null)
            acceptorCount=Integer.parseInt(value);
        value=null;
        value=config.getProperty(ConfigConstant.POLLER_COUNT);
        if(value!=null)
            pollerCount=Integer.parseInt(value);
        value=null;
        value=config.getProperty(ConfigConstant.CONNECT_ADDRESS);
        if(value!=null)
            address=InetAddress.getByName(value);
        value=null;
        value=config.getProperty(ConfigConstant.CONNECT_PORT);
        if(value!=null)
            port=Integer.parseInt(value);
        value=null;
        value=config.getProperty(ConfigConstant.MAX_WORKER_THREAD_COUNT);
        if(value!=null)
            maxWorkThread=Integer.parseInt(value);
        value=null;
        value=config.getProperty(ConfigConstant.CORE_WORKER_THREAD_COUNT);
        if(value!=null)
            coreWorkThread=Integer.parseInt(value);
        value=null;
        value=config.getProperty(ConfigConstant.SELECT_TIMEOUT);
        if(value!=null)
            selectTimeout=Long.parseLong(value);
        value=null;
        value=config.getProperty(ConfigConstant.PROTOCOL_CLASS);
        if(value!=null)
            protocol= (Protocol) Class.forName(value).newInstance();
        }catch (Exception e){
            throw new LifeCycleException(e);
        }
    }

    protected void startInternal() throws LifeCycleException {
       try {
           bind();//绑定端口
           createExecutor();//创建线程池
           createAcceptor();//创建接受器
           createPoller();//创建轮询器
       }catch (Exception e){
           throw new LifeCycleException(e);
       }
    }

    protected void stopInternal() throws LifeCycleException {
        try {
            getExecutor().shutdown();//停止
            unbind();
        }catch (Exception e){
            throw new LifeCycleException(e);
        }
    }

    public boolean isRunning(){
        return state.greatThan(LifeCycleState.Inited)&&state.lessThan(LifeCycleState.Stoping);
    }
    //-------------default method-------------
    void addEventToPoller(SocketChannel sc, int interestEvent){
        Poller poller=getPoller();
        SocketEvent event=new SocketEvent(sc,interestEvent);
        SoftReference<SocketAttachment> ref=socketAttachmentCache.poll();
        SocketAttachment attachment=null;
       if(ref!=null){
            attachment=ref.get();
            attachment.setPoller(poller);
            if(logger.isLoggable(Level.FINE))
                logger.fine("get attachment from cache :"+attachment);
        }else {
            attachment=new SocketAttachment(socketConfig,poller);
        }
        event.keyAttach=attachment;
        poller.addEvent(event);
        if(logger.isLoggable(Level.FINE))
            logger.fine("add event["+event+"] to poller["+poller+"]");
    }

    void createPoller() throws IOException {
        int count=getPollerCount();
        pollers=new Poller[count];
        for(int i=0;i<count;i++){
            pollers[i]=new Poller();
            Thread pollerThread=new Thread(pollers[i],"poller--"+i);
            pollerThread.setDaemon(true);
            pollerThread.start();
        }
    }

    void createAcceptor(){
        int count=getAcceptorCount();
        acceptors=new Acceptor[count];
        for(int i=0;i<count;i++){
            acceptors[i]=new Acceptor();
            Thread acceptorThread=new Thread(acceptors[i],"acceptor--"+i);
            acceptorThread.setDaemon(true);
            acceptorThread.start();
        }
    }
    //------Inner class----

    /**
     * 接收进来的请求，并交给适当的处理器处理
     */
    class Acceptor implements Runnable{
        public void run() {
            while(isRunning()){
                try {
                    SocketChannel sc=serverSocketChannel.accept();
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("accept a socket "+sc);
                    sc.configureBlocking(false);
                    addEventToPoller(sc,SelectionKey.OP_WRITE);
                } catch (IOException e) {
                    logger.warning("acceptor has something wrong "+e);
                }
            }
        }
    }
    public class Poller implements Runnable{
        private Selector selector;
        private ConcurrentLinkedQueue<SocketEvent> events= new ConcurrentLinkedQueue<SocketEvent>();
        Poller() throws IOException {
            synchronized (Selector.class) {
                //Selector.open()线程不安全
                this.selector =Selector.open();
            }
        }

        public Selector getSelector() {
            return selector;
        }
        //删除键并回收socket的资源
        public void cancelKey(SelectionKey key){
            SocketChannel sc= (SocketChannel) key.channel();
            SocketAttachment socketAttachment= (SocketAttachment) key.attachment();
            key.cancel();
            socketAttachment.reset();
            //将SocketAttachment放回cache
            SoftReference<SocketAttachment> reference=new SoftReference<SocketAttachment>(socketAttachment);
            socketAttachmentCache.offer(reference);
            try {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("close the socket "+sc);
                sc.socket().close();
                sc.close();
            } catch (IOException e) {
                logger.warning("socket fail to close ");
                e.printStackTrace();
            }
        }
        void addEvent(SocketEvent event){
            events.add(event);
            selector.wakeup();
        }
        void processEvents() throws ClosedChannelException {
            while (!events.isEmpty()){
                SocketEvent socketEvent=events.poll();
                SocketAttachment attachment;
                SelectionKey key=null;
                if(socketEvent!=null){
                    attachment=socketEvent.keyAttach;
                    attachment.getUserInfo().setFirstAccept(true);
                    key=socketEvent.sc.register(selector,socketEvent.interestEvent,attachment);
                    attachment.setKey(key);
                }else{
                    logger.warning("socketEvent is null");
                }
                if(logger.isLoggable(Level.FINE))
                    logger.fine("register the socket["+socketEvent.sc+"] to poller["+this+"]");
            }
        }
        void processKey(final SelectionKey key){
            executor=getExecutor();
            executor.execute(()->protocol.process(key));
        }
        public void run() {
            int keyCount=0;
            SelectionKey key=null;
            while (isRunning()){
                try {
                    processEvents();
                    keyCount=selector.select(selectTimeout);
                    //也许有新的sc注册进来
                    if (keyCount==0){
                        processEvents();
                        keyCount=selector.selectNow();
                    }
                    if(keyCount!=0){
                        Iterator iterator=selector.selectedKeys().iterator();
                        key= (SelectionKey) iterator.next();
                        iterator.remove();
                        key.interestOps((~key.readyOps())&key.interestOps());
                        //System.out.println("process key"+key.readyOps());
                        processKey(key);
                    }
                } catch (Exception e) {
                    logger.warning("poller has something wrong "+e);
                }
            }
        }
    }
    class SocketEvent{
        SocketChannel sc;
        SocketAttachment keyAttach;
        int interestEvent;
        SocketEvent(SocketChannel sc,int interestEvent){
            this.interestEvent=interestEvent;
            this.sc=sc;
        }

        @Override
        public String toString() {
            return "SocketEvent{" +
                    "sc=" + sc +
                    ", keyAttach=" + keyAttach +
                    ", interestEvent=" + interestEvent +
                    '}';
        }
    }
    static class DefaultThreadFactory implements ThreadFactory{
        static int threadcount=0;
        String baseName;
        DefaultThreadFactory(String baseName){
            this.baseName=baseName;
        }
        public Thread newThread(Runnable r) {
            Thread thread=new Thread(r);
            thread.setName(baseName+threadcount++);
            return thread;
        }
    }
}