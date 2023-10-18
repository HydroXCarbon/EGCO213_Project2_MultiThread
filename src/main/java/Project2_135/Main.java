package Project2_135;

import Project2_135.module.FactoryThread;
import Project2_135.module.Material;
import Project2_135.module.Product;
import Project2_135.module.SupplierThread;

import java.io.*;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;


public final class Main {

    public static void main(String[] args) {
        String inputPath = "src/main/java/Project2_135/";
        String configPath = inputPath + "test.txt";
        ArrayList<FactoryThread> factoryList = new ArrayList<>();
        ArrayList<SupplierThread> suppliersList = new ArrayList<>();
        List<Material> materialList = new ArrayList<>();
        ArrayList<Product> productList = new ArrayList<>();
        int day = 0;
        boolean configFound = false;

        //Insert data from file config to
        while (!configFound) {
            try {
                Scanner scan = new Scanner(new File(configPath));
                day = readFile(scan, factoryList, suppliersList, materialList, productList);
                System.out.printf("%-11s >>  read configs from %s\n\n",Thread.currentThread().getName(), configPath);
                configFound = true;
            } catch (Exception e) {
                System.out.println(e);
                System.out.printf("%-11s >> Enter config file for simulation =\n","Thread "+Thread.currentThread().getName());
                Scanner userInput = new Scanner(System.in);
                String newFileName = userInput.nextLine();
                configPath = inputPath + newFileName;
            }
        }

        //Set supplier size and barrier for each material
        for (Material material : materialList){
            material.setSupplierSize(suppliersList.size());
            material.setFactorySize(factoryList.size());
            material.setBarrier(new CyclicBarrier(factoryList.size()+1));
        }

        //Set each buffer and barrier for all thread
        for (FactoryThread factory : factoryList){
            factory.setBuffer(materialList);
            factory.setBarrier(new CyclicBarrier(factoryList.size()+1));
        }
        for (SupplierThread supplier : suppliersList){
            supplier.setBuffer(materialList);
        }

        //Start simulation
        try {
            simulation(day,factoryList,suppliersList, materialList);
        }catch (Exception e){
            System.out.println(e);
        }

        // Summery
        System.out.println("%-11s >>  Summary");
        Collections.sort(productList);
        for(Product product : productList){
            System.out.printf("%-11s >>  Total %10s =    %2d",Thread.currentThread().getName(), product.getName(), product.getLotSize());
        }
    }

    //Read file and insert data to each class
    public static int readFile(Scanner scan,ArrayList<FactoryThread> factoryList,ArrayList<SupplierThread> suppliersList, List<Material> materialList, ArrayList<Product> productList)throws Exception {

        int day = 0;

        //Read file each line
        while (scan.hasNext()) {
            try {
                String line = scan.nextLine();
                String[] col = line.split(",");
                String dataType = col[0].trim();

                if (dataType.equalsIgnoreCase("D")) {
                    day = Integer.parseInt(col[1].trim());
                    if (day <= 0) throw new Exception("Invalid day");
                } else if (dataType.equalsIgnoreCase("M")) {
                    for (int i = 1; i < col.length; i++) {
                        Material material = new Material(col[i].trim());
                        materialList.add(material);
                    }
                } else if (dataType.equalsIgnoreCase("S")) {
                    String supplierName = col[1].trim();
                    List<Integer> SupplierMaterial = new ArrayList<>();
                    for (int i = 2; i < col.length; i++) {
                        SupplierMaterial.add(Integer.parseInt(col[i].trim()));
                    }
                    SupplierThread supplierThread = new SupplierThread(supplierName, SupplierMaterial);
                    suppliersList.add(supplierThread);
                } else if (dataType.equalsIgnoreCase("F")) {
                    String factoryName = col[1].trim();
                    String productName = col[2].trim();
                    int amountPerDay = Integer.parseInt(col[3].trim());
                    List<Integer> factoryMaterial = new ArrayList<>();
                    productList.add(new Product(productName));
                    for (int i = 4; i < col.length; i++) {
                        factoryMaterial.add(Integer.parseInt(col[i].trim()));
                    }
                    FactoryThread factoryThread = new FactoryThread(factoryName, factoryMaterial, new Product(productName), amountPerDay);
                    factoryList.add(factoryThread);
                }else{
                    throw new Exception("Invalid data type");
                }
            }catch (Exception e){
                System.out.println(e);
            }
        }
        return day;
    }

    //Start simulation
    public static void simulation(int days,ArrayList<FactoryThread> factoryList,ArrayList<SupplierThread> suppliersList, List<Material> materialList)throws Exception{

        // Initiate simulation
        System.out.printf("%-11s >>  simulation days = %d\n",Thread.currentThread().getName(), days);

        // Display Supplier Thread
        for(SupplierThread supplier : suppliersList){
            System.out.printf("%-11s >>  %-11s  daily supply rates =",Thread.currentThread().getName(), supplier.getName());
            for (int i = 0 ; i < supplier.getMaterial().size(); i++){
                System.out.printf("%4d %s",supplier.getMaterial().get(i),materialList.get(i).getName());
            }
            System.out.println();
        }

        // Display Factory Thread
        for(FactoryThread factory : factoryList){
            System.out.printf("%-11s >>  %-11s  daily use    rates =",Thread.currentThread().getName(), factory.getName());
            for (int i = 0 ; i < factory.getMaterial().size(); i++){
                System.out.printf("%4d %s",factory.getMaterial().get(i),materialList.get(i).getName());
            }
            System.out.printf("    producing %3d %s\n", factory.getAmountPerDay(),factory.getProduct().getName());
        }

        // Start all Thread
        CountDownLatch latch = new CountDownLatch(factoryList.size() + suppliersList.size());
        for(SupplierThread supplier : suppliersList){
            supplier.setLatch(latch);
            supplier.start();
        }
        for(FactoryThread factory : factoryList){
            factory.setLatch(latch);
            factory.start();
        }

        // Start loop day
        for (int i=1 ; i<=days; i++){

            System.out.printf("\n%-11s >>  ---------------------------------------------------------\n",Thread.currentThread().getName());
            System.out.printf("%-11s >>  Day %d\n",Thread.currentThread().getName(), i);

            // Wake up all thread to do work
            for (SupplierThread supplier : suppliersList) {
                supplier.doWork();
            }
            for (FactoryThread factory : factoryList) {
                factory.doWork();
            }


            //wait for all thread to finish before next day
            latch.await();
        }

        // Terminate all thread
        for(SupplierThread supplier : suppliersList){
            supplier.terminate();
        }
        for(FactoryThread factory : factoryList){
            factory.terminate();
        }
    }
}
