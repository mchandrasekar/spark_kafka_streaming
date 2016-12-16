import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.config.SaslConfigs

object MessageHubParams {
  val KEY_SERIALIZER = "key.serializer"
  val VALUE_SERIALIZER = "value.serializer"
  val KEY_DESERIALIZER = "key.deserializer"
  val VALUE_DESERIALIZER = "value.deserializer"
  
  var defaultParams = Map[String, Object](
    "bootstrap.servers" -> MessageHubAuthUtil.getKafkaBrokers,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean),
    "auto.offset.reset" -> "latest",
    "acks" -> "-1",
    CommonClientConfigs.SECURITY_PROTOCOL_CONFIG -> "SASL_SSL",
    SaslConfigs.SASL_MECHANISM -> "PLAIN")
}

