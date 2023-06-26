import 'package:desktop/components/kafka/KafkaConfig.dart';
import 'package:desktop/utils/Log.dart';
import 'package:flutter/material.dart';

import '../generated/l10n.dart';

class MenuDrawer extends StatelessWidget {
  const MenuDrawer({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: MediaQuery.removePadding(
        context: context,
        //移除抽屉菜单顶部默认留白
        removeTop: true,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            const Padding(
              padding: EdgeInsets.only(top: 38.0),
              child: Row(
                children: <Widget>[
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 16.0),
                    child: ClipOval(
                      child: Text(
                        "add images",
                      ),
                    ),
                  ),
                  Text(
                    "Wendux",
                    style: TextStyle(fontWeight: FontWeight.bold),
                  )
                ],
              ),
            ),
            Expanded(
              child: ListView(
                children: <Widget>[
                  ExpansionTile(
                      title: Text(S.of(context).kafka),
                      children: <Widget>[
                        ListTile(
                            title: Text(S.of(context).kafkaConfig),
                            onTap: () async {
                              // 打开`TipRoute`，并等待返回结果
                              var result = await Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) {
                                    return KafkaConfig(
                                      key: new Key("kafkaConfig"),
                                      text: "input param",
                                    );
                                  },
                                ),
                              );
                              //输出`TipRoute`路由返回结果
                              Log.i("路由返回值: $result");
                            }),
                        ListTile(title: Text(S.of(context).kafkaManager)),
                        ExpansionTile(
                            title: Text(S.of(context).kafkaOperator),
                            children: <Widget>[
                              ListTile(
                                  title: Text(S.of(context).kafkaProducer)),
                              ListTile(
                                  title: Text(S.of(context).kafkaConsumer)),
                            ]),
                      ]),
                  ExpansionTile(
                      title: Text(S.of(context).zookeeper),
                      children: <Widget>[
                        ListTile(
                          title: Text(S.of(context).zookeeperConfig),
                          onTap: () => {Log.i("tap the list tile")},
                        ),
                        ElevatedButton(
                            onPressed: () => Navigator.pop(context, "我是返回值"),
                            child: Text(S.of(context).zookeeperTree)),
                      ]),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
