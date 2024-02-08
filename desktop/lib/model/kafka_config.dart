class KafkaConfigModel {
  int id;
  String broker;
  String name;

  KafkaConfigModel(this.id, this.broker, this.name);

  KafkaConfigModel.fromJson(Map<String, dynamic> json)
      : id = json['id'],
        broker = json['broker'],
        name = json['name'];
}
