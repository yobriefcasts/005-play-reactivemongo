package controllers

import play.api.mvc.{Action, Controller}
import models.Stocklist

object StockItem extends Controller {

  def index = Action {
    val data = Stocklist.getAll
    Ok(views.html.index(data))
  }

  def view(id: Int) = Action {
    Stocklist.getSingle(id).map { item =>
      Ok(views.html.view(item))
    } getOrElse {
      Redirect(routes.StockItem.index())
    }
  }

}
