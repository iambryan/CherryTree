package register

import model.data

sealed trait Registerable extends scala.AnyRef {

}

object Registerable {
  case class Node(a: Seq[data.Node], val needsClone: Boolean) extends Registerable
  case class Text(a: Seq[data.Text]) extends Registerable
  case class Unicode(a: data.Unicode) extends Registerable
}
