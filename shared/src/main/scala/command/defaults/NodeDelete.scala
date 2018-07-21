package command.defaults

import command.CommandCategory
import command.Key._
import doc.{DocState, DocTransaction}
import model.{cursor, operation}
import model.range.IntRange

class NodeDelete extends CommandCategory("deleting nodes") {


  private def deleteNodeRange(a: DocState, rr: model.range.Node): DocTransaction = {
    val parent = a.node(rr.parent)
    val r = rr.copy(childs = IntRange(rr.childs.start, rr.childs.until min parent.childs.size))
    DocTransaction(Seq(operation.Node.Delete(r)), {
      val (nowPos, toPos) = if (a.node.get(r.until).isDefined) {
        (r.until, r.start)
      } else if (r.childs.start > 0) {
        val p = r.parent :+ (r.childs.start - 1)
        (p, p)
      } else {
        (r.parent, r.parent)
      }
      Some(model.mode.Node.Content(toPos, a.node(nowPos).content.defaultNormalMode()))
    })
  }


  new Command {
    override def repeatable: Boolean = true
    override def description: String = "delete current node, and more sibling nodes under if has N"
    override val defaultKeys: Seq[KeySeq] = Seq("dd") // siblings not lines
    override def available(a: DocState): Boolean = a.isNormal
    override def action(a: DocState, count: Int): DocTransaction = {
      val r = a.asNormal._1
      if (r == cursor.Node.root) DocTransaction.empty
      else deleteNodeRange(a, model.range.Node(a.asNormal._1, count))
    }
  }

  new Command {
    override def description: String = "delete selected nodes"
    override val defaultKeys: Seq[KeySeq] = Seq("d", "D", "x", "X", Delete)
    override def available(a: DocState): Boolean = a.isNodeVisual
    override def action(a: DocState, count: Int): DocTransaction = a.mode match {
      case Some(v@model.mode.Node.Visual(_, _)) =>
        v.minimalRange.map(r => deleteNodeRange(a, r)).getOrElse(DocTransaction.empty)
      case _ => throw new IllegalArgumentException("Invalid command")
    }
  }
}
