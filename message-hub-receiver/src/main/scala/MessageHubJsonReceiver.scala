import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import play.api.libs.json.Json

object MessageHubJsonReceiver {
  var ssc: StreamingContext = null
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    val sc = new SparkConf().setAppName("Test Message Hub Kafka receiver").setMaster("local[3]")
    sc.set("spark.streaming.kafka.consumer.poll.ms", "2000")
    val sparkContext = new SparkContext(sc)
    val sqlContext: SQLContext = new SQLContext(sparkContext)
    ssc = new StreamingContext(sparkContext, Seconds(1))

    val kafkaparams = MessageHubParams.defaultParams ++ Map[String,Object](
      MessageHubParams.KEY_DESERIALIZER -> classOf[StringDeserializer].getName,
      MessageHubParams.VALUE_DESERIALIZER -> classOf[ByteArrayDeserializer].getName
    )
        
    MessageHubAuthUtil.authenticate
    val topics = Array("traversaljson")
    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaparams))

    var pairs = stream.map(record => (record.key, record.value))
    pairs.foreachRDD(rdd => rdd.collect().foreach(row => processTuple(row._1, row._2, sqlContext, sparkContext)))
    ssc.start()
    ssc.awaitTermination()
  }

  def processTuple(key: String, value: Array[Byte], sqlContext: SQLContext, sc: SparkContext) = {
    val jsonString = Json.parse(value).toString
    val jsonRdd = sc.parallelize(Seq(jsonString))
    val df: DataFrame = sqlContext.read.json(jsonRdd)
    df.show()
  }
}
