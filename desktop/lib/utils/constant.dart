import 'package:flutter/material.dart';

class ConstantKey {
  static final kafkaConfig = GlobalKey(debugLabel: "KafkaConfig");
  static final kafkaManager = GlobalKey(debugLabel: "KafkaManager");
}

class Cache {
  static Set<String> cachedRoute = {};
}

class Global {
  static String uri = "http://localhost:3000";
}
