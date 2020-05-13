package com.test.async.jdk.completefuture;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class CompletFuturewhenComplete {

    private static Random rand = new Random();
    private static long t = System.currentTimeMillis();
    static int getMoreData() {
        System.out.println("begin to start compute");
        int i = 1/0;
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end to start compute. passed " + (System.currentTimeMillis() - t)/1000 + " seconds");
        return rand.nextInt(1000);
    }
    public static void main(String[] args) throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(CompletFuturewhenComplete::getMoreData);
        CompletableFuture<Integer> f = future.whenComplete((v, e) -> {
            System.out.println("-----"+v);
            System.out.println("====="+e);
            v = v + 10000;
        });


//        System.out.println(f.get());
        System.in.read();
    }

}
