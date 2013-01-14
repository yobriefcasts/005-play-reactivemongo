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
    Ok(views.html.create())
  }

  def save = Action { implicit request =>
    StockItem.form.bindFromRequest.fold(
      errors => TODO(request),
      item => {
        Stocklist.insert(item)
        Redirect(routes.StockItems.index)
      }
    )
  }
}
