//6513135 Purin Pongpanich
//6513161 Jarupat Chodsitanan
//6513163 Chalisa Buathong
package Project2_135.module;

import java.util.List;

public class SupplierThread extends BaseThread {

    private static final Object lockS = new Object();

    // Constructor
    public SupplierThread(String name,List<Integer> material){
        super(name);
        this.material = material;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lockS) {
                while (!shouldRun) {
                    try {
                        lockS.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Terminate thread
                if (shouldTerminate) {
                    break;
                }

                // Do work here
                for( int i = 0 ; i<material.size();i++){
                    int quantity = material.get(i);
                    shareMaterial.get(i).put(quantity);
                }

                // Notify factory and count down
                shouldRun = false;
                latch.countDown();
                lockS.notify();
            }
        }
    }

    // Wake up thread
    public void doWork() {
        synchronized (lockS) {
            shouldRun = true;
            lockS.notify();
        }
    }

    // Terminate thread
    public void terminate() {
        synchronized (lockS) {
            shouldRun = true;
            shouldTerminate = true;
            lockS.notify();
        }
    }

}
