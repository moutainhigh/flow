package com.aspire.thread.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FakeLimitedResource {
	private final static AtomicBoolean inUse = new AtomicBoolean(false);
	//private final static AtomicInteger integer = new AtomicInteger(0);
	AtomicReference<Integer> ar = new AtomicReference<Integer>(0);
	volatile int i = 0;
	public void use() throws InterruptedException {
		// 真实环境中我们会在这里访问/维护一个共享的资源
		// 这个例子在使用锁的情况下不会非法并发异常IllegalStateException
		// 但是在无锁的情况由于sleep了一段时间，很容易抛出异常
		//System.out.println(inUse);
		//integer.getAndIncrement();
		while(true){
			Integer temp = ar.get();
			if (ar.compareAndSet(temp, temp + 1)) {
				break;
				//System.out.println(i);
				//throw new IllegalStateException("Needs to be used by one client at a time");
			}
			/*try {
			Thread.sleep((long) (3 * Math.random()));
		} finally {
			inUse.set(false);
		}*/
		}
	}

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(10);
		/*boolean compareAndSet = inUse.compareAndSet(false, true);
		System.out.println(compareAndSet);
		System.out.println(inUse);*/
		final FakeLimitedResource resource = new FakeLimitedResource();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
        	/*Callable<Void> task = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					resource.use();
					return null;
				}
        		
        	};
        	 service.submit(task);*/
        	new Thread(){
        		public void run(){
        			try {
        				for(int j=0;j<1000;j++){
        					
        					resource.use();
        				}
						countDownLatch.countDown();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
        		}
        	}.start();
        }
        /*while(Thread.activeCount()>1){
        	Thread.yield();
        }//保证前面的线程都执行完
*/        
        countDownLatch.await();
        System.out.println(resource.ar.get());
		
	}
	
	public  AtomicInteger inc = new AtomicInteger();
	 
    public  void increase() {
        inc.getAndIncrement();
    }
}
