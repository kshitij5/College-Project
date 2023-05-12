import 'dart:convert';
import 'dart:ffi';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:gas_detection/Result.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:webview_flutter/webview_flutter.dart';

class HomePage extends StatefulWidget {
  final String id;

  const HomePage({Key? key, required this.id}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState(id: id);
}

class _HomePageState extends State<HomePage> {
  final String id;

  _HomePageState({required this.id});

  String? _url;
  late final WebViewController _controller;

  Future<Result> fetchProducts() async {
    final response = await http.get(Uri.parse(
        'https://api.thingspeak.com/channels/$id/feeds.json?results=1'));
    if (response.statusCode == 200) {
      return Result.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Unable to fetch data from the REST API');
    }
  }

  showAlertDialog(BuildContext context) {
    Widget okButton = TextButton(
      child: Text("OK"),
      onPressed: () {
        Navigator.of(context, rootNavigator: true).pop();
      },
    );

    AlertDialog alert = AlertDialog(
      title: Text("SOS"),
      content: Text("MSG HEEREE"),
      actions: [
        okButton,
      ],
    );

    // show the dialog
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return alert;
      },
    );
  }

  @override
  void initState() {
    super.initState();
    _url =
        "https://thingspeak.com/channels/$id/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15&width=700&height=500";

    final WebViewController controller = WebViewController();

    controller
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setBackgroundColor(const Color(0x00000000))
      ..setNavigationDelegate(
        NavigationDelegate(
          onProgress: (int progress) {
            debugPrint('WebView is loading (progress : $progress%)');
          },
          onPageStarted: (String url) {
            debugPrint('Page started loading: $url');
          },
          onPageFinished: (String url) {
            debugPrint('Page finished loading: $url');

            print(url);
            fetchProducts().then((value) => {
                  if (double.parse(value.feeds![0].field1!.substring(0, 6)) >
                      280)
                    showAlertDialog(context)
                });
          },
          onWebResourceError: (WebResourceError error) {
            debugPrint('''
Page resource error:
  code: ${error.errorCode}
  description: ${error.description}
  errorType: ${error.errorType}
  isForMainFrame: ${error.isForMainFrame}
          ''');
          },
          onUrlChange: (UrlChange change) {
            debugPrint('url change to ${change.url}');
          },
        ),
      )
      ..addJavaScriptChannel(
        'Toaster',
        onMessageReceived: (JavaScriptMessage message) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(message.message)),
          );
        },
      )
      ..loadRequest(Uri.parse(_url!));

    _controller = controller;
  }

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    double height = MediaQuery.of(context).size.height;
    return Scaffold(
        body: Column(
      children: [
        Container(
          height: height / 2,
          child: WebViewWidget(
            controller: _controller,
          ),
        ),
        FutureBuilder<Result>(
          future: fetchProducts(), // async work
          builder: (BuildContext context, AsyncSnapshot<Result> snapshot) {
            if( snapshot.connectionState == ConnectionState.waiting){
              return  Center(child: Text('Please wait its loading...'));
            }else{
              if (snapshot.hasError)
                return Center(child: Text('Error: ${snapshot.error}'));
              else {

                // if (double.parse(snapshot.data!.feeds![0].field1!.substring(0, 6)) > 260)
                //   showAlertDialog(context);
                return Center(child: Text('${snapshot.data?.toJson()}'));

              }

            }

          },
        )
        // ElevatedButton(
        //   child: Text('Show alert'),
        //   onPressed: () {
        //     showAlertDialog(context);
        //   },
        // ),
      ],
    ));
  }
}
