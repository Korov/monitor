import 'package:flutter/material.dart';

class ConstantKey {
  static final kafkaConfig = GlobalKey(debugLabel: "KafkaConfig");
  static final kafkaManager = GlobalKey(debugLabel: "KafkaManager");
  static final home = GlobalKey(debugLabel: "Home");
}

class Cache {
  static Set<String> cachedRoute = {};
}

class Global {
  static String uri = "http://localhost:8091";
}
