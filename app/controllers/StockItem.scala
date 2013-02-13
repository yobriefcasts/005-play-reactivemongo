package controllers

import play.api.mvc.{Action, Controller}
import models.{StockItem}
import play.modules.reactivemongo.{ReactiveMongoPlugin, MongoController}
import play.api.Play.current

import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler
import reactivemongo.bson.{BSONObjectID, BSONDocument}

object StockItems extends Controller with MongoController {

  lazy val inventory = ReactiveMongoPlugin.collection("inventory")

  def index = Action {
    import StockItem.StockItemReader

    Async {
      inventory.find(BSONDocument()).toList().map { stock =>
        Ok(views.html.index(stock))
      }
    }
  }

  def view(id: String) = Action {
    import StockItem.StockItemReader

    Async {
      inventory.find(BSONDocument(
        "_id" -> BSONObjectID(id)
      )).headOption.map { maybeItem =>
        maybeItem.map { item =>
          Ok(views.html.view(item))
        } getOrElse {
          Redirect(routes.StockItems.index())
        }
      }
    }
  }

  def create = Action {
    Ok(views.html.create(StockItem.form))
  }

  def save = Action { implicit request =>
    import StockItem.StockItemWriter

    StockItem.form.bindFromRequest.fold(
      errors => BadRequest(views.html.create(errors)),
      item => {
        inventory.insert(item)
        Redirect(routes.StockItems.index)
      }
    )
  }

  def edit(id: String) = Action {
    import StockItem.StockItemReader

    Async {
      inventory.find(BSONDocument(
        "_id" -> BSONObjectID(id)
      )).headOption().map { maybeItem =>
        maybeItem.map { item =>
          val form = StockItem.form.fill(item)
          Ok(views.html.edit(id,form))
        } getOrElse {
          Redirect(routes.StockItems.index)
        }
      }
    }
  }

  def update(id: String) = Action { implicit request =>
    import StockItem.StockItemWriter
    StockItem.form.bindFromRequest.fold(
      errors => BadRequest(views.html.edit(id, errors)),
      item => {
        inventory.update(
          BSONDocument("_id" -> BSONObjectID(id)),
          item.copy(id = Some(BSONObjectID(id))),
          upsert = false,
          multi = false
        )
        Redirect(routes.StockItems.index)
      }
    )
  }

  def delete(id: String) = Action {
    inventory.remove(BSONDocument(
      "_id" -> BSONObjectID(id)
    ), firstMatchOnly = true)
    Redirect(routes.StockItems.index)
  }
}
