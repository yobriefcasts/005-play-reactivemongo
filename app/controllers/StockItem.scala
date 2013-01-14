package controllers

import play.api.mvc.{Action, Controller}
import models.{StockItem, Stocklist}

object StockItems extends Controller {

  def index = Action {
    val data = Stocklist.getAll
    Ok(views.html.index(data))
  }

  def view(id: Int) = Action {
    Stocklist.getSingle(id).map { item =>
      Ok(views.html.view(item))
    } getOrElse {
      Redirect(routes.StockItems.index())
    }
  }

  def create = Action {
    Ok(views.html.create(StockItem.form))
  }

  def save = Action { implicit request =>
    StockItem.form.bindFromRequest.fold(
      errors => BadRequest(views.html.create(errors)),
      item => {
        Stocklist.insert(item)
        Redirect(routes.StockItems.index)
      }
    )
  }

  def edit(id: Int) = Action {
    Stocklist.getSingle(id).map { item =>
      val form =StockItem.form.fill(item)
      Ok(views.html.edit(id, form))
    } getOrElse {
      Redirect(routes.StockItems.index)
    }
  }

  def update(id: Int) = Action { implicit request =>
    StockItem.form.bindFromRequest.fold(
      errors => BadRequest(views.html.edit(id, errors)),
      item => {
        Stocklist.update(id, item)
        Redirect(routes.StockItems.index)
      }
    )
  }

  def delete(id: Int) = Action {
    Stocklist.delete(id)
    Redirect(routes.StockItems.index)
  }
}
