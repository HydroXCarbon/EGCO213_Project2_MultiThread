package Project2_135.module;

import java.util.List;

public class SupplierThread extends BaseThread {

    // Constructor
    public SupplierThread(String name,List<Integer> material){
        super(name, material);
    }


    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                while (!shouldRun) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (shouldTerminate) {  // Check termination condition
                    break;
                }

                // Do work here
                for( int i = 0 ; i<material.size();i++){
                    int quantity = material.get(i);
                    shareMaterial.get(i).put(quantity);
                }

                // Notify factory and count down
                latch.countDown();
                shouldRun = false;
                lock.notify();
            }
        }
    }
}
