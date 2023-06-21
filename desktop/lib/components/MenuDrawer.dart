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
                        ListTile(title: Text(S.of(context).kafkaConfig)),
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
                        ListTile(title: Text(S.of(context).zookeeperConfig)),
                        ListTile(title: Text(S.of(context).zookeeperTree)),
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
