import 'package:desktop/components/MenuDrawer.dart';
import 'package:desktop/utils/HttpUtils.dart';
import 'package:desktop/utils/Log.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../../model/KafkaConfig.dart';

class KafkaConfig extends StatefulWidget {
  KafkaConfig({
    required Key key,
    required this.text,
  }) : super(key: key);
  final String text;

  @override
  _KafkaConfigState createState() => _KafkaConfigState(text: this.text);
}

class _KafkaConfigState extends State<KafkaConfig> {
  int _selectedIndex = 1;

  _KafkaConfigState({
    this.text = "",
  });

  String text;

  List<DataRow> _rows = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        //导航栏
        title: Text("Kafka Config"),
        actions: <Widget>[
          //导航栏右侧菜单
          IconButton(
              icon: Icon(Icons.arrow_back),
              onPressed: () => Navigator.pop(context, "我是返回值")),
        ],
      ),
      drawer: MenuDrawer(),
      body: Center(
        child: Column(
          children: <Widget>[
            ConstrainedBox(
              constraints: BoxConstraints(
                  minWidth: double.infinity,
                  maxWidth: double.infinity,
                  minHeight: 20.0),
              child: DataTable(
                sortColumnIndex: 1,
                sortAscending: true,
                columns: [
                  DataColumn(
                    label: Text('Cluster Name'),
                  ),
                  DataColumn(
                      label: Text('Address'),
                      numeric: true,
                      onSort: (int columnIndex, bool ascending) {}),
                  DataColumn(label: Text('Operation')),
                ],
                rows: _rows,
              ),
            ),
            Flex(
              direction: Axis.horizontal,
              children: [
                MaterialButton(
                    onPressed: () async {
                      _updateTable();
                      Response response = await HttpUtils.get(
                          "http://localhost:8091/kafka/query");
                      List data = response.data['data'];
                      Log.i(data.length);
                    },
                    child: Text("Button")),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  void _onAdd() {}

  @override
  void initState() {
    super.initState();
    _updateTable();
  }

  Future<void> _updateTable() async {
    Response response =
        await HttpUtils.get("http://localhost:8091/kafka/query");
    List data = response.data['data'];
    setState(() {
      for (var config in data) {
        KafkaConfigModel kafkaConfig = KafkaConfigModel.fromJson(config);
        _rows.add(DataRow(cells: [
          DataCell(Text(kafkaConfig.name)),
          DataCell(Text(kafkaConfig.broker)),
          DataCell(Text("operation")),
        ]));
      }
    });
  }
}
