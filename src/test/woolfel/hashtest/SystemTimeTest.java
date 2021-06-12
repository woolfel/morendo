package woolfel.hashtest;

public class SystemTimeTest {

    private static long c = 1;
    
    public static long nextCounter() {
        return c++;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        int count = 10000000;
        long start = System.currentTimeMillis();
        for (int idx=0; idx < count; idx++) {
            // long t = nextCounter();
        }
        long end = System.currentTimeMillis();
        long el = end - start;
        System.out.println("Elapsed time for loop w/o time " + (el));
        long start1 = System.currentTimeMillis();
        for (int idx=0; idx < count; idx++) {
            // long t1 = System.currentTimeMillis();
        }
        long end1 = System.currentTimeMillis();
        long el1 = end1 - start1;
        System.out.println("Elapsed time for System.currentTimeMillis " + (el1));
        // now test nanotime
        long start2 = System.currentTimeMillis();
        for (int idx=0; idx < count; idx++) {
           // long t2 = System.nanoTime();
        }
        long end2 = System.currentTimeMillis();
        long el2 = end2 - start2;
        System.out.println("Elapsed time for System.nanoTime " + (el2));
        
    }

}
