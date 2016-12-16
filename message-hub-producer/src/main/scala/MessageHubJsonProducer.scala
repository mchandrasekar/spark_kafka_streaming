import java.io.File
import java.io.FileWriter
import java.io.InputStream
import java.util.concurrent.TimeUnit

import scala.collection.JavaConversions.mapAsJavaMap
import scala.io.Source

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.security.JaasUtils
import org.apache.kafka.common.serialization.StringSerializer

import serializer.SampleTraversal
import org.apache.kafka.common.serialization.ByteArraySerializer

object MessageHubProducerSimple {
  def main(args: Array[String]): Unit = {
    val valueSerializer = new JsonSerializer[SampleTraversal]
    val kafkaparams = MessageHubParams.defaultParams ++
      Map[String, Object](MessageHubParams.VALUE_SERIALIZER -> classOf[ByteArraySerializer].getName)
    MessageHubAuthUtil.authenticate

    val kafkaProducer = new KafkaProducer[String, SampleTraversal](kafkaparams, null, valueSerializer)
    var counter = 0
    while (true) {
      var producerTraversal = new SampleTraversal(200 + counter, 2 + counter, 2 + counter)
      var key: String = "TestKey" + counter
      var producerRecord = new ProducerRecord("traversaljson", key, producerTraversal)
      try {
        val metadata = kafkaProducer.send(producerRecord).get(2000, TimeUnit.SECONDS);
        println(metadata.topic + " -- " + metadata.offset)
      } catch {
        case e: Throwable => e.printStackTrace
      }
      counter += 1
      Thread.sleep(2000)
    }
  }
}