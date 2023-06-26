// 枚举类型 - 请求类型
import 'package:desktop/utils/Log.dart';
import 'package:dio/dio.dart';

enum HttpType { HttpTypeGet, HttpTypePost }

class HttpUtils {
  // 单例方法
  static Dio? _dioInstance;

  static Dio getHttpUtils() {
    if (_dioInstance == null) {
      _dioInstance = Dio();
    }
    return _dioInstance!;
  }

  // 对外抛出方法 - get请求
  static Future<Response> get(String requestUrl) async {
    return await _sendHttpRequest(HttpType.HttpTypeGet, requestUrl);
  }

  // 对外抛出方法 - post请求
  static Future<Response> post(String requestUrl,
      {Map<String, dynamic>? queryParameters, dynamic data}) async {
    return await _sendHttpRequest(HttpType.HttpTypePost, requestUrl,
        queryParameters: queryParameters, data: data);
  }

  // 私有方法 - 处理get请求、post请求
  static Future _sendHttpRequest(HttpType type, String requestUrl,
      {Map<String, dynamic>? queryParameters, dynamic data}) async {
    try {
      switch (type) {
        case HttpType.HttpTypeGet:
          return await getHttpUtils().get(requestUrl);
        case HttpType.HttpTypePost:
          return await await getHttpUtils()
              .post(requestUrl, queryParameters: queryParameters, data: data);
        default:
          throw Exception('报错了：请求只支持get和post');
      }
    } on DioException catch (e) {
      Log.e("报错:$e");
    }
  }

  // 对外抛出方法 - 下载文件
  static void downloadFile(String downLoadUrl, String savePath,
      void Function(bool result) func) async {
    DateTime timeStart = DateTime.now();
    print('开始下载～当前时间：$timeStart');
    try {
      Dio dio = getHttpUtils();
      var response = await dio.download(downLoadUrl, savePath,
          onReceiveProgress: (int count, int total) {
        String progressValue = (count / total * 100).toStringAsFixed(1);
        print('当前下载进度:$progressValue%');
      }).whenComplete(() {
        DateTime timeEnd = DateTime.now();
        //用时多少秒
        int second_use = timeEnd.difference(timeStart).inSeconds;
        print('下载文件耗时$second_use秒');
        func(true);
      });
    } catch (e) {
      print("downloadFile报错：$e");
    }
  }
}
