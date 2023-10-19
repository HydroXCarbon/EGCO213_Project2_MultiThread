package Project2_135.module;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Material{

    // Variables
    private final String name;
    private int quantity = 0;
    private int supplierSize;
    private int factorySize;
    private CyclicBarrier barrier;

    // Constructor
    public Material(String name) {
        this.name = name;
    }

    // Set factory size
    public void setFactorySize(int factorySize){
        this.factorySize = factorySize;
    }

    // Set supplier size
    public void setSupplierSize(int supplierSize){
        this.supplierSize = supplierSize;
    }

    // Set barrier
    public void setBarrier(CyclicBarrier barrier){
        this.barrier = barrier;
    }

    // Add quantity from supplier
    synchronized public void put(int quantity) {

        // Start adding
        this.quantity += quantity;
        System.out.printf("%-11s >>  Put      %4d %-16s balance = %4d %s\n",Thread.currentThread().getName(), quantity, name, this.quantity, name);
    }

    // Remove quantity from factory
    synchronized public int get(int quantity) {

        // Start producing
        if(this.quantity >= quantity) {
            this.quantity -= quantity;
        } else {
            quantity = this.quantity;
            this.quantity = 0;
        }
        System.out.printf("%-11s >>  Get      %4d %-16s balance = %4d %s\n",Thread.currentThread().getName(), quantity, name, this.quantity, name);
        return quantity;
    }

    // Access name
    public String getName() {
        return name;
    }

    // Access quantity
    public int getQuantity() {
        return quantity;
    }

    // Access factory size
    public int getFactorySize(){
        return factorySize;
    }

    // Access supplier size
    public int getSupplierSize(){
        return supplierSize;
    }
}
