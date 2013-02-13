package models

import play.api.data._
import play.api.data.Forms._
import reactivemongo.bson.{BSONLong, BSONString, BSONDocument, BSONObjectID}
import reactivemongo.bson.handlers.{BSONWriter, BSONReader}

case class StockItem(id: Option[BSONObjectID], name: String, quantity: Long)

object StockItem {

  val form = Form(
    mapping(
      "id" -> ignored[Option[BSONObjectID]](None),
      "name" -> nonEmptyText,
      "quantity" -> longNumber
    )(StockItem.apply)(StockItem.unapply)
  )

  implicit object StockItemWriter extends BSONWriter[StockItem]{
    def toBSON(document: StockItem): BSONDocument = {
      BSONDocument(
        "_id" -> document.id.getOrElse(BSONObjectID.generate),
        "name" -> BSONString(document.name),
        "quantity" -> BSONLong(document.quantity)
      )
    }
  }

  implicit object StockItemReader extends BSONReader[StockItem]{
    def fromBSON(doc: BSONDocument): StockItem = {
      val document = doc.toTraversable
      StockItem(
        document.getAs[BSONObjectID]("_id"),
        document.getAs[BSONString]("name").map(_.value).get,
        document.getAs[BSONLong]("quantity").map(_.value).get
      )
    }
  }

}


