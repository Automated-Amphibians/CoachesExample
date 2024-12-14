package org.aa8426.examples.lambdas;

import java.util.concurrent.atomic.AtomicInteger;

class Counter {
    private int count = 0;

    // Increment the counter
    public void increment() {
        count++; // This is not atomic!
    }

    // Get the current count
    public int getCount() {
        return count;
    }
}

public class RaceConditionDemo {
    public static void main(String[] args) {
        Counter counter = new Counter();
        AtomicInteger ai = new AtomicInteger(); 
        AtomicInteger aiRight = new AtomicInteger();

        // Create two threads that increment the counter
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                //System.out.println(i);
                ai.set(ai.get() + 1);
                counter.increment();
                aiRight.addAndGet(1);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                //System.out.println(i);
                counter.increment();
                ai.set(ai.get() + 1);
                aiRight.addAndGet(1);
            }
        });

        // Start both threads
        t1.start();
        t2.start();

        // Wait for both threads to finish
        try {
            t1.join(); 
            System.out.println("eh");
            t2.join();
            System.out.println("eh");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print the final count
        System.out.println("Final Count: " + counter.getCount());
        System.out.println("Final Count AI: " + ai.get());
        System.out.println("Final Count AI Right: " + aiRight.get());
    }
}
