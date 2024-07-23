package org.example.iteration.one;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//TODO: Improve exception handling
public class RangeRequestHandler implements Runnable {

    public RangeRequestHandler(long startCount, long endCount, HttpClient httpClient, Gson gson, int threadCount) {
        this.startCount = startCount;
        this.endCount = endCount;
        this.httpClient = httpClient;
        this.gson = gson;
        this.threadCount = threadCount;
    }

    long startCount;
    long endCount;
    HttpClient httpClient;
    Gson gson;
    int threadCount;

    private static void handleResponse(HttpResponse<String> response, Gson gson, FileWriter fileWriter) {
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);

        if (jsonArray.size() > 2) {
            throw new RuntimeException("Found scenario with more than 1 pincode");
        }

        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

        if (jsonObject.get("Message").getAsString().startsWith("Number of pincode(s)")) {
            JsonArray internalJsonArray = jsonObject.getAsJsonArray("PostOffice");
            for (JsonElement internalJsonElement : internalJsonArray) {
                String finalResult = getFinalResult(internalJsonElement);
                try {
                    fileWriter.write(finalResult);
                    fileWriter.write(System.lineSeparator());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static String getFinalResult(JsonElement internalJsonElement) {
        JsonObject internalJsonObject = internalJsonElement.getAsJsonObject();
        String name = internalJsonObject.get("Name").getAsString();
        String district = internalJsonObject.get("District").getAsString();
        String state = internalJsonObject.get("State").getAsString();
        String pincode = internalJsonObject.get("Pincode").getAsString();
        return String.format("%s,%s,%s,%s", name, district, state, pincode);
    }

    @Override
    public void run() {

        System.out.println("Starting for range " + startCount + " " + endCount);

        String filePath = String.format("/Users/mvilas/IdeaProjects/Experimental/output/%s", threadCount);

        FileWriter fileWriter = null;
        try {
            checkAndCreateIfFileDoesntExists(filePath);
            fileWriter = new FileWriter(filePath, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (long i = startCount; i < endCount; i++) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("https://api.postalpincode.in/pincode/%s", i)))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> responseFuture = null;
            try {
                responseFuture = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            handleResponse(responseFuture, gson, fileWriter);
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAndCreateIfFileDoesntExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            boolean fileCreationResult = file.createNewFile();
            if (fileCreationResult) {
                System.out.println("Created file at path: " + filePath);
            } else {
                System.out.println("Failed to create file at path: " + filePath);
            }
        }
    }
}
