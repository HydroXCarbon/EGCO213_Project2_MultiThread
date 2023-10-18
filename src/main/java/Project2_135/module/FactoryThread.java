package Project2_135.module;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class FactoryThread extends BaseThread {

    // Variables
    private final Product product;
    private final int amountPerDay;
    private CyclicBarrier barrier;

    // Constructor
    public FactoryThread(String name, List<Integer> material, Product product, int amountPerDay) {
        super(name, material);
        this.product = product;
        this.amountPerDay = amountPerDay;
    }

    // Set barrier
    public void setBarrier(CyclicBarrier barrier){
        this.barrier = barrier;
    }

    // Access product
    public Product getProduct() {
        return product;
    }

    // Access amount per day
    public int getAmountPerDay() {
        return amountPerDay;
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
                try {
                    // Wait for all supplier to finish


                    // Show holding material
                    System.out.printf("%-11s >>  Holding  ", Thread.currentThread().getName());
                    for (int i = 0; i < material.size(); i++) {
                        int quantityTemp = material.get(i);
                        System.out.printf("%4d %-16s", quantityTemp, shareMaterial.get(i).getName());
                    }
                    System.out.print("\n");

                    // Wait for all factory to finish
                    barrier.await();

                    // Start producing
                    for (int i = 0; i < material.size(); i++) {
                        int quantity = material.get(i);
                        shareMaterial.get(i).get(quantity, material);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Notify factory and count down
                latch.countDown();
                shouldRun = false;
                lock.notify();
            }
        }
    }
}
