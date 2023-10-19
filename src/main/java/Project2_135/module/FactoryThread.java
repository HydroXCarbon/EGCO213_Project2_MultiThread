//6513135 Purin Pongpanich
//6513161 Jarupat Chodsitanan
//6513163 Chalisa Buathong
package Project2_135.module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class FactoryThread extends BaseThread {

    private static final Object lockF = new Object();
    // Variables
    private final Product product;
    private final int amountPerDay;
    private CyclicBarrier barrier;
    private List<Integer> holdingMaterial;

    // Constructor
    public FactoryThread(String name, List<Integer> material, Product product, int amountPerDay) {
        super(name);
        this.material = material.stream().map(n -> n * amountPerDay).collect(Collectors.toList());
        this.product = product;
        this.amountPerDay = amountPerDay;
        this.holdingMaterial = new ArrayList<>();
        for (int i = 0; i < material.size(); i++) {
            this.holdingMaterial.add(0);
        }
    }

    // Set barrier
    public void setBarrier(CyclicBarrier barrier) {
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
            synchronized (lockF) {
                while (!shouldRun) {
                    try {
                        lockF.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Terminate thread
                if (shouldTerminate) {
                    break;
                }

                // Notify 1 factory
                lockF.notify();
            }
            // Do work here
            try {

                // Wait for other factory
                barrier.await();

                // Show holding material
                String output = String.format("%-11s >>  Holding  ", Thread.currentThread().getName());
                for (int i = 0; i < holdingMaterial.size(); i++) {
                    int quantityTemp = holdingMaterial.get(i);
                    output += String.format("%4d %-16s", quantityTemp, shareMaterial.get(i).getName());
                }
                System.out.println(output);

                // Wait for other factory
                barrier.await();

                // Get material
                for (int i = 0; i < material.size(); i++) {
                    int quantity = material.get(i) - holdingMaterial.get(i);
                    if (quantity > 0) {
                        int materialTemp = shareMaterial.get(i).get(quantity) + holdingMaterial.get(i);
                        holdingMaterial.set(i, materialTemp);
                    }
                }

                // Check material
                boolean isEnough = true;
                for (int i = 0; i < holdingMaterial.size(); i++) {
                    if (holdingMaterial.get(i) < material.get(i)) {
                        isEnough = false;
                        break;
                    }
                }
                // Wait for other factory
                barrier.await();

                // Start producing if have enough material
                if (!isEnough) {
                    System.out.printf("%-11s >>  %s production fails\n", Thread.currentThread().getName(), product.getName());

                    // Return material
                    for (int i = 0; i < holdingMaterial.size(); i++) {
                        if (holdingMaterial.get(i) < material.get(i) && holdingMaterial.get(i) > 0) {
                            int quantityToReturn = holdingMaterial.get(i);
                            holdingMaterial.set(i, 0);
                            shareMaterial.get(i).put(quantityToReturn);
                        }
                    }

                } else {
                    List<Integer> tempList = new ArrayList<>();
                    for (int i = 0; i < material.size(); i++) {
                        tempList.add(holdingMaterial.get(i) - material.get(i));
                    }
                    this.holdingMaterial = tempList;
                    product.addLotSize(1);
                    System.out.printf("%-11s >>  %s production succeeds, lot %d\n", Thread.currentThread().getName(), product.getName(), product.getLotSize());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            shouldRun = false;
            latch.countDown();
        }
    }

    // Wake up thread
    public void doWork() {
        synchronized (lockF) {
            shouldRun = true;
            lockF.notify();
        }
    }

    // Terminate thread
    public void terminate() {
        synchronized (lockF) {
            shouldRun = true;
            shouldTerminate = true;
            lockF.notify();
        }
    }
}
