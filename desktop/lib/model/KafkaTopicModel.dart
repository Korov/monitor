class KafkaTopicModel {
  String name;
  bool isInternal;

  KafkaTopicModel(this.name, this.isInternal);

  KafkaTopicModel.fromJson(Map<String, dynamic> json)
      : name = json['name'],
        isInternal = json['isInternal'];
}

class KafkaTopicDescriptionModel {
  String name;
  bool isInternal;
  List<KafkaTopicPartitionModel> partitions;

  KafkaTopicDescriptionModel(this.name, this.isInternal, this.partitions);
}

class KafkaTopicPartitionModel {
  int partition;
  Node leader;
  List<Node> replicas;
  List<Node> isr;
  int beginningOffset;
  int endOffset;

  KafkaTopicPartitionModel(this.partition, this.leader, this.replicas, this.isr,
      this.beginningOffset, this.endOffset);
}

class Node {
  int id;
  String host;
  int port;
  String? rack;

  Node(this.id, this.host, this.port, this.rack);
}
