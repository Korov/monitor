import 'package:desktop/utils/constant.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'components/kafka/kafka_config.dart';
import 'components/kafka/kafka_manager.dart';
import 'generated/i10n.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Monitor',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      routes: {
        "KafkaConfig": (context) => KafkaConfig(
              key: ConstantKey.kafkaKey,
              text: "",
            ),
        "KafkaManager": (context) => KafkaManager(
              key: const Key("kafkaManager"),
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
