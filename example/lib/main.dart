import 'package:flutter/material.dart';
import 'package:flutter_logan/flutter_logan.dart';

void main() {
  return runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  void _initLogan() async{
    await Logan.init("0123456789012345", "0123456789012345");
    await Logan.setDebug(false);
  }
  @override
  Widget build(BuildContext context) {
    _initLogan();
    return new MaterialApp(
      title: 'Flutter Demo',
      theme: new ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: new MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  bool _debug = false;
  String _size = "";
  @override
  void initState() {
    super.initState();
  }

  void _incrementCounter() async {
    setState(() {
      _counter++;
    });
    await Logan.w("count: $_counter", 1);
  }
  void _writeData() async {
    await Logan.w("Logan designed by MeituanDianPing", 1);
  }

  void _flush() async {
    await Logan.f();
  }

  void _upload() async {
    Map<dynamic,dynamic> map = await Logan.s("https://openlogan.inf.test.sankuai.com/logan/upload.json", _getTime(), "testAppId", "testUnionid", "testdDviceId");
    int statusCode = map['code'];
    String msg = map['msg'];
    print("code: $statusCode; msg: $msg");
  }

  void _showFileInfo() async {
    Map<dynamic,dynamic> map = await Logan.getAllFilesInfo();
    String fileSize = map[_getTime()].toString();
    print("fileSize: $fileSize byte");
    setState(() {
      if(fileSize != null){
        _size = fileSize + "byte";
      }else{
        _size = "";
      }
    });
  }
  String _getTime(){
    DateTime date = DateTime.now();
    String time = "${date.year.toString()}-${date.month.toString().padLeft(2,'0')}-${date.day.toString().padLeft(2,'0')}";
    print("time: $time");
    return time;
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget.title),
      ),
      body: new Center(
        child: new Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            RaisedButton(
              child: Text('写入一条数据'),
              onPressed: _writeData,
            ),
            RaisedButton(
              child: Text('刷新写入文件'),
              onPressed: _flush,
            ),
            RaisedButton(
              child: Text('上传日志'),
              onPressed: _upload,
            ),
            RaisedButton(
              child: Text('展示文件大小'),
              onPressed: _showFileInfo,
            ),
            Text(_size),
          ],
        ),
      ),
    );
  }
}
