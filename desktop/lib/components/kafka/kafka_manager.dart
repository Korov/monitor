import 'package:desktop/components/menu_drawer.dart';
import 'package:desktop/utils/http_utils.dart';
import 'package:desktop/utils/log.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../../customize/tap_drop_down.dart';
import '../../model/kafka_config.dart';
import '../../model/kafka_topic_model.dart';
import '../../utils/constant.dart';

class KafkaManager extends StatefulWidget {
  const KafkaManager({required Key key}) : super(key: key);

  @override
  KafkaManagerState createState() => KafkaManagerState();
}

class KafkaManagerState extends State<KafkaManager> {
  int _selectedIndex = 0;

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  final List<Widget> _bottomNavPages = []; // 底部导航栏各个可切换页面组

  KafkaManagerState();

  List<DataRow> _rows = [];

  @override
  void initState() {
    super.initState();
    _bottomNavPages
      ..add(const TopicManager(text: 'Topic管理'))
      ..add(const ClusterManager(text: '集群管理'))
      ..add(const GroupManager(text: 'Group管理'));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        //导航栏
        title: const Text("Kafka Manager"),
        actions: <Widget>[
          //导航栏右侧菜单
          Cache.cachedRoute.isNotEmpty
              ? IconButton(
                  icon: const Icon(Icons.arrow_back),
                  onPressed: () {
                    Cache.cachedRoute.remove(ConstantKey.kafkaManager.toString());
                    Navigator.pop(context, "我是返回值");
                  })
              : Container(),
        ],
      ),
      drawer: const MenuDrawer(),
      body: _bottomNavPages[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Topic管理'),
          BottomNavigationBarItem(icon: Icon(Icons.business), label: '集群管理'),
          BottomNavigationBarItem(icon: Icon(Icons.school), label: 'Group管理'),
        ],
        currentIndex: _selectedIndex,
        selectedItemColor: Colors.teal,
        onTap: _onItemTapped,
      ),
    );
  }

  Future<void> _updateTable() async {
    Response response = await HttpUtils.get("${Global.uri}/kafka/query");
    List data = response.data['data'];
    setState(() {
      _rows = [];
      for (var config in data) {
        KafkaConfigModel kafkaConfig = KafkaConfigModel.fromJson(config);
        _rows.add(DataRow(cells: [
          DataCell(Text(kafkaConfig.name)),
          DataCell(Text(kafkaConfig.broker)),
          DataCell(IconButton(
            icon: const Icon(Icons.delete),
            tooltip: 'Delete',
            onPressed: () {
              _deleteConfig(kafkaConfig.id);
            },
          )),
        ]));
      }
    });
  }

  Future<void> _deleteConfig(int id) async {
    Response response =
        await HttpUtils.delete("${Global.uri}/kafka/delete?id=$id");
    dynamic data = response.data['data'];
    Log.i(data);
    _updateTable();
  }
}

class GroupManager extends StatefulWidget {
  const GroupManager({
    super.key,
    required this.text,
  });

  final String text;

  @override
  GroupManagerInner createState() => GroupManagerInner();
}

class GroupManagerInner extends State<GroupManager> {
  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Text("GroupManager"),
    );
  }
}

class ClusterManager extends StatefulWidget {
  const ClusterManager({
    super.key,
    required this.text,
  });

  final String text;

  @override
  ClusterManagerInner createState() => ClusterManagerInner();
}

class ClusterManagerInner extends State<ClusterManager> {
  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Text("ClusterManager"),
    );
  }
}

class TopicManager extends StatefulWidget {
  const TopicManager({
    super.key,
    required this.text,
  });

  final String text;

  @override
  TopicManagerInner createState() => TopicManagerInner();
}

class TopicManagerInner extends State<TopicManager> {
  String? brokerDropdownValue;
  List<DropdownMenuItem<String>> brokerDropdownList = [];

  String? topicDropdownValue;
  List<DropdownMenuItem<String>> topicDropdownList = [];

  List<DataRow> topicRows = [];

  @override
  void initState() {
    super.initState();
    _queryAllKafka();
  }

  Future<List<DropdownMenuItem<String>>> _queryAllKafka() async {
    Response response = await HttpUtils.get("${Global.uri}/kafka/query");
    List data = response.data['data'];
    setState(() {
      brokerDropdownList = [];
      for (var item in data) {
        DropdownMenuItem<String> dropdownMenuItem = DropdownMenuItem<String>(
          value: item['id'].toString(),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(item['name']),
              Text(
                item['broker'],
                style: const TextStyle(
                  color: Colors.black45,
                ),
              ),
            ],
          ),
        );
        brokerDropdownList.add(dropdownMenuItem);
      }
    });
    return brokerDropdownList;
  }

  Future<KafkaTopicDescriptionModel> _queryKafkaTopicDetail(
      String? brokerId, String topicName) async {
    String url =
        "${Global.uri}/kafka/topic/detail/query?sourceId=$brokerId&topic=$topicName";
    Response response = await HttpUtils.get(url);
    dynamic data = response.data['data'];
    String name = data['name'];
    bool isInternal = data['internal'];
    List<KafkaTopicPartitionModel> partitions = [];
    for (var partitionItem in data['partitions']) {
      int beginningOffset = partitionItem['beginningOffset'];
      int endOffset = partitionItem['endOffset'];
      int partition = partitionItem['partition'];

      var leaderItem = partitionItem['leader'];
      Node leader = Node(leaderItem['id'], leaderItem['host'],
          leaderItem['port'], leaderItem['rack']);

      List<Node> replicas = [];
      for (var replicaItem in partitionItem['replicas']) {
        replicas.add(Node(replicaItem['id'], replicaItem['host'],
            replicaItem['port'], replicaItem['rack']));
      }

      List<Node> isr = [];
      for (var isrItem in partitionItem['isr']) {
        isr.add(Node(
            isrItem['id'], isrItem['host'], isrItem['port'], isrItem['rack']));
      }

      KafkaTopicPartitionModel partitionModel = KafkaTopicPartitionModel(
          partition, leader, replicas, isr, beginningOffset, endOffset);
      partitions.add(partitionModel);
    }
    KafkaTopicDescriptionModel topicDescription =
        KafkaTopicDescriptionModel(name, isInternal, partitions);
    return topicDescription;
  }

  Future<List<String>> _queryKafkaConsumers(
      String? brokerId, String topicName) async {
    String url =
        "${Global.uri}/kafka/consumer/query?sourceId=$brokerId&topic=$topicName";
    Response response = await HttpUtils.get(url);
    dynamic data = response.data['data'];
    List<String> consumers = [];
    for (var item in data) {
      consumers.add(item);
    }
    return consumers;
  }

  Future<void> _deleteKafkaTopic(String? brokerId, String topicName) async {
    String url =
        "${Global.uri}/kafka/topic/delete?sourceId=$brokerId&topic=$topicName";
    Response response = await HttpUtils.delete(url);
    dynamic data = response.data['data'];
    Log.i("delete broker:$brokerId, topic:$topicName, data:$data");
  }

  Future<List<DropdownMenuItem<String>>> _queryKafkaTopic(
      String? sourceId, String? keyword) async {
    String url = "${Global.uri}/kafka/topic/query?sourceId=$sourceId";
    if (keyword != null) {
      url = "$url&keyword=$keyword";
    }
    Response response = await HttpUtils.get(url);
    List data = response.data['data'];
    setState(() {
      topicDropdownList.clear();
      topicRows.clear();
      for (var item in data) {
        String name = item['name'];
        bool isInternal = item['internal'];
        DropdownMenuItem<String> dropdownMenuItem =
            DropdownMenuItem<String>(value: name, child: Text(name));
        topicDropdownList.add(dropdownMenuItem);
        topicRows.add(DataRow(cells: [
          DataCell(Text(name)),
          DataCell(Text(isInternal.toString())),
          DataCell(Row(
            children: [
              IconButton(
                icon: const Icon(Icons.description),
                tooltip: 'TopicDetail',
                onPressed: () async {
                  KafkaTopicDescriptionModel topicDetail =
                      await _queryKafkaTopicDetail(brokerDropdownValue!, name);
                  showTopicDetail(context, topicDetail);
                },
              ),
              !isInternal
                  ? IconButton(
                      icon: const Icon(Icons.delete),
                      tooltip: 'Delete',
                      onPressed: () async {
                        await _deleteKafkaTopic(brokerDropdownValue!, name);
                        _queryKafkaTopic(brokerDropdownValue!, null);
                      },
                    )
                  : Container(),
              IconButton(
                icon: const Icon(Icons.people),
                tooltip: 'Consumer',
                onPressed: () async {
                  dynamic result =
                      await _queryKafkaConsumers(brokerDropdownValue!, name);
                  Log.i(result);
                },
              )
            ],
          )),
        ]));
      }
    });
    return topicDropdownList;
  }

  Future<void> _addTopic(String brokerDropdownValue, String topic,
      int partition, int replica) async {
    Map<String, String> body = {};
    body['sourceId'] = brokerDropdownValue;
    body['topic'] = topic;
    body['partition'] = partition.toString();
    body['replica'] = replica.toString();
    Response response =
        await HttpUtils.post("${Global.uri}/kafka/topic/create", data: body);
    response.data['data'];
    Navigator.of(context).pop();
    _queryKafkaTopic(brokerDropdownValue, null);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          SizedBox(
            height: 45.0,
            width: double.infinity,
            child: Row(
              children: [
                TapDropdownButton(
                    hint: const Text("请选择Kafka环境"),
                    value: brokerDropdownValue,
                    items: brokerDropdownList,
                    onChanged: (value) {
                      setState(() {
                        brokerDropdownValue = value!;
                        _queryKafkaTopic(brokerDropdownValue, null);
                      });
                    },
                    onTap: () async {
                      List<DropdownMenuItem<String>> listValue =
                          await _queryAllKafka();
                      return listValue;
                    }),
                TapDropdownButton(
                    hint: const Text("请输入topic"),
                    value: topicDropdownValue,
                    items: topicDropdownList,
                    onChanged: (value) {
                      setState(() {
                        topicDropdownValue = value!;
                      });
                    },
                    onTap: () async {
                      List<DropdownMenuItem<String>> listValue =
                          await _queryKafkaTopic(brokerDropdownValue, null);
                      return listValue;
                    }),
                TextButton(
                  onPressed: () {
                    showDialog(
                      context: context,
                      builder: (BuildContext context) {
                        TextEditingController nameController =
                            TextEditingController();
                        int partitionCounter = 1;
                        int replicaCounter = 1;
                        return StatefulBuilder(builder:
                            (BuildContext context, StateSetter setState) {
                          return SimpleDialog(
                            title: const Text('创建Topic'),
                            children: <Widget>[
                              Form(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisSize: MainAxisSize.max,
                                  children: <Widget>[
                                    Row(
                                      children: [
                                        const Flexible(
                                            flex: 1, child: Text("Topic名字：")),
                                        Flexible(
                                          flex: 2,
                                          child: TextFormField(
                                            controller: nameController,
                                            validator: (value) {
                                              if (value == null ||
                                                  value.isEmpty) {
                                                return '请输入名字';
                                              }
                                              return null;
                                            },
                                            decoration: const InputDecoration(
                                              hintText: '请输入名字',
                                            ),
                                          ),
                                        )
                                      ],
                                    ),
                                    Row(
                                      children: [
                                        const Flexible(
                                            flex: 1, child: Text("分区数量：")),
                                        Flexible(
                                          flex: 1,
                                          child: FloatingActionButton(
                                            onPressed: () {
                                              setState(() {
                                                partitionCounter++;
                                              });
                                            },
                                            child: const Icon(Icons.add),
                                          ),
                                        ),
                                        Flexible(
                                            flex: 1,
                                            child: Text('$partitionCounter')),
                                        Flexible(
                                          flex: 1,
                                          child: FloatingActionButton(
                                            onPressed: () {
                                              setState(() {
                                                if (partitionCounter > 0) {
                                                  partitionCounter--;
                                                }
                                              });
                                            },
                                            child: const Icon(Icons.remove),
                                          ),
                                        ),
                                      ],
                                    ),
                                    Row(
                                      children: [
                                        const Flexible(
                                            flex: 1, child: Text("副本数量：")),
                                        Flexible(
                                          flex: 1,
                                          child: FloatingActionButton(
                                            onPressed: () {
                                              setState(() {
                                                replicaCounter++;
                                              });
                                            },
                                            child: const Icon(Icons.add),
                                          ),
                                        ),
                                        Flexible(
                                            flex: 1,
                                            child: Text('$replicaCounter')),
                                        Flexible(
                                          flex: 1,
                                          child: FloatingActionButton(
                                            onPressed: () {
                                              setState(() {
                                                if (replicaCounter > 0) {
                                                  replicaCounter--;
                                                }
                                              });
                                            },
                                            child: const Icon(Icons.remove),
                                          ),
                                        ),
                                      ],
                                    )
                                  ],
                                ),
                              ),
                              Row(
                                children: [
                                  TextButton(
                                    child: const Text('确定'),
                                    onPressed: () {
                                      _addTopic(
                                          brokerDropdownValue!,
                                          nameController.text,
                                          partitionCounter,
                                          replicaCounter);
                                    },
                                  ),
                                  TextButton(
                                    child: const Text('取消'),
                                    onPressed: () {
                                      Navigator.of(context).pop();
                                    },
                                  ),
                                ],
                              ),
                            ],
                          );
                        });
                      },
                    );
                  },
                  child: const Text("创建Topic"),
                ),
              ],
            ),
          ),
          Expanded(
              child: SingleChildScrollView(
                  child: Container(
            width: double.infinity,
            child: DataTable(
              columns: [
                const DataColumn(
                  label: Text('Topic Name'),
                ),
                DataColumn(
                    label: const Text('Topic Type'),
                    numeric: true,
                    onSort: (int columnIndex, bool ascending) {}),
                const DataColumn(label: Text('Operation'))
              ],
              rows: topicRows,
            ),
          ))),
        ],
      ),
    );
  }

  void showTopicDetail(
      BuildContext context, KafkaTopicDescriptionModel topicDetail) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        List<DataRow> partitionRow = [];
        for (KafkaTopicPartitionModel partition in topicDetail.partitions) {
          partitionRow.add(DataRow(cells: [
            DataCell(Text(partition.partition.toString())),
            DataCell(Text("broker:${partition.leader.id}")),
            DataCell(Text(partition.replicas
                .map((event) => "broker:${event.id}")
                .reduce((previous, element) => "$previous,$element"))),
            DataCell(Text(partition.isr
                .map((event) => "broker:${event.id}")
                .reduce((previous, element) => "$previous,$element"))),
            DataCell(Text(partition.beginningOffset.toString())),
            DataCell(Text(partition.endOffset.toString())),
            DataCell(Text(
                (partition.endOffset - partition.beginningOffset).toString())),
          ]));
        }
        return AlertDialog(
          title: Text('${topicDetail.name}分区详情'),
          content: Container(
            width: double.maxFinite,
            child: SingleChildScrollView(
              scrollDirection: Axis.vertical,
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: DataTable(
                  columns: const [
                    DataColumn(label: Text('分区号'), numeric: true),
                    DataColumn(label: Text('leader分区')),
                    DataColumn(label: Text('所有副本')),
                    DataColumn(label: Text('isr副本')),
                    DataColumn(label: Text('最小偏移量')),
                    DataColumn(label: Text('最大偏移量')),
                    DataColumn(label: Text('消息数量')),
                  ],
                  rows: partitionRow,
                ),
              ),
            ),
          ),
          actions: <Widget>[
            TextButton(
              child: const Text('确认'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }
}
