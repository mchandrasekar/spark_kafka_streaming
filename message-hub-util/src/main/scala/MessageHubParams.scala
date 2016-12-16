import scala.collection.mutable.Map
import scala.reflect.ClassTag

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.config.SaslConfigs

abstract class KafkaDefaultParams {
  var defaultReceiverParams = Map[String, Object](
    "bootstrap.servers" -> MessageHubAuthUtil.getKafkaBrokers,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean),
    "auto.offset.reset" -> "latest",
    "acks" -> "-1",
    CommonClientConfigs.SECURITY_PROTOCOL_CONFIG -> "SASL_SSL",
    SaslConfigs.SASL_MECHANISM -> "PLAIN")
}

class KafkaReceiverParams extends KafkaDefaultParams {
   def setKeyDeserializer[U]()(implicit c: ClassTag[U]){
    defaultReceiverParams.put("key.deserializer", c.runtimeClass.getName);
   }
  
   def setValueDeserializer[U]()(implicit c: ClassTag[U]){
    defaultReceiverParams.put("value.deserializer", c.runtimeClass.getName);
   }
   
   def setBootstrapServers(brokers: String) {
     defaultReceiverParams.put("bootstrap.servers",brokers)
   }
   
   implicit def toImmutableMap(): Map[String,Object]= {
    Map( defaultReceiverParams.toList: _* )
  }
}

class KafkaProducerParams extends KafkaDefaultParams {
   def setKeySerializer[U]()(implicit c: ClassTag[U]){
    defaultReceiverParams.put("key.serializer", c.runtimeClass.getName);
   }
  
   def setValueSerializer[U]()(implicit c: ClassTag[U]){
    defaultReceiverParams.put("value.serializer", c.runtimeClass.getName);
   }
   
   def setBootstrapServers(brokers: String) {
     defaultReceiverParams.put("bootstrap.servers",brokers)
   }
   
   implicit def toImmutableMap(): Map[String,Object]= {
    Map( defaultReceiverParams.toList: _* )
  }
}
