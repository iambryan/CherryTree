package shared
import scala.util.Random



package object ot {



  object StringDoc extends AtomicDoc[String] {
    override def generateRandomChange(data: String, random: Random): AtomicOt.Operation[String] =
      AtomicOt.Operation(generateRandomData(random))
    override def generateRandomData(random: Random): String =
      random.nextString(10)
  }
  type StringOperation = AtomicOt.Operation[String]
  type StringConflict = AtomicOt.Conflict[String]
  type StringSelection = AtomicDoc.Selection

  object IntOt extends AtomicDoc[Int] {
    override def generateRandomChange(data: Int, random: Random): AtomicOt.Operation[Int] =
      AtomicOt.Operation(generateRandomData(random))
    override def generateRandomData(random: Random): Int =
      random.nextInt()
  }
  type IntOperation = AtomicOt.Operation[Int]
  type IntConflict = AtomicOt.Conflict[Int]
  type IntSelection = AtomicDoc.Selection


  case class Segment(from: Int, to: Int) {
    def contains(p: Int): Boolean = p >= from && p <= to
    def size: Int = to - from + 1
  }


  def transformAfterAdded(point: Int, size: Int, p: Int): Int = {
    if (p < point) {
      p
    } else {
      p + size
    }
  }

  def transformAfterDeleted(s: Segment, p: Int): Option[Int] = {
    if (p < s.from) {
      Some(p)
    } else if (s.contains(p)) {
      None
    } else {
      Some(p - s.size)
    }
  }

  /**
    * @return None if either side of `s` is deleted
    */
  def transformAfterDeleted(d: Segment, f: Segment): Option[Segment] = {
    val l = transformAfterDeleted(d, f.from)
    val r = transformAfterDeleted(d, f.to)
    (l, r) match {
      case (Some(ll), Some(rr)) => Some(Segment(ll, rr))
      case _ => None
    }
  }

  def transformDeletingSegmentAfterDeleted(d: Segment, f: Segment): Option[Segment] = {
    val l = transformAfterDeleted(d, f.from)
    val r = transformAfterDeleted(d, f.to)
    (l, r) match {
      case (Some(ll), Some(rr)) => Some(Segment(ll, rr))
      case (Some(ll), None) => Some(Segment(ll, d.from - 1))
      case (None, Some(rr)) => Some(Segment(d.from, rr))
      case (None, None) =>  None
    }
  }
}