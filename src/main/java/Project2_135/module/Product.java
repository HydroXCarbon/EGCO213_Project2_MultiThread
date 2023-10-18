package Project2_135.module;

public class Product implements Comparable<Product> {

    private final String name;
    private int lotSize = 0;

    public Product(String name){
        this.name = name;
    }

    // Add lot size
    synchronized public void addLotSize(int lotSize){
        this.lotSize += lotSize;
    }

    // Access name
    public String getName(){
        return name;
    }

    // Access lot size
    public int getLotSize(){
        return lotSize;
    }

    @Override
    public int compareTo(Product other) {
        if (this.lotSize > other.lotSize) {
            return -1;
        } else if (this.lotSize < other.lotSize) {
            return 1;
        }

        return this.name.compareTo(other.name);
    }
}
