
object Main extends App {
  val nodeGraph: String = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7"

  case class NodePath(name: String, con: String, cost: Int)

  val paths = nodeGraph.split(",")
    .map(_.replace(" ", ""))
    .map(np => NodePath(np.charAt(0).toString, np.charAt(1).toString, np.charAt(2).toString.toInt)).toSeq


  def traverse(graph: Seq[NodePath], trace: Seq[String], myNode: String = "", myPath: Seq[NodePath] = Seq.empty, results: Seq[Seq[NodePath]] = Seq.empty): Seq[Seq[NodePath]] = {
    if (trace.isEmpty) results.appended(myPath) //Reached the end of the line
    else if (myNode.isBlank) traverse(graph, trace, trace.head, Seq.empty, Seq.empty) //Startup condition
    else if (!myNode.equals(trace.head)) results //Took a wrong turn
    else if (trace.length == 1 && trace.head.equals(myNode)) results.appended(myPath)
    else {
      val availablePaths = graph.filter(_.name.equals(myNode)) //Get a list of paths that are okay to take
      /* In order to understand recursion you must first understand recursion, in any case flatten results out */
      val pathRes = availablePaths.flatMap(np => traverse(graph, trace.drop(1), np.con, myPath.appended(np), results ))
      results.appendedAll(pathRes.filter(_.nonEmpty)) //Don't bother keeping the empty ones
    }
  }

  val res = traverse(paths, Seq("A","E","D"))

  println(res)
}
