import 'package:flutter/material.dart';

class ConstantKey {
  static final kafkaKey = GlobalKey(debugLabel: "KafkaConfig");
}

class Cache {
  static Set<String> cachedRoute = {};
}
