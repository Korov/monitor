import 'package:desktop/components/kafka/kafka_config.dart';
import 'package:fluent_ui/fluent_ui.dart';

import '../generated/l10n.dart';
import '../utils/constant.dart';
import 'kafka/kafka_manager.dart';

class Home extends StatefulWidget {
  final String userName;

  const Home({required Key key, required this.userName}) : super(key: key);

  @override
  HomeState createState() => HomeState();
}

class HomeState extends State<Home> {
  int topIndex = 0;
  late String userName;

  PaneDisplayMode displayMode = PaneDisplayMode.auto;

  @override
  void initState() {
    super.initState();
    userName = widget.userName;
  }

  @override
  Widget build(BuildContext context) {
    List<NavigationPaneItem> items = [
      PaneItem(
        icon: const Icon(FluentIcons.home),
        title: Text(S.of(context).kafkaConfig),
        body: KafkaConfig(
          key: ConstantKey.kafkaConfig,
          text: "input param",
        ),
      ),
      PaneItem(
        icon: const Icon(FluentIcons.home),
        title: Text(S.of(context).kafkaManager),
        body: KafkaManager(
          key: ConstantKey.kafkaManager,
        ),
      ),
      PaneItemSeparator(),
      PaneItem(
        icon: const Icon(FluentIcons.issue_tracking),
        title: const Text('Track orders'),
        infoBadge: const InfoBadge(source: Text('8')),
        body: const Text('Track orders'),
      ),
      PaneItem(
        icon: const Icon(FluentIcons.disable_updates),
        title: const Text('Disabled Item'),
        body: const Text('Disabled Item'),
        enabled: false,
      ),
      PaneItemExpander(
        icon: const Icon(FluentIcons.account_management),
        title: const Text('Account'),
        body: const Text('Account'),
        items: [
          PaneItemHeader(header: const Text('Apps')),
          PaneItem(
            icon: const Icon(FluentIcons.mail),
            title: const Text('Mail'),
            body: const Text('Mail'),
          ),
          PaneItem(
            icon: const Icon(FluentIcons.calendar),
            title: const Text('Calendar'),
            body: const Text('Calendar'),
          ),
        ],
      ),
    ];

    return NavigationView(
      appBar: NavigationAppBar(
        title: Text(userName),
      ),
      pane: NavigationPane(
        selected: topIndex,
        onChanged: (index) => setState(() => topIndex = index),
        displayMode: displayMode,
        items: items,
        footerItems: [
          PaneItem(
            icon: const Icon(FluentIcons.settings),
            title: const Text('Settings'),
            body: const Text('Settings'),
          ),
          PaneItemAction(
            icon: const Icon(FluentIcons.add),
            title: const Text('Add New Item'),
            onTap: () {
              // Your Logic to Add New `NavigationPaneItem`
              items.add(
                PaneItem(
                  icon: const Icon(FluentIcons.new_folder),
                  title: const Text('New Item'),
                  body: const Center(
                    child: Text(
                      'This is a newly added Item',
                    ),
                  ),
                ),
              );
              setState(() {});
            },
          ),
        ],
      ),
    );
  }
}
