package command.defaults

import command.{CommandCategory, CommandInterface, Motion}
import command.Key.KeySeq
import doc.{DocState, DocTransaction}
import model.cursor.Node
import model.{cursor, data, mode, operation}
import model.data.{Node, Rich, Text, Unicode}
import model.operation.Node
import model.range.IntRange
import register.Registerable

class YankPaste extends CommandCategory("registers, yank and paste") {


  new Command {
    override val description: String = "yank current node (without childs)"
    override val defaultKeys: Seq[KeySeq] = Seq("yy")
    override def available(a: DocState): Boolean = a.isNormal

    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      val c = a.asNormal._1
      commandState.yank(Registerable.Node(Seq(data.Node(a.node(c).content, Seq.empty))), isDelete = false)
      DocTransaction.empty
    }
  }

  new Command {
    override val description: String = "yank current node"
    override val defaultKeys: Seq[KeySeq] = Seq("yr") // siblings not lines
    override def available(a: DocState): Boolean = a.isNormal

    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      val c = a.asNormal._1
      commandState.yank(Registerable.Node(Seq(a.node(c))), isDelete = false)
      DocTransaction.empty
    }
  }

  new Command {
    override val description: String = "yank selected nodes"
    override val defaultKeys: Seq[KeySeq] = Seq("y")
    override def available(a: DocState): Boolean = a.isNodeVisual

    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      a.mode match {
        case Some(v@model.mode.Node.Visual(fix, _)) =>
          val ns = if (v.fix == v.move && v.fix == cursor.Node.root) {
            Seq(a.node)
          } else {
            a.node(v.minimalRange.get)
          }
          commandState.yank(Registerable.Node(ns), isDelete = false)
          DocTransaction.mode(model.mode.Node.Content(fix, a.node(fix).content.defaultNormalMode()))
        case _ => throw new IllegalArgumentException("Invalid command")
      }
    }
  }

  new Command {
    override val description: String = "yank to line end"
    override val defaultKeys: Seq[KeySeq] = Seq("Y")
    override def available(a: DocState): Boolean = a.isRichNormal

    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      val (c, rich, normal) = a.asRichNormal
      commandState.yank(Registerable.Text(rich.copyTextualRange(IntRange(normal.range.start, rich.size))), isDelete = false)
      DocTransaction.empty
    }
  }

  new Command {
    override val description: String = "yank selected text"
    override val defaultKeys: Seq[KeySeq] = Seq("y")
    override def available(a: DocState): Boolean = a.isRichVisual

    override def action(a: DocState, count: Int, commandState: CommandInterface, key: Option[KeySeq], grapheme: Option[Unicode], motion: Option[Motion]): DocTransaction = {
      a.mode match {
        case Some(model.mode.Node.Content(pos, v@model.mode.Content.RichVisual(fix, _))) =>
          commandState.yank(Registerable.Text(a.rich(pos).copyTextualRange(v.merged)), isDelete = false)
          DocTransaction.mode(model.mode.Node.Content(pos, model.mode.Content.RichNormal(fix)))
        case _ => throw new IllegalArgumentException("Invalid command")
      }
    }
  }


  new Command {
    override val description: String = "yank range selected by motion"
    override def needsMotion: Boolean = true
    override val defaultKeys: Seq[KeySeq] = Seq("y")
    override protected def available(a: DocState): Boolean = a.isRichNormal
    override def repeatable: Boolean = true

    override def action(a: DocState, count: Int, commandState: CommandInterface, key: Option[KeySeq], grapheme: Option[Unicode], motion: Option[Motion]): DocTransaction = {
      val (cur, rich, normal) = a.asRichNormal
      if (rich.isEmpty) return DocTransaction.empty
      motion.foreach(m => {
        m.act(commandState, rich, count, normal.range, grapheme).foreach(r => {
          commandState.yank(Registerable.Text(rich.copyTextualRange(r)), isDelete = false)
        })
      })
      DocTransaction.empty
    }
  }


  abstract class PutCommand extends Command {
    override protected def available(a: DocState): Boolean = a.isNormal

    def putNode(a: DocState, at: cursor.Node, node: Seq[data.Node]): (Seq[operation.Node], mode.Node)
    def putText(rich: Rich, selection: IntRange, frag: Seq[Text]): (Seq[operation.Rich], mode.Content)
    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      commandState.retrieveSetRegisterAndSetToDefault() match {
        case Some(Registerable.Node(n)) =>
          if (n.nonEmpty) {
            val (cursor, normal) = a.asNormal
            val (op, mode) = putNode(a, cursor, n)
            DocTransaction(op, Some(mode))
          } else {
            DocTransaction.empty
          }
        case Some(Registerable.Text(n)) =>
          if (n.nonEmpty && a.isRichNormal) {
            val (cursor, rich, normal) = a.asRichNormal
            val (op, mode) = putText(rich, normal.range, n)
            DocTransaction(operation.Node.rich(cursor, op), Some(a.copyContentMode(mode)))
          } else {
            DocTransaction.empty
          }
        case Some(Registerable.Unicode(n)) =>
          ???
        case _ => DocTransaction.empty
      }
    }
  }

  new PutCommand {
    override val description: String = "put after"
    override def defaultKeys: Seq[KeySeq] = Seq("p")

    def putNode(a: DocState, at: cursor.Node, node: Seq[data.Node]): (Seq[operation.Node], mode.Node) = {
      val insertionPoint = insertPointAfter(a, at)
      (Seq(operation.Node.Insert(insertionPoint, node)), mode.Node.Content(insertionPoint, node.head.content.defaultNormalMode()))
    }

    override def putText(rich: Rich, selection: IntRange, frag: Seq[Text]): (Seq[operation.Rich], mode.Content) = {
      val op = operation.Rich.insert(selection.until, frag)
      val rg = Rich(frag).rangeEnd.moveBy(selection.until)
      (Seq(op), mode.Content.RichNormal(rg))
    }
  }

  new PutCommand {
    override val description: String = "put before"
    override def defaultKeys: Seq[KeySeq] = Seq("P")

    def putNode(a: DocState, at: cursor.Node, node: Seq[data.Node]): (Seq[operation.Node], mode.Node) = {
      val pt = if (at == cursor.Node.root) Seq(0) else at
      (Seq(operation.Node.Insert(pt, node)), mode.Node.Content(pt, node.head.content.defaultNormalMode()))
    }

    override def putText(rich: Rich, selection: IntRange, frag: Seq[Text]): (Seq[operation.Rich], mode.Content) = {
      val op = operation.Rich.insert(selection.start, frag)
      val rg = Rich(frag).rangeEnd.moveBy(selection.start)
      (Seq(op), mode.Content.RichNormal(rg))
    }
  }
}