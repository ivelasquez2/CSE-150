package nachos.threads;
import nachos.machine.*;
/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
	LinkedList LinkedList;
	public Alarm(){
		LinkedList = new LinkedList();
		Machine.timer().setInterruptHandler(new Runnable(){
			public void run() {
				timerInterrupt();
			}
		});
	}


    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
	public void timerInterrupt() {
	    long time = Machine.timer().getTime();//Clock tick timer
	    boolean intStatus = Machine.interrupt().disable();
	    LinkedList.Node Current = LinkedList.checkList();
	    while(LinkedList != null && Current.data <= time) {
	    	Current.Thread.ready();
	    	LinkedList.deleteNode(Current);
	    }
	    Machine.interrupt().restore(intStatus);
	}


    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
	   public void waitUntil(long x) {
			long wakeTime = Machine.timer().getTime() + x;
			Boolean intStatus = Machine.interrupt().disable();
			LinkedList.push(wakeTime, KThread.currentThread());
			KThread.sleep();
			Machine.interrupt().restore(intStatus);
	   }
	   
	   public class LinkedList{
		   Node head;
		   class Node{
			   long data;
			   KThread Thread;
			   Node next;
			   int pos = 0;
			   Node(long d,KThread KThread){
				   data = d;
				   Thread = KThread;
				   next = null;
			   }
		   }
		   public void push(long wakeTime, KThread KThread) {
			   Node NewNode = new Node(wakeTime, KThread);
			   NewNode.next = head;
			   head = NewNode;
			}
			void deleteNode(Node Current) {
			   if (head == null) {
				   return;
			   }
			   long timeKey = Current.data;
			   Node tempHead = head;
			   Node prev = null;
			   if(Current == head) {
				   head = prev;
				   return;
			   }
			   while(tempHead != null && timeKey != tempHead.data) {
				   prev = tempHead;
				   tempHead = tempHead.next;
			   }
			   if(tempHead == null) {
				   return;
			   }
			   prev.next = tempHead.next;
			}
			public Node checkList() {
			   Node check = head;
			   Node smallest = null;
			   long tempVal = 0;
			   long LowestVal = check.data;
			   while (check != null) {
				   tempVal = check.data;
				   if(tempVal < LowestVal) {
					   LowestVal = tempVal;
					   smallest = check;
					   check = check.next;
				   }else {
					   check = check.next;
					}
				}
				return smallest;
			}
	   }
	   
}
