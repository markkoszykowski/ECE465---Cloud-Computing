# Single-Node Multi-Threaded Dijkstra's Algorithm for solving All Pairs Shortest Path

The implementation uses threads to concurrently execute multiple instances of the Single Source Shortest Path version of Dijkstra's algorithm. The current implementation
assumes that the number of "processors" is less than the number of vertices in the graph. Therefore, each thread handles an entire execution of Dijkstra's SSSP algorithm.
Future implementations will handle the instance where the number of processors exceeds the number of vertices, requiring multiple processors to work simultaneously on a given
instance of the SSSP problem.

Done sequentially, the algorithm has a complexity of O(|V|<sup>3</sup>). Done in parallel, the complexity is reduced by a factor of p, where p is the number of threads.
The new complexity is therefore O(|V|<sup>3</sup>/p).

Using a randomly generated graph with 1000 nodes and at least 10 edges per node, the sequential run-time is 7891 ms. With only 4 threads, this run-time is reduced to
2551 ms.

## Usage

Once you have cloned the Git repository, simply enter the root and run

    mvn clean install

At this point, a 'target' folder should have been created. To execute the compiled code, enter the 'target' folder and run

    java -jar ece465_hw1b-v1.2.jar

## Tests

| # of Vertices | Min. Edges Per Node | Single Thread | 4 Threads |
|------------|------------|-------------|-------------|
| 100 | 5 | 24 ms | 19 ms |
| 1000 | 10 | 7891 ms | 2551 ms |

## v1.2 (1b) Updates

 - Improved tooling -> (tentatively) added building and cleaning scripts
 - Added executable .jar file to build routine
 - Tentatively added logging

## Authors

 - Mark Koszykowski
 - Omar Thenmalai

## References

[Shortest Path Algorithms](https://web.stanford.edu/class/cs97si/07-shortest-path-algorithms.pdf)