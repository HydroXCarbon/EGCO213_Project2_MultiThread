//6513135 Purin Pongpanich
//6513161 Jarupat Chodsitanan
//6513163 Chalisa Buathong
package Project2_135.module;

import java.util.List;
import java.util.concurrent.CountDownLatch;

abstract class BaseThread extends Thread {

    // Variables
    protected List<Integer> material;
    protected List<Material> shareMaterial;
    protected boolean shouldRun = false;
    protected boolean shouldTerminate = false;
    protected CountDownLatch latch;

    // Constructor
    public BaseThread(String name) {
        super(name);
    }

    // Set latch
    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    // Access material
    public List<Integer> getMaterial() {
        return material;
    }

    // Set buffer
    public void setBuffer(List<Material> shareMaterial) {
        this.shareMaterial = shareMaterial;
    }
}
