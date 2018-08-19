package model.operation

import model._
import Type.Type
import model.operation.Node.Replace
import model.range.IntRange

import scala.util.Random

abstract sealed class Content extends Operation[data.Content] {
  override type This = Content

  private [model] def transform(d: data.Content, m: mode.Content): (mode.Content, Boolean)
}

object Content extends OperationObject[data.Content, Content] {

  abstract sealed class Code extends operation.Content

  case class CodeContent(op: operation.Unicode) extends Code {
    override def ty: Type = op.ty
    override def apply(d: data.Content): data.Content = {
      d match {
        case c: data.Content.Code => c.copy(unicode = op(c.unicode))
        case _ => throw new IllegalStateException("Not applicable operation")
      }
    }

    override def transform(d: data.Content, m: mode.Content): (mode.Content, Boolean) = (m, false)

    override def reverse(d: data.Content): Content = copy(op = op.reverse(d.asInstanceOf[data.Content.Code].unicode))

    override def merge(before: Any): Option[Content] = before match {
      case CodeContent(o) => op.merge(o).map(CodeContent)
      case _ => None
    }

    override def mergeForUndoer(before: Content): Option[(Content, Boolean)] = before match {
      case Rich(be) => op.mergeForUndoer(be).map(a => (Rich(a._1), a._2))
      case _ => None
    }

    override def isEmpty: Boolean = op.isEmpty
  }
  case class CodeLang(lang: String) extends Code {
    override def ty: Type = Type.AddDelete
    override def apply(d: data.Content): data.Content = {
      d match {
        case c: data.Content.Code => c.copy(lang = lang)
        case _ => throw new IllegalStateException("Not applicable operation")
      }
    }

    override def transform(d: data.Content, m: mode.Content): (mode.Content, Boolean) = (m, false)

    override def reverse(d: data.Content): Content = CodeLang(d.asInstanceOf[data.Content.Code].lang)

    override def merge(before: Any): Option[Content] = before match {
      case CodeLang(b) => Some(this)
      case _ => None
    }

    override def isEmpty: Boolean = false
  }

  case class Rich(op: operation.Rich) extends operation.Content {
    override def ty: Type = op.ty
    override def apply(d: data.Content): data.Content = {
      d match {
        case c: data.Content.Rich => c.copy(content = op(c.content))
        case _ => throw new IllegalStateException("Not applicable operation")
      }
    }

    override def transform(d: data.Content, m: mode.Content): (mode.Content, Boolean) = m match {
      case b: mode.Content.Rich => op.transformRich(d.asInstanceOf[model.data.Content.Rich].content, b)
      case _ => throw new IllegalStateException("What")
    }

    override def reverse(d: data.Content): Content = copy(op = op.reverse(d.asInstanceOf[data.Content.Rich].content))

    override def mergeForUndoer(before: Content): Option[(Content, Boolean)] = before match {
      case Rich(be) => op.mergeForUndoer(be).map(a => (Rich(a._1), a._2))
      case _ => None
    }

    override def merge(before: Any): Option[Content] = before match {
      case Rich(o) => op.merge(o).map(Rich)
      case _ => None
    }

    override def isEmpty: Boolean = op.isEmpty
  }


  override val pickler: Pickler[Content] = new Pickler[Content] {
    override def pickle(obj: Content)(implicit state: PickleState): Unit = {
      import state.enc._
      obj match {
        case CodeContent(u) =>
          writeInt(0)
          Unicode.pickler.pickle(u)
        case CodeLang(l) =>
          writeInt(1)
          writeString(l)
        case Rich(u) =>
          writeInt(2)
          operation.Rich.pickler.pickle(u)
      }
    }

    override def unpickle(implicit state: UnpickleState): Content = {
      import state.dec._
      readInt match {
        case 0 =>
          Content.CodeContent(Unicode.pickler.unpickle)
        case 1 =>
          Content.CodeLang(readString)
        case 2 =>
          Content.Rich(operation.Rich.pickler.unpickle)
      }
    }
  }

  override def random(d: data.Content, r: Random): Content = {
    d match {
      case data.Content.Rich(content) => Rich(operation.Rich.random(content, r))
      case data.Content.Code(unicode, lb) =>
        if (r.nextBoolean()) {
          CodeContent(operation.Unicode.random(unicode, r))
        } else {
          CodeLang(r.nextInt(10).toString)
        }
    }
  }
}
