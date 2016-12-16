import java.io.File
import java.io.FileWriter
import java.io.InputStream

import scala.io.Source

import org.apache.kafka.common.security.JaasUtils

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__

object MessageHubAuthUtil {
  case class MessageHubConfig(user: String, password: String, kafka_brokers_sasl: Array[String])
  var messageHubConfig: MessageHubConfig = null

  implicit val documentReader: Reads[MessageHubConfig] = (
    (__ \ "user").read[String] and
    (__ \ "password").read[String] and
    (__ \ "kafka_brokers_sasl").read[Array[String]])(MessageHubConfig.apply _)

  def readMessageHubVcap = {
    var is: InputStream = getClass.getClassLoader.getResourceAsStream("vcap_messagehub.json")
    val confString: String = Source.fromInputStream(is).mkString
    messageHubConfig = Json.parse(confString).as[MessageHubConfig](documentReader)
  }

  def getKafkaBrokers: String = {
    if (null == messageHubConfig) {
      readMessageHubVcap
    }
    messageHubConfig.kafka_brokers_sasl.mkString(",")
  }

  def authenticate() = {
    createJaasConfiguration
  }

  def main(args: Array[String]): Unit = {
    authenticate
    println(getKafkaBrokers)
  }

  def createJaasConfiguration() {
    //Create the jaas configuration
    if (null == messageHubConfig) {
      readMessageHubVcap
    }
    var is: InputStream = null
    try {
      is = getClass.getClassLoader.getResourceAsStream("jaas.conf")
      println("Path + " + getClass.getClassLoader.getResourceAsStream("jaas.conf"))
      val confString = Source.fromInputStream(is).mkString
        .replace("$USERNAME", messageHubConfig.user)
        .replace("$PASSWORD", messageHubConfig.password)

      val confDir = new File(System.getProperty("java.io.tmpdir") + File.separator +
        fixPath(messageHubConfig.user))
      confDir.mkdirs
      val confFile = new File(confDir, "jaas.conf");
      val fw = new FileWriter(confFile);
      fw.write(confString)
      fw.close

      //Set the jaas login config property
      println("Registering JaasConfiguration: " + confFile.getAbsolutePath)
      System.setProperty(JaasUtils.JAVA_LOGIN_CONFIG_PARAM, confFile.getAbsolutePath)
    } catch {
      case e: Throwable => {
        e.printStackTrace
        throw e
      }
    } finally {
      if (is != null) is.close
    }
  }

  private def fixPath(path: String): String = {
    path.replaceAll("\\ / : * ? \" < > |,", "_")
  }
}
