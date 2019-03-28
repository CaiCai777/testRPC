package caicai.client;



import caicai.model.RpcRequest;
import caicai.model.RpcResponse;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class TestFuture implements Future<Object> {
    private Sync sync;
    private RpcResponse rpcResponse;
    private RpcRequest rpcRequest;
    private long startTime;
    private long responseThresholdTime=5000;
    //存放用户添加的callBack
    public ArrayList<AsyncCallback> pendingCallbacks=new ArrayList<AsyncCallback>();
    public TestFuture(RpcRequest rpcRequest){
        this.rpcRequest=rpcRequest;
        this.sync=new Sync();
        this.startTime=System.currentTimeMillis();
    }
    private ReentrantLock lock=new ReentrantLock();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
     throw new UnsupportedOperationException();
    }
//判断future是否有结果
    @Override
    public boolean isDone() {
        return sync.isDone();
    }
//同步阻塞获得future的结果
    @Override
    public Object get() throws InterruptedException, ExecutionException {
       sync.acquire(-1);
       if(this.rpcResponse!=null)
           return rpcResponse.getResult();
       else {//acquire 线程可能在阻塞过程中被中断而释放，因此不一定可以得到结果
           return null;
       }



    }
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
    public void done(RpcResponse response){
        this.rpcResponse=response;
        sync.release(1);//等待线程不再阻塞
        //已经成功得到result，可以运行用户定义的callback
        invokeAsynCallbacks(pendingCallbacks);

    }
    //运行用户定义的callBack
    private void invokeAsynCallbacks(ArrayList<AsyncCallback> callbacks){
        try {
            lock.lock();
            for(AsyncCallback callback:callbacks)
                runCallback(callback);

        }finally {
            lock.unlock();
        }


    }
    //用户可以通过这个方法给future添加callBack
    public void addAsyncCallback(AsyncCallback callback){
        try {
            lock.lock();
            if (isDone()){
                runCallback(callback);
            }else
            {
              pendingCallbacks.add(callback);
            }

        }finally {
            lock.unlock();
        }
    }
   private void runCallback(AsyncCallback callback){
        final RpcResponse response=this.rpcResponse;
       MyThreadPoolExecutor.getInstance().submit(new Runnable() {
           @Override
           public void run() {
               if(!response.isError())
                   callback.success(response.getResult());
               else {
                   callback.fail(new RuntimeException("Response error", new Throwable(response.getError())));
               }
           }
       });
    }

    static class Sync extends AbstractQueuedSynchronizer{
        private static final long seriaVersionUID=1L;
        //表示状态
        private final int done=1;
        private final int pending=0;
        protected boolean tryAcquire(int arg) {
            if(getState()==done){
                return true;
            }else
                return false;
        }
        protected boolean tryRelease(int arg) {
            if(getState()==pending){
                if(compareAndSetState(pending,done)){
                    return true;
                }
                else return false;
            }else
                return true;
        }
        public boolean isDone(){
            getState();
            return getState()==done;

        }

    }

}
