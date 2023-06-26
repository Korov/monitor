import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'components/kafka/KafkaConfig.dart';
import 'generated/l10n.dart';

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
      localizationsDelegates: [
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
