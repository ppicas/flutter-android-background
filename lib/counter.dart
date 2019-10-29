import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Counter {
  Counter() {
    _readCount().then((count) => _count.value = count);
  }

  ValueNotifier<int> _count = ValueNotifier(0);

  ValueListenable<int> get count => _count;

  void increment() {
    _count.value++;
    _writeCount(_count.value);
  }

  Future<int> _readCount() async {
    var prefs = await SharedPreferences.getInstance();
    return prefs.getInt('Counter.count') ?? 0;
  }

  Future<void> _writeCount(int count) async {
    var prefs = await SharedPreferences.getInstance();
    return prefs.setInt('Counter.count', count);
  }
}
