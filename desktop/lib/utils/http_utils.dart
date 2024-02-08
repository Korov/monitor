import 'package:desktop/utils/log.dart';
import 'package:dio/dio.dart';

enum HttpType { httpTypeGet, httpTypePost, httpTypeDelete, httpTypePut }

class HttpUtils {
  // 单例方法
  static Dio? _dioInstance;

  static Dio getHttpUtils() {
    _dioInstance ??= Dio();
    return _dioInstance!;
  }

  // 对外抛出方法 - get请求
  static Future<Response> get(String requestUrl) async {
    return await _sendHttpRequest(HttpType.httpTypeGet, requestUrl);
  }

  // 对外抛出方法 - post请求
  static Future<Response> post(String requestUrl,
      {Map<String, dynamic>? queryParameters, dynamic data}) async {
    return await _sendHttpRequest(HttpType.httpTypePost, requestUrl,
        queryParameters: queryParameters, data: data);
  }

  static Future<Response> delete(String requestUrl,
      {Map<String, dynamic>? queryParameters, dynamic data}) async {
    return await _sendHttpRequest(HttpType.httpTypeDelete, requestUrl,
        queryParameters: queryParameters, data: data);
  }

  static Future<Response> put(String requestUrl,
      {Map<String, dynamic>? queryParameters, dynamic data}) async {
    return await _sendHttpRequest(HttpType.httpTypePut, requestUrl,
        queryParameters: queryParameters, data: data);
  }

  // 私有方法 - 处理get请求、post请求
  static Future _sendHttpRequest(HttpType type, String requestUrl,
      {Map<String, dynamic>? queryParameters, dynamic data}) async {
    try {
      switch (type) {
        case HttpType.httpTypeGet:
          return await getHttpUtils().get(requestUrl);
        case HttpType.httpTypePost:
          return await getHttpUtils()
              .post(requestUrl, queryParameters: queryParameters, data: data);
        case HttpType.httpTypeDelete:
          return await getHttpUtils()
              .delete(requestUrl, queryParameters: queryParameters, data: data);
        case HttpType.httpTypePut:
          return await getHttpUtils()
              .put(requestUrl, queryParameters: queryParameters, data: data);
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
    Log.i('开始下载～当前时间：$timeStart');
    try {
      Dio dio = getHttpUtils();
      await dio.download(downLoadUrl, savePath,
          onReceiveProgress: (int count, int total) {
        String progressValue = (count / total * 100).toStringAsFixed(1);
        Log.i('当前下载进度:$progressValue%');
      }).whenComplete(() {
        DateTime timeEnd = DateTime.now();
        //用时多少秒
        int secondUse = timeEnd.difference(timeStart).inSeconds;
        Log.i('下载文件耗时$secondUse秒');
        func(true);
      });
    } catch (e) {
      Log.i("downloadFile报错：$e");
    }
  }
}
