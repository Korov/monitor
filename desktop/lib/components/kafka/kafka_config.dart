import 'package:desktop/utils/constant.dart';
import 'package:desktop/utils/http_utils.dart';
import 'package:desktop/utils/log.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../../model/kafka_config.dart';

class KafkaConfig extends StatefulWidget {
  const KafkaConfig({
    required Key key,
    required this.text,
  }) : super(key: key);
  final String text;

  @override
  _KafkaConfigState createState() => _KafkaConfigState(text: this.text);
}

class _KafkaConfigState extends State<KafkaConfig> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _addressController = TextEditingController();

  _KafkaConfigState({
    this.text = "",
  });

  String text;

  List<DataRow> _rows = [];

  @override
  void initState() {
    super.initState();
    _updateTable();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          children: <Widget>[
            Expanded(
              child: SingleChildScrollView(
                scrollDirection: Axis.vertical,
                child: SizedBox(
                  width: double.infinity,
                  child: DataTable(
                    sortColumnIndex: 1,
                    sortAscending: true,
                    columns: [
                      const DataColumn(
                        label: Text('Cluster Name'),
                      ),
                      DataColumn(
                          label: const Text('Address'),
                          numeric: true,
                          onSort: (int columnIndex, bool ascending) {}),
                      const DataColumn(
                        label: Text('Operation'),
                      ),
                    ],
                    rows: _rows,
                  ),
                ),
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
                              title: const Text('Add Kafka Address'),
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
                                          const Flexible(
                                              flex: 1, child: Text("名字：")),
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
                                              flex: 1, child: Text("地址：")),
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
                                              decoration: const InputDecoration(
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
                                      child: const Text('确定'),
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
                    child: const Text("Add Environment")),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _updateTable() async {
    String url = "${Global.uri}/kafka/query";
    Log.i(url);
    Response response = await HttpUtils.get(url);
    Log.i(response);
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

  Future<void> _addConfig(String name, String address) async {
    Map<String, String> body = {};
    body['name'] = name;
    body['broker'] = address;
    Response response =
        await HttpUtils.post("${Global.uri}/kafka/add", data: body);
    dynamic data = response.data['data'];
    Log.i(data);
    _updateTable();
  }
}
