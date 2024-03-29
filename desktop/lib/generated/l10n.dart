// GENERATED CODE - DO NOT MODIFY BY HAND
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'intl/messages_all.dart';

// **************************************************************************
// Generator: Flutter Intl IDE plugin
// Made by Localizely
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, lines_longer_than_80_chars
// ignore_for_file: join_return_with_assignment, prefer_final_in_for_each
// ignore_for_file: avoid_redundant_argument_values, avoid_escaping_inner_quotes

class S {
  S();

  static S? _current;

  static S get current {
    assert(_current != null,
        'No instance of S was loaded. Try to initialize the S delegate before accessing S.current.');
    return _current!;
  }

  static const AppLocalizationDelegate delegate = AppLocalizationDelegate();

  static Future<S> load(Locale locale) {
    final name = (locale.countryCode?.isEmpty ?? false)
        ? locale.languageCode
        : locale.toString();
    final localeName = Intl.canonicalizedLocale(name);
    return initializeMessages(localeName).then((_) {
      Intl.defaultLocale = localeName;
      final instance = S();
      S._current = instance;

      return instance;
    });
  }

  static S of(BuildContext context) {
    final instance = S.maybeOf(context);
    assert(instance != null,
        'No instance of S present in the widget tree. Did you add S.delegate in localizationsDelegates?');
    return instance!;
  }

  static S? maybeOf(BuildContext context) {
    return Localizations.of<S>(context, S);
  }

  /// `Kafka`
  String get kafka {
    return Intl.message(
      'Kafka',
      name: 'kafka',
      desc: '',
      args: [],
    );
  }

  /// `Config`
  String get kafkaConfig {
    return Intl.message(
      'Config',
      name: 'kafkaConfig',
      desc: '',
      args: [],
    );
  }

  /// `Manager`
  String get kafkaManager {
    return Intl.message(
      'Manager',
      name: 'kafkaManager',
      desc: '',
      args: [],
    );
  }

  /// `Operator`
  String get kafkaOperator {
    return Intl.message(
      'Operator',
      name: 'kafkaOperator',
      desc: '',
      args: [],
    );
  }

  /// `Producer`
  String get kafkaProducer {
    return Intl.message(
      'Producer',
      name: 'kafkaProducer',
      desc: '',
      args: [],
    );
  }

  /// `Consumer`
  String get kafkaConsumer {
    return Intl.message(
      'Consumer',
      name: 'kafkaConsumer',
      desc: '',
      args: [],
    );
  }

  /// `Zookeeper`
  String get zookeeper {
    return Intl.message(
      'Zookeeper',
      name: 'zookeeper',
      desc: '',
      args: [],
    );
  }

  /// `Config`
  String get zookeeperConfig {
    return Intl.message(
      'Config',
      name: 'zookeeperConfig',
      desc: '',
      args: [],
    );
  }

  /// `Tree`
  String get zookeeperTree {
    return Intl.message(
      'Tree',
      name: 'zookeeperTree',
      desc: '',
      args: [],
    );
  }
}

class AppLocalizationDelegate extends LocalizationsDelegate<S> {
  const AppLocalizationDelegate();

  List<Locale> get supportedLocales {
    return const <Locale>[
      Locale.fromSubtags(languageCode: 'en', countryCode: 'US'),
      Locale.fromSubtags(languageCode: 'zh', countryCode: 'CN'),
    ];
  }

  @override
  bool isSupported(Locale locale) => _isSupported(locale);
  @override
  Future<S> load(Locale locale) => S.load(locale);
  @override
  bool shouldReload(AppLocalizationDelegate old) => false;

  bool _isSupported(Locale locale) {
    for (var supportedLocale in supportedLocales) {
      if (supportedLocale.languageCode == locale.languageCode) {
        return true;
      }
    }
    return false;
  }
}
