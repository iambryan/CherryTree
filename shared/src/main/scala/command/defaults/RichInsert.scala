package command.defaults

import client.Client
import client.Client.ViewMessage
import command.{CommandCategory, CommandInterface, Key, Motion}
import command.Key._
import doc.{DocState, DocTransaction}
import model.{mode, operation, range}
import model.data.{Rich, SpecialChar, Text, Unicode}
import model.range.IntRange

class RichInsert extends CommandCategory("when in insert mode") {


  trait RichInsertCommand extends Command {
    override def available(a: DocState): Boolean = a.isRichInsert
  }

  trait EditCommand extends RichInsertCommand  {
    def edit(content: Rich,a: Int): Seq[model.operation.Rich]
    override def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      val (n, content, insert) = a.asRichInsert
      val res = model.operation.Node.rich(n, edit(content, insert.pos))
      DocTransaction(res, None)
    }
  }

  new RichInsertCommand with OverrideCommand {
    override val description: String = "delete text before cursor"
    override val hardcodeKeys: Seq[KeySeq] = (Backspace: KeySeq) +: (if (model.isMac) Seq(Ctrl + "h") else Seq.empty[KeySeq])

    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      val (cursor, content, insert) = a.asRichInsert
      if (insert.pos > 0) {
        DocTransaction(
          Seq(operation.Node.rich(cursor, operation.Rich.deleteOrUnwrapAt(content, content.rangeBefore(insert.pos).start))),
          None) // we don't explicitly set mode, as insert mode transformation is always correct
      } else {
        joinWithPrevious(a)
      }
    }
  }

  // TODO join with previous
  def joinWithPrevious(a: DocState): DocTransaction = {
    DocTransaction.empty
  }

  new RichInsertCommand with OverrideCommand {
    override val description: String = "delete word before cursor"
    override val hardcodeKeys: Seq[KeySeq] = Seq(Alt + Backspace: KeySeq, Ctrl + "w")

    override def available(a: DocState): Boolean = a.isRichInsert


    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      val (cursor, content, insert) = a.asRichInsert
      if (insert.pos > 0) {
        val r = content.moveLeftWord(insert.pos).map(_.start).getOrElse(0)
        deleteRichNormalRange(a, commandState,cursor, IntRange(r, insert.pos), insert =true, noHistory = true)
      } else {
        joinWithPrevious(a)
      }
    }
  }

  new EditCommand with OverrideCommand {
    override val description: String = "delete text after cursor"
    override val hardcodeKeys: Seq[KeySeq] = (Shift + Backspace : KeySeq) +: (Delete: KeySeq) +: (if (model.isMac) Seq(Ctrl + "d") else Seq.empty[KeySeq])
    override def edit(content: Rich, a: Int): Seq[operation.Rich] = {
      if (a < content.size) {
        Seq(operation.Rich.deleteOrUnwrapAt(content, a))
      } else {
        Seq.empty
      }
    }
  }


  new RichInsertCommand with OverrideCommand {
    override val description: String = "open a new sibling next to current one and continue in insert mode (currently only works when you are in end of text)"
    // TODO what to do on enter?
    override val hardcodeKeys: Seq[KeySeq] = Seq(Enter)
    override def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = {
      val (node, rich, insert) =  a.asRichInsert
      if (insert.pos == rich.size) {
        val mover = a.mover()
        val n = mover.firstChild(node).getOrElse(mover.nextOver(node))
        DocTransaction(
          Seq(operation.Node.Insert(n, Seq(model.data.Node.create())))
          , Some(model.mode.Node.Content(n, model.mode.Content.RichInsert(0))))
      } else {
        DocTransaction.empty
      }
    }
  }

  SpecialChar.all.map(deli => deli -> new DeliCommand(deli) {


    override val description: String = if (deli.atomic) s"insert a new ${deli.name}" else  s"insert a new/or move cursor out of ${deli.name}"

    override def emptyAsFalseInInsertMode: Boolean = true

    override def available(a: DocState): Boolean =
      a.isRichNormalOrInsert && {
      val (node, rich, insert, until) = a.asRichNormalOrInsert
      if (deli.coded) {
        if (rich.insideCoded(insert)) {
          if (rich.insideCoded(insert, deli)) {
            !rich.wrappedByCodedContent(insert)
          } else {
            false
          }
        } else {
          true
        }
      } else {
        !rich.insideCoded(insert)
      }
    }

    override def action(a: DocState, count: Int, commandState: CommandInterface, key: Option[KeySeq], grapheme: Option[Unicode], motion: Option[Motion]): DocTransaction = {
      val (n, content, insert, until) = a.asRichNormalOrInsert
      val keyU = Unicode(key.map(a => Key.toString(a)).getOrElse(""))
      def moveSomeInsertMode(some: Int) = Some(a.copyContentMode(mode.Content.RichInsert(insert + some)))
      if (insert < content.size && content.after(insert).special(deli.end) && key.nonEmpty && delimitationGraphemes.get(deli.end).contains(keyU)) {
        DocTransaction(Seq.empty, moveSomeInsertMode(1))
      } else if (!content.insideCoded(insert) && (key.isEmpty || delimitationGraphemes.get(deli.start).contains(keyU))) {
        val wrap = deli.wrap()
        val k = operation.Rich.insert(insert, wrap)
        val trans = Seq(model.operation.Node.Content(n, model.operation.Content.Rich(k)))
        if (deli.atomic) {
          DocTransaction(trans, moveSomeInsertMode(wrap.size),
            viewMessagesAfter = Seq(ViewMessage.ShowUrlAndTitleAttributeEditor(n, IntRange(insert, insert + wrap.size), Text.Image(Unicode.empty))))
        } else {
          DocTransaction(trans, moveSomeInsertMode(1))
        }
      } else {
        DocTransaction.empty
      }
    }

    override protected def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = throw new NotImplementedError("Should not call this")
  }).toMap

  abstract class InsertMovementCommand extends Command {
    override def available(a: DocState): Boolean = a.isRichInsert
    def move(rich: Rich, i: Int): Int
    override def action(a: DocState, commandState: CommandInterface, count: Int): DocTransaction = a.asRichInsert match {
      case (node, rich, insert) =>
        val m = move(rich, insert.pos)
        if (m != insert.pos) DocTransaction.mode(a.copyContentMode(mode.Content.RichInsert(m))) else DocTransaction.empty
    }
  }

  // LATER  insert movement
  // i_<S-Left>    shift-left/right  one word left/right
  //i_<S-Up>      shift-up/down     one screenful backward/forward
  //i_<End>       <End>             cursor after last character in the line
  //i_<Home>      <Home>            cursor to first character in the line
  new InsertMovementCommand { // DIFFERENCE we added two move, also disabled up/down
    override val description: String = "move cursor right"
    override def defaultKeys: Seq[KeySeq] = Seq(Right)
    override def move(rich: Rich, i: Int): Int = rich.rangeAfter(i).until
  }

  new InsertMovementCommand {
    override val description: String = "move cursor left"
    override def defaultKeys: Seq[KeySeq] = Seq(Left)
    override def move(rich: Rich, i: Int): Int = rich.rangeBefore(i).start
  }

  // disabled keys!!!!
  new InsertMovementCommand with OverrideCommand {
    override val description: String = ""
    override def hardcodeKeys: Seq[KeySeq] = Seq[KeySeq](Down: KeySeq, Up)
    override def move(rich: Rich, i: Int): Int = i
  }

  // LATER insert movements
  // moving around:
  //i_<Up>        cursor keys       move cursor left/right/up/down
  //i_<S-Left>    shift-left/right  one word left/right
  //i_<S-Up>      shift-up/down     one screenful backward/forward
  //i_<End>       <End>             cursor after last character in the line
  //i_<Home>      <Home>            cursor to first character in the line

  // LATER seems all very wired
  // Q_ss          Special keys in Insert mode
  //
  //i_CTRL-V      CTRL-V {char}..   insert character literally, or enter decimal
  //                                     byte value
  //i_<NL>        <NL> or <CR> or CTRL-M or CTRL-J
  //                                  begin new line
  //i_CTRL-E      CTRL-E            insert the character from below the cursor
  //i_CTRL-Y      CTRL-Y            insert the character from above the cursor
  //
  //i_CTRL-A      CTRL-A            insert previously inserted text
  //i_CTRL-@      CTRL-@            insert previously inserted text and stop
  //                                     Insert mode
  //i_CTRL-R      CTRL-R {0-9a-z%#:.-="}  insert the contents of a register
  //
  //i_CTRL-N      CTRL-N            insert next match of identifier before the
  //                                     cursor
  //i_CTRL-P      CTRL-P            insert previous match of identifier before
  //                                     the cursor
  //i_CTRL-X      CTRL-X ...        complete the word before the cursor in
  //                                     various ways
  //
  //i_CTRL-U      CTRL-U            delete all entered characters in the current
  //                                     line
  //i_CTRL-T      CTRL-T            insert one shiftwidth of indent in front of
  //                                       the current line
  //i_CTRL-D      CTRL-D            delete one shiftwidth of indent in front of
  //                                     the current line
  //i_0_CTRL-D    0 CTRL-D          delete all indent in the current line
  //i_^_CTRL-D    ^ CTRL-D          delete all indent in the current line,
  //                                     restore indent in next line

  // Q_si          Special inserts
  //
  //:r       :r [file]       insert the contents of [file] below the cursor
  //:r!      :r! {command}   insert the standard output of {command} below the
  //                              cursor
}
