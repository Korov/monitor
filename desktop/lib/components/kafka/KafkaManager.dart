import 'package:desktop/components/MenuDrawer.dart';
import 'package:desktop/utils/HttpUtils.dart';
import 'package:desktop/utils/Log.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../../customize/TapDropDown.dart';
import '../../model/KafkaConfig.dart';
import '../../model/KafkaTopicModel.dart';
import '../../utils/Constant.dart';

class KafkaManager extends StatefulWidget {
  KafkaManager({required Key key}) : super(key: key);

  @override
  _KafkaManagerState createState() => _KafkaManagerState();
}

class _KafkaManagerState extends State<KafkaManager> {
  int _selectedIndex = 0;

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  List<Widget> _bottomNavPages = []; // 底部导航栏各个可切换页面组

  _KafkaManagerState();

  List<DataRow> _rows = [];

  @override
  void initState() {
    super.initState();
    _bottomNavPages
      ..add(TopicManager(text: 'Topic管理'))
      ..add(ClusterManager(text: '集群管理'))
      ..add(GroupManager(text: 'Group管理'));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        //导航栏
        title: const Text("Kafka Manager"),
        actions: <Widget>[
          //导航栏右侧菜单
          IconButton(
              icon: Icon(Icons.arrow_back),
              onPressed: () {
                Cache.cachedRoute.remove("kafkaManager");
                Log.i(Cache.cachedRoute);
                Navigator.pop(context, "我是返回值");
              }),
        ],
      ),
      drawer: MenuDrawer(),
      body: _bottomNavPages[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        items: <BottomNavigationBarItem>[
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
    Response response =
        await HttpUtils.get("http://localhost:8091/kafka/query");
    List data = response.data['data'];
    setState(() {
      _rows = [];
      for (var config in data) {
        KafkaConfigModel kafkaConfig = KafkaConfigModel.fromJson(config);
        _rows.add(DataRow(cells: [
          DataCell(Text(kafkaConfig.name)),
          DataCell(Text(kafkaConfig.broker)),
          DataCell(IconButton(
            icon: Icon(Icons.delete),
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
        await HttpUtils.delete("http://localhost:8091/kafka/delete?id=$id");
    dynamic data = response.data['data'];
    Log.i(data);
    _updateTable();
  }

  Future<void> _addConfig(String name, String address) async {
    Map<String, String> body = {};
    body['name'] = name;
    body['broker'] = address;
    Response response =
        await HttpUtils.post("http://localhost:8091/kafka/add", data: body);
    dynamic data = response.data['data'];
    Log.i(data);
    _updateTable();
  }
}

class GroupManager extends StatefulWidget {
  GroupManager({
    required this.text,
  });

  final String text;

  @override
  _GroupManager createState() => _GroupManager();
}

class _GroupManager extends State<GroupManager> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Text("GroupManager"),
    );
  }
}

class ClusterManager extends StatefulWidget {
  ClusterManager({
    required this.text,
  });

  final String text;

  @override
  _ClusterManager createState() => _ClusterManager();
}

class _ClusterManager extends State<ClusterManager> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Text("ClusterManager"),
    );
  }
}

class TopicManager extends StatefulWidget {
  TopicManager({
    required this.text,
  });

  final String text;

  @override
  _TopicManager createState() => _TopicManager();
}

class _TopicManager extends State<TopicManager> {
  String? brokerDropdownValue = null;
  List<DropdownMenuItem<String>> brokerDropdownList = [];

  String? topicDropdownValue = null;
  List<DropdownMenuItem<String>> topicDropdownList = [];

  List<DataRow> topicRows = [];

  @override
  void initState() {
    super.initState();
    _queryAllKafka();
  }

  Future<List<DropdownMenuItem<String>>> _queryAllKafka() async {
    Response response =
        await HttpUtils.get("http://localhost:8091/kafka/query");
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
                style: TextStyle(
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
        "http://localhost:8091/kafka/topic/detail/query?sourceId=$brokerId&topic=$topicName";
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

  Future<List<DropdownMenuItem<String>>> _queryKafkaTopic(
      String? sourceId, String? keyword) async {
    String url = "http://localhost:8091/kafka/topic/query?sourceId=$sourceId";
    if (keyword != null) {
      url = url + "&keyword=$keyword";
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
                icon: Icon(Icons.description),
                tooltip: 'TopicDetail',
                onPressed: () async {
                  KafkaTopicDescriptionModel topicDetail =
                      await _queryKafkaTopicDetail(brokerDropdownValue!, name);
                  int ll = topicDetail.partitions.length;
                  showTopicDetail(context, topicDetail);
                },
              ),
              IconButton(
                icon: Icon(Icons.delete),
                tooltip: 'Delete',
                onPressed: () {
                  Log.i("press delete");
                },
              ),
              IconButton(
                icon: Icon(Icons.people),
                tooltip: 'Consumer',
                onPressed: () {
                  Log.i("press delete");
                },
              )
            ],
          )),
        ]));
      }
    });
    return topicDropdownList;
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
                    hint: Text("请选择Kafka环境"),
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
                    hint: Text("请输入topic"),
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
                    Log.i("print button");
                  },
                  child: Text("创建Topic"),
                ),
              ],
            ),
          ),
          Expanded(
              child: ConstrainedBox(
            constraints: BoxConstraints(
                minWidth: double.infinity,
                maxWidth: double.infinity,
                minHeight: 20.0),
            child: DataTable(
              columns: [
                DataColumn(
                  label: Text('Topic Name'),
                ),
                DataColumn(
                    label: Text('Topic Type'),
                    numeric: true,
                    onSort: (int columnIndex, bool ascending) {}),
                DataColumn(label: Text('Operation'))
              ],
              rows: topicRows,
            ),
          )),
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
            DataCell(Text("broker:" + partition.leader.id.toString())),
            DataCell(Text(partition.replicas
                .map((event) => "broker:" + event.id.toString())
                .reduce((previous, element) => previous + "," + element))),
            DataCell(Text(partition.isr
                .map((event) => "broker:" + event.id.toString())
                .reduce((previous, element) => previous + "," + element))),
            DataCell(Text(partition.beginningOffset.toString())),
            DataCell(Text(partition.endOffset.toString())),
            DataCell(Text(
                (partition.endOffset - partition.beginningOffset).toString())),
          ]));
        }
        return AlertDialog(
          title: Text(topicDetail.name + '分区详情'),
          content: Container(
            width: double.maxFinite,
            child: SingleChildScrollView(
              scrollDirection: Axis.vertical,
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: DataTable(
                  columns: [
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
              child: Text('确认'),
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
