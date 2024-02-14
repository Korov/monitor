import 'package:desktop/utils/constant.dart';
import 'package:fluent_ui/fluent_ui.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:system_theme/system_theme.dart';

import 'components/kafka/kafka_config.dart';
import 'components/kafka/kafka_manager.dart';
import 'components/home.dart';
import 'generated/l10n.dart';

void main() {
  runApp(const Monitor());
}

class Monitor extends StatelessWidget {
  const Monitor({super.key});

  @override
  Widget build(BuildContext context) {
    return FluentApp(
      title: 'Monitor',
      debugShowCheckedModeBanner: false,
      theme: FluentThemeData(
        accentColor: SystemTheme.accentColor.accent.toAccentColor(),
      ),
      routes: {
        "KafkaConfig": (context) => KafkaConfig(
              key: ConstantKey.kafkaConfig,
              text: "",
            ),
        "KafkaManager": (context) => KafkaManager(
              key: ConstantKey.kafkaManager,
            )
      },
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        S.delegate
      ],
      supportedLocales: S.delegate.supportedLocales,
      locale: const Locale('en', 'US'),
      //手动指定locale
      /*home: KafkaConfig(
        key: GlobalKey<FormState>(),
        text: 'KafkaConfig',
      ),*/
      home: Home(
        key: ConstantKey.home,
        userName: "Admin",
      ),
    );
  }
}
