* Setup [spark 2.0](https://spark.apache.org/docs/2.0.0-preview/spark-standalone.html#running-alongside-hadoop)
* Add spark-submit executable to PATH variable, spark-submit will be found under <spark_installation_dir>/bin

#### Install vcap.json
* Put bluemix kafka service vcap.json unser message-hub-receiver/src/main/resources/vcap.json

### Run producer
* cd message-hub-producer
* sbt assemble
* spark-submit --master local[3] --class MessageHubJsonProducer target/scala-2.11/message-hub-producer-assembly-1.6.jar

### Run consumer
* cd message-hub-receiver
* sbt assemble
* spark-submit --master local[3] --class MessageHubJsonReceiver target/scala-2.11/message-hub-receiver-assembly-1.6.jar
