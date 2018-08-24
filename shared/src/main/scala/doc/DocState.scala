package doc

import model.{cursor, data, mode, operation, transaction}
import model.cursor.Node
import model.data.{Atom, Rich}
import model.mode.Content.CodeInside
import model.range.IntRange
import settings.Settings


case class DocState(
  node: model.data.Node,
  zoom: cursor.Node,
  mode0: model.mode.Node,
  badMode: Boolean,
  userFoldedNodes: Map[String, Boolean]
) {
  def changeContentType(cur: cursor.Node,
    to: Option[data.Node.ContentType],
    opts: transaction.Node = Seq.empty
  ): DocTransaction = {
    val chidlren = if (!node(cur).has(data.Node.ChildrenType)) {
      to.flatMap(_.preferredChildrenType).map(a => operation.Node.AttributeChange(cur, data.Node.ChildrenType, Some(a))).toSeq
    } else {
      Seq.empty
    }
    DocTransaction(
      opts ++ Seq(operation.Node.AttributeChange(cur, data.Node.ContentType, to)) ++ chidlren, None)
  }

  def nodeRefRelative(uuid: String): String = {
    if (uuid == node.uuid) "" else model.data.Node.nodeRefRelative(uuid)
  }

  def goTo(cur: Node, settings: Settings, mustZoom: Boolean = false): DocTransaction = {
    DocTransaction(Seq.empty,
      Some(model.mode.Node.Content(cur, node(cur).content.defaultMode(settings.enableModal))),
      zoomAfter = Some(cur).filter(c => mustZoom || !currentlyVisible(c)))
  }


  def breakWhiteSpaceInserts: Boolean = mode.exists(_.breakWhiteSpaceInserts)


  def lookup(uuid: String) = node.lookup(uuid, cursor.Node.root)

  def quickSearch(tt: Seq[data.Unicode],
    heading: Boolean,
    headingLevel: Int,
    code: Boolean,
    deli: settings.SpecialKeySettings, viewport: Boolean): Seq[cursor.Node] = {
    val (n, cur) = if (viewport) (node(zoom), zoom) else (node, cursor.Node.root)
    n.filter(cur, a => {
      (!heading || a.isHeading) &&
        (headingLevel <= 0 || a.heading.contains(headingLevel)) &&
        (
          a.content match {
            case data.Content.Code(_, _) => code
            case data.Content.Rich(j) =>
              !code && j.quickSearch(tt, deli)
          }
        )
    }).sortBy(cur => {
      val n = node(cur)
      (!n.isH1, !n.isHeading)
    })
  }


  def zoomId: String = node(zoom).uuid

  def mode: Option[model.mode.Node] = if (badMode) None else Some(mode0)

  assert(node.get(zoom).isDefined, s"wrong zoom? $zoom")
  assert(mode0.inside(zoom), s"mode not inside zoom $mode0 $zoom")

  def folded(a: cursor.Node): Boolean = {
    val no = node(a)
    userFoldedNodes.getOrElse(no.uuid, no.isH1)
  }

  def viewAsFolded(a: cursor.Node): Boolean = {
    assert(cursor.Node.contains(zoom, a))
    val no = node(a)
    a != zoom && userFoldedNodes.getOrElse(no.uuid, no.isH1)
  }

  def viewAsFolded(a: cursor.Node, default: Boolean): Boolean = {
    assert(cursor.Node.contains(zoom, a))
    val no = node(a)
    a != zoom && userFoldedNodes.getOrElse(no.uuid, default)
  }

  def inViewport(a: cursor.Node): Boolean = cursor.Node.contains(zoom, a)


  def currentlyVisible(a: Node): Boolean = {
    inViewport(a) && !viewAsHidden(a)
  }
  def viewAsHidden(k: Node): Boolean = {
    var n = k
    while (n.size > zoom.size) {
      n = cursor.Node.parent(n)
      if (viewAsFolded(n)) {
        return true
      }
    }
    false
  }

  def mover(): cursor.Node.Mover = new cursor.Node.Mover(node, zoom, viewAsFolded)


  def rich(n: cursor.Node): Rich = node(n).content.asInstanceOf[model.data.Content.Rich].content

  def isRich(n: cursor.Node): Boolean = node(n).content.isRich

  def isInsert: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.RichInsert(_))) => true
    case Some(model.mode.Node.Content(_, model.mode.Content.CodeInside(_, _))) => true
    case _ => false
  }

  def isRichInsert: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.RichInsert(_))) => true
    case _ => false
  }

  def isRich: Boolean = mode match {
    case Some(model.mode.Node.Content(_, rich: model.mode.Content.Rich)) => true
    case _ => false
  }

  def isCode: Boolean = mode match {
    case Some(model.mode.Node.Content(_, rich: model.mode.Content.Code)) => true
    case _ => false
  }

  def isRichNormalOrVisual: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.RichVisual(_, _))) => true
    case Some(model.mode.Node.Content(_, model.mode.Content.RichNormal(a))) => true
    case _ => false
  }

  def isRichNormalOrInsert: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.RichInsert( _))) => true
    case Some(model.mode.Node.Content(_, model.mode.Content.RichNormal(a))) => true
    case _ => false
  }

  def isNonEmptyRichNormalOrVisual: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.RichVisual(_, _))) => true
    case Some(model.mode.Node.Content(_, model.mode.Content.RichNormal(a))) => a.nonEmpty
    case _ => false
  }


  def contentMode: model.mode.Content = mode match {
    case Some(c: model.mode.Node.Content)  => c.a
    case _ => throw new IllegalStateException("not supported")
  }

  def asContent: cursor.Node = mode match {
    case Some(model.mode.Node.Content(cur, _))  => cur
    case _ => throw new IllegalStateException("not supported")
  }

  def isContent: Boolean = mode match {
    case Some(model.mode.Node.Content(_, _))  => true
    case _ => false
  }

  def isNormal: Boolean = mode match {
    case Some(model.mode.Node.Content(_, a)) if a.isNormal => true
    case _ => false
  }

  def isCodeNormal: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.CodeNormal)) => true
    case _ => false
  }

  def isCodeInside: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.CodeInside(_, _))) => true
    case _ => false
  }

  def asRichNormalAtom: (cursor.Node, Rich, Atom) = {
    val (cur, rich, nv) = asRichNormal
    if (rich.isEmpty) throw new IllegalArgumentException("Wrong!")
    else {
      val t = rich.after(nv.focus.start)
      (cur, rich, t)
    }
  }

  def editCode(text: Atom): DocTransaction = {
    assert(text.text.isCodedAtomic)
    editCode(IntRange.len(text.textRange.start + 1, text.text.asDelimited.contentSize))
  }

  def editCode(range: IntRange,
    modeBefore: model.mode.Content.Rich = mode.get.asInstanceOf[model.mode.Node.Content].a.asInstanceOf[model.mode.Content.Rich]
  ): DocTransaction = {
    DocTransaction(
      copyContentMode(model.mode.Content.RichCodeSubMode(range,
        CodeInside(if (modeBefore.isInstanceOf[model.mode.Content.RichInsert]) "insert" else "normal", 0),
        modeBefore)))
  }

  def editAttribute(range: IntRange,
    modeBefore: model.mode.Content.Rich = mode.get.asInstanceOf[model.mode.Node.Content].a.asInstanceOf[model.mode.Content.Rich]
  ): DocTransaction = {
    DocTransaction(
      copyContentMode(model.mode.Content.RichAttributeSubMode(range,
        modeBefore)))
  }

  def editAttribute(text: Atom): DocTransaction = {
    editAttribute(IntRange.len(text.textRange.start + 1, text.text.asDelimited.contentSize))
  }

  def asRichAtom: (cursor.Node, Rich, Atom)  = {
    isRich((c: cursor.Node, r: Rich, a: Atom) => return (c, r, a))
    throw new IllegalArgumentException("Not possible")
  }

  def isRich(a: (cursor.Node, Rich, Atom) => Boolean): Boolean = {
    if (isRichInsert) {
      val (cur, rich, nv) = asRichInsert
      if (rich.isEmpty) false
      else {
        val t = rich.after(nv.pos)
        a(cur, rich, t)
      }
    } else if (isRichNormalOrVisual) {
      val (cur, rich, nv) = asRichNormalOrVisual
      if (rich.isEmpty) false
      else {
        val t = rich.after(nv.focus.start)
        a(cur, rich, t)
      }
    } else {
      false
    }
  }

  def isRichNormal(a: (Rich, Atom) => Boolean): Boolean = {
    if (isRichNormal) {
      val (_, rich, nv) = asRichNormal
      if (rich.isEmpty) false
      else {
        val t = rich.after(nv.focus.start)
        a(rich, t)
      }
    } else {
      false
    }
  }

  def isRichNormal: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.RichNormal(_))) => true
    case _ => false
  }

  def isRichVisual: Boolean = mode match {
    case Some(model.mode.Node.Content(_, model.mode.Content.RichVisual(_, _))) => true
    case _ => false
  }

  def isVisual: Boolean = mode match {
    case Some(model.mode.Node.Visual(_, _)) => true
    case Some(model.mode.Node.Content(_, model.mode.Content.RichVisual(_, _))) => true
    case _ => false
  }

  def isNodeVisual: Boolean = mode match {
    case Some(model.mode.Node.Visual(_, _)) => true
    case _ => false
  }

  def asNodeVisual: model.mode.Node.Visual = mode match {
    case Some(v@model.mode.Node.Visual(_, _)) => v
    case _ => throw new MatchError("Not possible")
  }

  def asCode: (cursor.Node, data.Content.Code) = {
    mode match {
      case Some(model.mode.Node.Content(n, c)) =>
        c match {
          case _: model.mode.Content.Code => (n, node(n).content.asInstanceOf[data.Content.Code])
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

  def asCodeInside: cursor.Node = {
    mode match {
      case Some(model.mode.Node.Content(n, c)) =>
        c match {
          case t@model.mode.Content.CodeInside(_, _) => n
          case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

  def asNormal: (cursor.Node, model.mode.Content.Normal) = {
    mode match {
      case Some(model.mode.Node.Content(n, c)) =>
        c match {
          case t@model.mode.Content.RichNormal(_) => (n, t)
          case t@model.mode.Content.CodeNormal => (n, t)
          case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

  def asRichVisual: (cursor.Node, Rich, model.mode.Content.RichVisual) = {
    mode match {
      case Some(o@model.mode.Node.Content(n, c)) =>
        val content = rich(n)
        c match {
          case v@model.mode.Content.RichVisual(fix, m) => (n, content, v)
          case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

  def asRichNormalOrInsert: (cursor.Node, Rich, Int, Int) = {
    mode match {
      case Some(o@model.mode.Node.Content(n, c)) =>
        val content = rich(n)
        c match {
          case nor@model.mode.Content.RichNormal(r) => (n, content, r.start, r.until)
          case v@model.mode.Content.RichInsert(m) => (n, content, m, -1)
          case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

  def asRichNormalOrVisual: (cursor.Node, Rich, model.mode.Content.RichNormalOrVisual) = {
    mode match {
      case Some(o@model.mode.Node.Content(n, c)) =>
        val content = rich(n)
        c match {
          case nor@model.mode.Content.RichNormal(r) => (n, content, nor)
          case v@model.mode.Content.RichVisual(fix, m) => (n, content, v)
          case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

  def asRichInsert: (cursor.Node, Rich, model.mode.Content.RichInsert) = {
    mode match {
      case Some(o@model.mode.Node.Content(n, c)) =>
        val content = rich(n)
        c match {
          case insert@model.mode.Content.RichInsert(r) => (n, content, insert)
          case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }


  def asRichNormal: (cursor.Node, Rich, model.mode.Content.RichNormal) = {
    mode match {
      case Some(o@model.mode.Node.Content(n, c)) =>
        val content = rich(n)
        c match {
          case nor@model.mode.Content.RichNormal(r) => (n, content, nor)
          case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
        }
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

  def copyContentMode(normal: model.mode.Content): model.mode.Node.Content = {
    mode match {
      case Some(o@model.mode.Node.Content(n, c)) =>
        o.copy(a = normal)
      case _ => throw new IllegalArgumentException("Should not call this method with not applicable state")
    }
  }

}
