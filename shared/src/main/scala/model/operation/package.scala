package model


import model.mode.Mode

import scala.util.Random

/**
  * this is definitely not true:
  *
  *
  * test a.r.r == a
  */

package object operation {


  object Type extends Enumeration {
    type Type = Value
    val Structural, Add, Delete, AddDelete = Value

    def reverse(a: Type): Type = a match {
      case Add => Delete
      case Delete => Add
      case a => a
    }
  }
  import Type.Type

  trait Operation[DATA] {
    type This
    def mergeForUndoer(before: This): Option[This] = None
    def ty: Type
    def apply(data: DATA): DATA

    def reverse(d: DATA): This

    def merge(before: Any): Option[This]

    def isEmpty: Boolean
  }

  trait OperationOnModal[DATA, M <: Mode[DATA]] extends Operation[DATA] {

        type MM = M
        type MODE = (M, Boolean)
        def MODE(a: MM, b: Boolean = false): MODE = (a, b)

    def transform(a: M): Option[M] = {
      transformMaybeBad(a) match {
        case (b, false) => Some(b)
        case _ => None
      }
    }
        private[model] def transformMaybeBad(a: M): MODE
        private[model] def transformMaybeBad(a: MODE): MODE = {
          val res = transformMaybeBad(a._1)
          (res._1, res._2 || a._2)
        }
  }



  trait OperationObject[DATA, OPERATION <: Operation[DATA]] {

    type TRANSACTION = Seq[OPERATION]

    val pickler: Pickler[OPERATION]

    def random(data: DATA): OPERATION = random(data, new Random())

    def random(data: DATA, r: Random): OPERATION

    def randomTransaction(size: Int, data: DATA, r: Random): TRANSACTION = {
      var a = data
      var i = 0
      var cs = Seq.empty[OPERATION]
      while (i < size) {
        val c = random(a, r)
        a = c.apply(a)
        cs = cs :+ c
        i += 1
      }
      cs
    }

    def merge(a0: Seq[OPERATION]): Seq[OPERATION] = {
      def rec(a0: Seq[OPERATION]): Seq[OPERATION] = {
        val a = a0.filter(!_.isEmpty)
        val res = if (a.isEmpty) a else a.dropRight(1).foldRight(Seq(a.last)) { (before, seq) =>
          seq.head.merge(before) match {
            case Some(h) => h.asInstanceOf[OPERATION] +: seq.tail
            case None => before +: seq
          }
        }
        if (res.size != a.size) {
          rec(res)
        } else {
          res
        }
      }
      rec(a0)
    }

    def apply(c: Option[OPERATION], model: DATA): DATA = c match {
      case None => model
      case Some(a) => a.apply(model)
    }

    def apply(cs: TRANSACTION, model: DATA): DATA = {
      cs.foldLeft(model) { (model, c) => c.apply(model) }
    }

    def applyT(cs: Seq[TRANSACTION], model: DATA): DATA = {
      cs.foldLeft(model) { (model, c) => apply(c, model) }
    }
  }
}
