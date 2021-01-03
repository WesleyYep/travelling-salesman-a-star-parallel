package util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Non-blocking channel. Producer Consumer problem
 * @author Chang Kon Han
 * @param <T>
 */

public class Channel<T> {

	private Queue<T> msgQueue;
	private long id;
	private AtomicBoolean isWaiting = new AtomicBoolean(false);
	
	public Channel(long id) {
		this.id = id;
		// thread-safe
		msgQueue = new ConcurrentLinkedQueue<T>();
	}
	
	public void send(T msg) {
		msgQueue.add(msg);
	}
	
	public T receive() {
		return msgQueue.poll();
	}
	
	public boolean isEmpty() {
		return msgQueue.isEmpty();
	}
	
	public long getId() {
		return id;
	}
	
	public int size() {
		return msgQueue.size();
	}
	
	public synchronized void callWait() throws InterruptedException {
		isWaiting.set(true);
		wait();
	}
	
	public synchronized void callNotifyAll() {
		isWaiting.set(false);
		notifyAll();
	}
	
	public boolean getIsWaiting() {
		return isWaiting.get();
	}
}
