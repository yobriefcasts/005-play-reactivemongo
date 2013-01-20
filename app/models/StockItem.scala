package models

import collection.mutable.ArrayBuffer

import play.api.data._
import play.api.data.Forms._

case class StockItem(id: Int, name: String, quantity: Long)

object StockItem {

  val form = Form(
    mapping(
      "id" -> ignored(0),
      "name" -> nonEmptyText,
      "quantity" -> longNumber
    )(StockItem.apply)(StockItem.unapply)
  )

}

object Stocklist {

  private val stock = ArrayBuffer(
    StockItem(1, "Toothpaste", 10),
    StockItem(2, "Toilet Paper", 3),
    StockItem(3, "Shampoo", 2),
    StockItem(4, "Conditioner", 3)
  )

  def getAll = stock.toList

  def getSingle(id: Int) = stock.find(_.id == id)

  def insert(item: StockItem) = {
    val entry = item.copy(id = nextId)
    stock.append(entry)
    entry
  }

  def update(id: Int, item: StockItem) = {
    stock.indexWhere(_.id == id) match {
      case index if index != -1 => {
        val updated = item.copy(id = id)
        stock remove index
        stock.insert(index, updated)
        Some(updated)
      }
      case _ => None
    }
  }

  def delete(id: Int) = stock remove stock.indexWhere(_.id == id)

  private def nextId = stock.sortBy(_.id).last.id + 1
}
