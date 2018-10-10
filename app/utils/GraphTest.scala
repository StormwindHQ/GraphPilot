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
    println(z)
    def n(outer: Int): z.NodeT = z get outer  // look up 'outer' that is known to be contained
    println(n(1) shortestPathTo n(4))
    println(n(1) pathTo n(4))
    println(n(2) pathTo n(4))
    println(n(1) pathUntil (_.degree >= 90))
    println("finding any!")
    println(n(2) findSuccessor (_.outDegree >  1))
    println(n(1))
    // Core User Guide: Inner and Outer Objects
    println(z.edges)
    println(z.edges.head)
    println(z.edges.head.toOuter)
    val traversal = z.get(1)
    println(traversal.pathTo(z.get(4)))
    println(z.get(2).pathTo(z.get(4)))


    def getAllPaths(g: z.NodeT, paths: List[Any]): Unit = {
      val directs = g.diSuccessors.toList

      if (directs.length == 0) {
        paths
      } else {
        println(directs(0).diSuccessors, paths)
        getAllPaths(directs(0), paths :+ directs(0))
      }
      /* for (node <- directs) {
        getAllPaths(node, paths :+ node)
      } */
    }
    val accum = List[Any]()

    println(getAllPaths(z.get(1), accum))
    println(accum)
  }
}

