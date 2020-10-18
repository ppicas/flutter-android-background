import 'package:flutter/cupertino.dart';

import 'counter_service.dart';

void backgroundMain() {
  WidgetsFlutterBinding.ensureInitialized();

  CounterService.instance().startCounting();
}
