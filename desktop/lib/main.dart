import 'package:desktop/utils/constant.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'components/kafka/kafka_config.dart';
import 'components/kafka/kafka_manager.dart';
import 'generated/l10n.dart';

void main() {
  runApp(const Monitor());
}

class Monitor extends StatelessWidget {
  const Monitor({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Monitor',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.grey),
        useMaterial3: true,
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
      home: KafkaConfig(
        key: GlobalKey<FormState>(),
        text: 'KafkaConfig',
      ),
    );
  }
}
