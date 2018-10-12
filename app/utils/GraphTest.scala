package utils

import scalax.collection.Graph // or scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._

/* case class Flight(flightNo : String)(
  val departure: DayTime  = DayTime (0,0),
  val duration : Duration = Duration(0,0)) */

class GraphTest {
  def test(): Unit = {
    val g1 = Graph(3~1, 5)
    val g2 = Graph(UnDiEdge(3, 1), 5)
    val gA = Graph(3~>1.2)
    println(g1)
    println(g2)
    println(gA)
    val h = Graph(1~1, 1~2~3)
    println(h)

    // Example graph
    val nodes = List(1, 2, 3, 4, 5)
    val z = Graph(1~>2, 2~>3, 2~>4, 3~>4, 5~>4)

    def getAllPaths(g: z.NodeT, paths: List[z.NodeT]): List[Any] = {
      val directs = g.diSuccessors.toList

      if (directs.length == 0) {
        // Dead end
        paths
      } else if (directs.length == 1) {
        // Node with single direction, simply returns itself
        if (paths.length == 0) {
          // instead of appending successor, append itself
          getAllPaths(directs(0), paths :+ g :+ directs(0))
        } else {
          getAllPaths(directs(0), paths :+ directs(0))
        }
      } else {
        var newResult = List[z.NodeT]()
        directs.map(d => {
          getAllPaths(d, paths :+ d)
        })
      }
    }
    val accum = List[z.NodeT]()

    println(getAllPaths(z.get(1), accum))
  }
}

