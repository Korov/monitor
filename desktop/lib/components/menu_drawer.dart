import 'package:desktop/components/kafka/kafka_config.dart';
import 'package:desktop/components/kafka/kafka_manager.dart';
import 'package:desktop/utils/log.dart';
import 'package:flutter/material.dart';

import '../generated/l10n.dart';
import '../utils/constant.dart';

class MenuDrawer extends StatelessWidget {
  const MenuDrawer({
    super.key,
  });

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
                      child: Icon(Icons.people_alt),
                    ),
                  ),
                  Text(
                    "Admin",
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
                      initiallyExpanded: true,
                      childrenPadding: const EdgeInsets.only(left: 16.0),
                      children: <Widget>[
                        ListTile(
                            title: Text(S.of(context).kafkaConfig),
                            onTap: () async {
                              if (Cache.cachedRoute.contains(
                                  ConstantKey.kafkaConfig.toString())) {
                                return;
                              }
                              Cache.cachedRoute
                                  .add(ConstantKey.kafkaConfig.toString());
                              var result = await Navigator.pushNamed(
                                  context, "KafkaConfig",
                                  arguments: KafkaConfig(
                                    key: ConstantKey.kafkaConfig,
                                    text: "input param",
                                  ));
                              //输出`TipRoute`路由返回结果
                              Log.i("路由返回值: $result");
                            }),
                        ListTile(
                            title: Text(S.of(context).kafkaManager),
                            onTap: () async {
                              if (Cache.cachedRoute.contains(
                                  ConstantKey.kafkaManager.toString())) {
                                return;
                              }
                              Cache.cachedRoute
                                  .add(ConstantKey.kafkaManager.toString());
                              await Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) {
                                    return KafkaManager(
                                      key: ConstantKey.kafkaManager,
                                    );
                                  },
                                ),
                              );
                            }),
                        ExpansionTile(
                            title: Text(S.of(context).kafkaOperator),
                            initiallyExpanded: true,
                            childrenPadding: const EdgeInsets.only(left: 16.0),
                            children: <Widget>[
                              ListTile(
                                  title: Text(S.of(context).kafkaProducer)),
                              ListTile(
                                  title: Text(S.of(context).kafkaConsumer)),
                            ]),
                      ]),
                  ExpansionTile(
                      title: Text(S.of(context).zookeeper),
                      initiallyExpanded: true,
                      childrenPadding: const EdgeInsets.only(left: 16.0),
                      children: <Widget>[
                        ListTile(
                          title: Text(S.of(context).zookeeperConfig),
                          onTap: () => {Log.i("tap the list tile")},
                        ),
                        ListTile(
                          title: Text(S.of(context).zookeeperTree),
                          onTap: () => {Log.i("tap the list tile")},
                        ),
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
