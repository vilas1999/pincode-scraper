package org.example.iteration.one;

import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        Gson gson = new Gson();
        HttpClient httpClient = HttpClient.newHttpClient();

        long pincodeRangeStart = 562000;
        long pincodeRangeEnd = 563000;

        long batchSize = 1000;
        int batchCount = (int) ((pincodeRangeEnd - pincodeRangeStart) / batchSize);
        long startTime = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>();
        for (int index = 0; index <= batchCount; index++) {
            long batchPincodeStart = pincodeRangeStart + (index * batchSize) + 1;
            long batchPincodeEnd = pincodeRangeStart + ((index + 1) * batchSize);
            Thread thread = new Thread(new RangeRequestHandler(batchPincodeStart, batchPincodeEnd, httpClient, gson, index));
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) / 1000 + " seconds");
    }
}