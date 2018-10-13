package utils

class ListUtil {

  /**
    * Flattens a nested array into a linear list
    *
    * @example
    * flatten(List(List(1, 2, List(4, 5, List(6, 7)))))
    * // returns List(1, 2, ,4, 5, 6, 7)
    *
    * @param l
    * @return
    */
  def flatten(l: List[Any]): List[Any] = l match {
    case Nil => Nil
    case (h:List[_])::tail => flatten(h):::flatten(tail)
    case h::tail => h::flatten(tail)
  }

  /**
    * List split
    *
    * @example
    * val test = List("A", "B", "C", "A", "D", "E")
    * split(test, A)
    * // result: List(List("B", "C"), List("D", "E"))
    *
    * @param l
    * @param i
    * @return
    */
  def split(l: List[String], i:String): List[List[String]] = {
    l match {
      case List() => List()
      case _ =>
        val (h, t) = l.span(a => a != i)
        List(h) ++ split(t.drop(1), i)
    }
  }
}