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

  _KafkaManagerState();

  List<DataRow> _rows = [];

  @override
  void initState() {
    super.initState();
    _updateTable();
    Cache.cachedRoute.add("kafkaManager");
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
                  DataColumn(
                    label: Text('Operation'),
                  ),
                ],
                rows: _rows,
              ),
            ),
            Flex(
              direction: Axis.horizontal,
              children: [
                TextButton(
                    onPressed: () async {
                      showDialog(
                          context: context,
                          builder: (BuildContext context) {
                            return SimpleDialog(
                              title: Text('Add Kafka Address'),
                              children: <Widget>[
                                Form(
                                  key: _formKey,
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisSize: MainAxisSize.max,
                                    children: <Widget>[
                                      Row(
                                        children: [
                                          Flexible(flex: 1, child: Text("名字：")),
                                          Flexible(
                                            flex: 2,
                                            child: TextFormField(
                                              controller: _nameController,
                                              validator: (value) {
                                                if (value == null ||
                                                    value.isEmpty) {
                                                  return '请输入名字';
                                                }
                                                return null;
                                              },
                                              decoration: InputDecoration(
                                                hintText: '请输入名字',
                                              ),
                                            ),
                                          )
                                        ],
                                      ),
                                      Row(
                                        children: [
                                          Flexible(flex: 1, child: Text("地址：")),
                                          Flexible(
                                            flex: 2,
                                            child: TextFormField(
                                              controller: _addressController,
                                              validator: (value) {
                                                if (value == null ||
                                                    value.isEmpty) {
                                                  return '请输入地址';
                                                }
                                                return null;
                                              },
                                              decoration: InputDecoration(
                                                hintText: '请输入地址',
                                              ),
                                            ),
                                          )
                                        ],
                                      ),
                                    ],
                                  ),
                                ),
                                Row(
                                  children: [
                                    TextButton(
                                      child: Text('确定'),
                                      onPressed: () {
                                        if (_formKey.currentState != null) {
                                          if (_formKey.currentState!
                                              .validate()) {
                                            Log.i(_nameController.text);
                                            Log.i(_addressController.text);
                                            _addConfig(_nameController.text,
                                                _addressController.text);
                                            _nameController.text = "";
                                            _addressController.text = "";
                                            // 执行保存操作
                                            Navigator.of(context).pop();
                                          }
                                        }
                                      },
                                    ),
                                    TextButton(
                                      child: Text('取消'),
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
                    child: Text("Add Environment")),
              ],
            ),
          ],
        ),
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
