# Pincode Data Scraper and Retriever

This Java project aims to scrape pincode data from various sources on the web and store it in an efficient manner for
easy retrieval. The primary goal of this project is to explore and learn about Java concurrency concepts while building
a practical application.

## Project Overview

The Pincode Data Scraper and Retriever is a Java application that fetches pincode information
from https://api.postalpincode.in/pincode/ and stores it in a structured format. The application utilizes concurrent
programming techniques to improve performance and handle large amounts of data efficiently.

# Project Tracker

## Iteration - 1

* Standard java multithreaded program where each thread is given a set of pincodes for which the data should be
  retrieved.
* In the initial version, each thread writes the output to a file specific to the thread._(To avoid concurrency issues,
  we will see how this can be handled in future)_
* Key issue here is that the API calls are synchronous in nature i.e. the thread moves on the next request only after
  completing the current request. This isn't the most optimal approach, since for the duration of the API response the
  thread would be idle. This response time for this specific API is quite large.

#### Benchmarking against a sample dataset
* With a batchSize of 1000 and 2 threads, time taken to scrape 2000 ids is ~600 seconds


