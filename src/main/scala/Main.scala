import scala.annotation.tailrec
import scopt.OParser

import java.nio.file.Files


object Main extends App {

  case class NodePath(name: String, con: String, cost: Int)

  OParser.parse(InputArgs.parser, args, InputArgs.Config()) match {
    case Some(config) =>
      val paths = Files.readString(config.input.toPath).split(",")
        .map(_.replace(" ", ""))
        .map(np => NodePath(np.charAt(0).toString, np.charAt(1).toString, np.charAt(2).toString.toInt)).toSeq


      if(config.all) {
        //Question 1. The average latency of the trace A-B-C.
        println(" 1. " + avgTraceLatency(paths, Seq("A", "B", "C")).getOrElse("NO SUCH TRACE") )
        //Question 2. The average latency of the trace A-D.
        println(" 2. " + avgTraceLatency(paths, Seq("A", "D")).getOrElse("NO SUCH TRACE"))
        //Question 3. The average latency of the trace A-D-C.
        println(" 3. " + avgTraceLatency(paths, Seq("A", "D", "C")).getOrElse("NO SUCH TRACE"))
        //Question 4. The average latency of the trace A-E-B-C-D
        println(" 4. " + avgTraceLatency(paths, Seq("A", "E", "B", "C", "D")).getOrElse("NO SUCH TRACE"))
        //Question 5. The average latency of the trace A-E-D.
        println(" 5. " + avgTraceLatency(paths, Seq("A", "E", "D")).getOrElse("NO SUCH TRACE"))
        // Question 6. The number of traces originating in service C and ending in service C with a maximum of 3 hops.
        val threeHopC = exploreDepth(paths,"C","C",3).filter(_.length <= 3)
        println(" 6. " + threeHopC.length)
        // Question 7. The number of traces originating in A and ending in C with exactly 4 hops.
        val fourHopAC = exploreDepth(paths,"A","C",4).filter(_.length == 4)
        println(" 7. " + fourHopAC.length)
        // Question 8. The length of the shortest trace (in terms of latency) between A and C.
        println(" 8. " + shortestLatency(paths, "A", "C").map(_.cost).sum)
        // Question 9. The length of the shortest trace (in terms of latency) between B and B.
        println(" 9. " + shortestLatency(paths, "B", "B").map(_.cost).sum)
        // Question 10. The number of different traces from C to C with an average latency of less than 30
        val lessThanThirty = exploreLatency(paths, "C", "C", 30)
        println("10. " + lessThanThirty.map(_.map(_.cost).sum).length)
      }


    case _ =>
  }

  def avgTraceLatency(graph: Seq[NodePath],
                      trace: Seq[String]): Option[Int] = {

    val res = traverse(graph, trace)
    if(res.isEmpty) None
    else Some(res.map(r => r.map(_.cost).sum).sum/res.length)
  }

  /**
   *
   * @param graph   A sequence of node paths representing a graph
   * @param trace   The desired path the traverse through the graph
   * @param myNode  The node we are currently on
   * @param myPath  The path we took to get where we are, very Robert Frost
   * @param results The list of paths that lead to the road less traveled
   * @return List of accumulated results from the recursive call
   */
  def traverse(graph: Seq[NodePath],
               trace: Seq[String],
               myNode: String = "",
               myPath: Seq[NodePath] = Seq.empty,
               results: Seq[Seq[NodePath]] = Seq.empty
              ): Seq[Seq[NodePath]] = {

    if (trace.isEmpty) if (myPath.nonEmpty) results.appended(myPath) else results //Reached the end of the line
    else if (myNode.isBlank) traverse(graph, trace, trace.head, Seq.empty, Seq.empty) //Startup condition
    else if (!myNode.equals(trace.head)) results //Took a wrong turn
    else if (trace.length == 1 && trace.head.equals(myNode)) results.appended(myPath)
    else {
      val availablePaths = graph.filter(_.name.equals(myNode)) //Get a list of paths that are okay to take
      /* In order to understand recursion you must first understand recursion, in any case flatten results out */
      val pathRes = availablePaths.flatMap(np => traverse(graph, trace.drop(1), np.con, myPath.appended(np), results))
      results.appendedAll(pathRes)
    }

  }

  /**
   * Function to explore the graph for paths to and from nodes with a maximum search depth
   * @param graph The graph to explore
   * @param myNode The node we are currently on
   * @param goalNode The node we are trying to reach
   * @param maxDepth The maximum number of hops we are willing to take
   * @param myPath The path that we took to get to myNode
   * @param results An accumulated list of results
   * @param earlyStop Boolean to stop bouncing when we reach the end instead of continuing on
   * @return A list of paths from the start node to the end node
   */
  def exploreDepth(graph: Seq[NodePath],
                   myNode: String,
                   goalNode: String,
                   maxDepth: Int,
                   myPath: Seq[NodePath] = Seq.empty,
                   results: Seq[Seq[NodePath]] = Seq.empty,
                   earlyStop: Boolean = false
                  ): Seq[Seq[NodePath]] = {

    if (maxDepth < 0) results
    else if (earlyStop && myNode.equals(goalNode) && myPath.nonEmpty) results.appended(myPath)
    else {
      val availablePaths: Seq[NodePath] = graph.filter(_.name.equals(myNode)) //Get a list of paths that are okay to take
      val explored = availablePaths.flatMap((path: NodePath) => exploreDepth(graph, path.con, goalNode, maxDepth - 1, myPath.appended(path), results, earlyStop))

      if (myNode.equals(goalNode) && myPath.nonEmpty) results.appended(myPath).appendedAll(explored)
      else results.appendedAll(explored)
    }
  }


  /**
   * Function to explore the graph for paths to and from nodes with a maximum latency for a given path
   * @param graph The graph to explore
   * @param myNode The node we are currently on
   * @param goalNode The node we are trying to reach
   * @param maxLatency The maximum amount of accumulated latency from start until we stop searching
   * @param myPath The path that we took to get to myNode
   * @param results An accumulated list of results
   * @return A list of paths from the start node to the end node
   *
   * Note: This function is almost identical to the exploreDepth with a different stopping condition. If this were a
   * real program that kind of anti DRY pattern would dictate a better solution.
   */
  def exploreLatency(graph: Seq[NodePath],
                     myNode: String,
                     goalNode: String,
                     maxLatency: Int,
                     myPath: Seq[NodePath] = Seq.empty,
                     results: Seq[Seq[NodePath]] = Seq.empty,
                    ): Seq[Seq[NodePath]] = {

    if (myPath.map(_.cost).sum >= maxLatency) results // Check if we have exceeded our max latency
    else {
      val availablePaths: Seq[NodePath] = graph.filter(_.name.equals(myNode)) //Get a list of paths that are okay to take
      val explored = availablePaths.flatMap((path: NodePath) => exploreLatency(graph, path.con, goalNode, maxLatency, myPath.appended(path), results))

      if (myNode.equals(goalNode) && myPath.nonEmpty) results.appended(myPath).appendedAll(explored)
      else results.appendedAll(explored)
    }
  }

  /**
   * A recursive search function to find the shortest latency between nodes by starting from 1 and incrementing the
   * latency until a path is found. This is similar to the solution of finding a prime number
   * @param graph The graph to explore
   * @param startNode The node to start on
   * @param endNode The node to end on
   * @param startLatency The starting point for the latency which will continually double until a solution is found
   * @return A sequence of node paths representing the path with the shortest latency
   */
  @tailrec
  def shortestLatency(graph: Seq[NodePath], startNode: String, endNode: String, startLatency: Int = 1): Seq[NodePath] = {

    val result = exploreLatency(graph, startNode, endNode, startLatency)
    if(result.nonEmpty) {
      result.minBy(np => np.map(_.cost).sum)
    } else {
      shortestLatency(graph, startNode, endNode, startLatency * 2)
    }

  }

}
