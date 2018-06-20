package model.data

import model._


case class Unicode(private val str: String) {

  override def toString: String = str

  def isEmpty: Boolean = str.isEmpty
  lazy val size: Int = str.codePointCount(0, str.size)
  def slice(r: range.Unicode): Unicode = {
    val start = str.offsetByCodePoints(0, r.start)
    val end = str.offsetByCodePoints(start, r.size)
    Unicode(str.substring(start, end))
  }
  def insert(at: Int, u: Unicode): Unicode = {
    if (at > size) throw new IllegalArgumentException()
    val index = str.offsetByCodePoints(0, at)
    Unicode(s"${str.substring(0, index)}${u.str}${str.substring(index)}")
  }
  def delete(r: range.Unicode): Unicode = {
    val start = str.offsetByCodePoints(0, r.start)
    val end = str.offsetByCodePoints(start, r.size)
    Unicode(s"${str.substring(0, start)}${str.substring(end)}")
  }

  def move(r: range.Unicode, at: Int): Unicode = {
    val s = slice(r)
    delete(r).insert(r.transformInsertionPointAfterDeleted(at), s)
  }
}
