import 'package:desktop/components/MenuDrawer.dart';
import 'package:desktop/utils/HttpUtils.dart';
import 'package:desktop/utils/Log.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../../model/KafkaConfig.dart';
import '../../utils/Constant.dart';

class KafkaManager extends StatefulWidget {
  KafkaManager({required Key key}) : super(key: key);

  @override
  _KafkaManagerState createState() => _KafkaManagerState();
}

class _KafkaManagerState extends State<KafkaManager> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _addressController = TextEditingController();

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
  String? dropdownValue = null;

  List<DropdownMenuItem<String>> dropdownList = [];

  @override
  void initState() {
    super.initState();
    /*for (var value in ['One', 'Two', 'Three', 'Four']) {
      DropdownMenuItem<String> dropdownMenuItem = new DropdownMenuItem<String>(
        value: value,
        child: Text(value),
      );
      dropdownList.add(dropdownMenuItem);
    }
    Log.i(dropdownList.length);*/
    dropdownList.add(new DropdownMenuItem<String>(
      value: '',
      child: Text(''),
    ));
  }

  _queryAllKafka() async {
    Response response =
        await HttpUtils.get("http://localhost:8091/kafka/query");
    HttpUtils.get("http://localhost:8091/kafka/query").then((value) {
      List data = response.data['data'];
      dropdownList = [];
      setState(() {
        dropdownList = [];
        for (var item in data) {
          Log.i("name:${item['name']}, address:${item['broker']}");
          DropdownMenuItem<String> dropdownMenuItem =
              new DropdownMenuItem<String>(
            value: item['name'],
            child: Row(
              children: [
                Text(item['name']),
                Text(item['broker']),
              ],
            ),
          );
          dropdownList.add(dropdownMenuItem);
        }
        Log.i(dropdownList.length);
      });
    });
    List data = response.data['data'];
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
                FutureBuilder<List>(builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    return DropdownButton(
                      items: snapshot.data
                          ?.map((option) => DropdownMenuItem(
                                value: option['name'],
                                child: Row(
                                  children: [
                                    Text(option['name']),
                                    Text(option['broker']),
                                  ],
                                ),
                              ))
                          .toList(),
                      onChanged: (value) {},
                    );
                  } else if (snapshot.hasError) {
                    return Text(snapshot.error.toString());
                  } else {
                    return CircularProgressIndicator();
                  }
                }),
                DropdownButton(
                    hint: Text("请选择Kafka环境"),
                    value: dropdownValue,
                    items: dropdownList,
                    onChanged: (value) {
                      setState(() {
                        dropdownValue = value!;
                      });
                      Log.i("chanaged value:$value");
                    },
                    onTap: () async {
                      await _queryAllKafka();
                      await Future.delayed(Duration(seconds: 3));
                      Log.i("drop down tap");
                    }),
                /*DropdownButton(
                  hint: Text("请选择Topic"),
                  value: dropdownValue,
                  items: dropdownList,
                  onChanged: (value) {
                    setState(() {
                      dropdownValue = value!;
                    });
                    Log.i("chanaged value:$value");
                  },
                ),*/
                TextButton(
                  onPressed: () {
                    Log.i("print button");
                  },
                  child: Text("button"),
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
                  label: Text('Cluster Name'),
                ),
                DataColumn(
                    label: Text('Address'),
                    numeric: true,
                    onSort: (int columnIndex, bool ascending) {}),
                DataColumn(label: Text('Operation'))
              ],
              rows: [],
            ),
          )),
        ],
      ),
    );
  }
}
