class KafkaTopicModel {
  String name;
  bool isInternal;

  KafkaTopicModel(this.name, this.isInternal);

  KafkaTopicModel.fromJson(Map<String, dynamic> json)
      : name = json['name'],
        isInternal = json['isInternal'];
}
