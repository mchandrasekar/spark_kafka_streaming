package serializer

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.JsPath

case class SampleTraversal(requestTime: Int,nodesTouched: Int,edgesTouched: Int) {
  require(requestTime > 0)
}

case class UserInfo(serviceId: String,timeOfRequest: String) {}
object SampleTraversal {

  implicit val SubmitSampleCommandFormat = (
    (JsPath \ "requestTime").format[Int] and
      (JsPath \ "nodesTouched").format[Int] and
      (JsPath \ "edgesTouched").format[Int]
    ) (SampleTraversal.apply, unlift(SampleTraversal.unapply))

}