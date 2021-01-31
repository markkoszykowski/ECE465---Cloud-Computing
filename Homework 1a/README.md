## Single-Node Multi-Threaded Dijkstra's Algorithm for solving All Pairs Shortest Path

The implementation uses threads to concurrently execute multiple instances of the Single Source Shortest Path version of Dijkstra's algorithm. The current implementation
assumes that the number of "processors" is less than the number of vertices in the graph. Therefore, each thread handles an entire execution of Dijkstra's SSSP algorithm.
Future implementations will handle the instance where the number of processors exceeds the number of vertices, requiring multiple processors to work simultaneously on a given
instance of the SSSP problem.

Done sequentially, the algorithm has a complexity of O(|V|<sup>3</sup>). Done in parallel, the complexity is reduced by a factor of p, where p is the number of threads.
The next complexity is therefore O(|V|<sup>3</sup>/p).

Using a randomly generated graph with 1000 nodes and at least 10 edges per node, the sequential run-time is 7891 ms. With only 4 threads, this run-time is reduced to
2551 ms.
