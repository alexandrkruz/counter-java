
import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

    static final int THREADS = 4;
    static final int HITS = 50_000;
    static final int TOTAL = THREADS * HITS;

    static int broken;
    static final AtomicInteger safe = new AtomicInteger();
    static volatile int visible;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(
                THREADS + " потоки × " + HITS
                        + " інкрементів = " + TOTAL + " очікуваних"
        );

        // Частина 1. Звичайне поле int
        System.out.println("\nзвичайний int, broken++:");
        for (int run = 1; run <= 3; run++) {
            broken = 0;
            load(() -> broken++);
            line(run, broken);
        }

        // Частина 2. Атомарний лічильник
        System.out.println("\nAtomicInteger, safe.incrementAndGet():");
        for (int run = 1; run <= 3; run++) {
            safe.set(0);
            load(() -> safe.incrementAndGet());
            line(run, safe.get());
        }

        // Частина 3. volatile не робить ++ атомарним
        System.out.println("\nvolatile int, visible++:");
        for (int run = 1; run <= 3; run++) {
            visible = 0;
            load(() -> visible++);
            line(run, visible);
        }

        System.out.println(
        );
        System.out.println(

        );
    }

    static void load(Runnable inc) throws InterruptedException {
        Thread[] w = new Thread[THREADS];

        for (int t = 0; t < THREADS; t++) {
            w[t] = new Thread(() -> {
                for (int i = 0; i < HITS; i++) {
                    inc.run();
                }
            });
        }

        for (Thread x : w) {
            x.start();
        }

        for (Thread x : w) {
            x.join();
        }
    }

    static void line(int run, int got) {
        System.out.println(
                "  прогін " + run + ": " + got
                        + ", загублено " + (TOTAL - got)
        );
    }
}
