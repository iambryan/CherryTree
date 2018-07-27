package command.defaults

import command.{CommandCategory, CommandInterface, Motion}
import command.Key.KeySeq
import doc.{DocState, DocTransaction}
import model.data.{Rich, Unicode}
import model.range

class RichTextObject extends CommandCategory("text object motion") {

  // TODO
  //v_as     N  as        Select "a sentence"
  //v_is     N  is        Select "inner sentence"
  //v_a'     N  a'        Select "a single quoted string"
  //v_i'     N  i'        Select "inner single quoted string"
  //v_aquote N  a"        Select "a double quoted string"
  //v_iquote N  i"        Select "inner double quoted string"
  //v_a`     N  a`        Select "a backward quoted string"
  //v_i`     N  i`        Select "inner backward quoted string"

  abstract class RichTextObjectCommand extends Command with command.TextObject {
    override def available(a: DocState, commandState: CommandInterface): Boolean = commandState.needsMotion && a.isNonEmptyRichNormalOrVisual

    override def action(a: DocState, count: Int, commandState: CommandInterface, key: Option[KeySeq], grapheme: Option[Unicode], motion: Option[Motion]): DocTransaction = {
      val (node, rich, visual) = a.asRichVisual
      move(rich, visual.move, None) match {
        case Some(r) => DocTransaction.mode(a.copyContentMode(model.mode.Content.RichVisual(visual.fix, ???)))
        case None => DocTransaction.empty
      }
    }
  }

//  new RichTextObjectCommand {
//    override val description: String = "a word, including tailing whitespace" // will select next word if current on whitespace
//    override def defaultKeys: Seq[KeySeq] = Seq("aw")
//    override def move(content: Rich, a: range.IntRange, char: Option[Unicode]): Option[range.IntRange] = {
//      content.extendToWordAndTailingWhitespace(a)
//    }
//  }
//
//  new RichTextObjectCommand    {
//    override val description: String = "a word" // will select next word if current on whitespace
//    override def defaultKeys: Seq[KeySeq] = Seq("iw")
//    override def move(content: Rich, a: range.IntRange, char: Option[Unicode]): Option[range.IntRange] = {
//      content.extendToWordOrWhitespace(a)
//    }
//  }
//
//  new RichTextObjectCommand {
//    override val description: String = "a WORD, including tailing whitespace" // will select next word if current on whitespace
//    override def defaultKeys: Seq[KeySeq] = Seq("aW")
//    override def move(content: Rich, a: range.IntRange, char: Option[Unicode]): Option[range.IntRange] = {
//      content.extendToWORDAndTailingWhitespace(a)
//    }
//  }
//
//  new RichTextObjectCommand {
//    override val description: String = "a WORD" // will select next word if current on whitespace
//    override def defaultKeys: Seq[KeySeq] = Seq("iW")
//    override def move(content: Rich, a: range.IntRange, char: Option[Unicode]): Option[range.IntRange] = {
//      content.extendToWORDOrWhitespace(a)
//    }
//  }
}
