package Project2_135.module;

import java.util.List;
import java.util.concurrent.CountDownLatch;

abstract class BaseThread extends Thread {

    // Variables
    protected final List<Integer> material;
    protected List<Material> shareMaterial;
    protected final Object lock = new Object();
    protected boolean shouldRun = false;
    protected boolean shouldTerminate = false;
    protected CountDownLatch latch;

    // Constructor
    public BaseThread(String name, List<Integer> material){
        super(name);
        this.material = material;
    }

    // Set latch
    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    // Access material
    public List<Integer> getMaterial(){
        return material;
    }

    // Set buffer
    public void setBuffer(List<Material> shareMaterial){
        this.shareMaterial = shareMaterial;
    }

    // Wake up thread
    public void doWork() {
        synchronized (lock) {
            shouldRun = true;
            lock.notify();
            while (shouldRun) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Terminate thread
    public void terminate() {
        synchronized (lock) {
            shouldTerminate = true;
            lock.notify();
        }
    }
}
