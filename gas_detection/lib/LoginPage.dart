import 'package:flutter/material.dart';
import 'package:gas_detection/HomePage.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _idController = TextEditingController();

  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  late Future<int> _counter;

  @override
  void initState() {
    super.initState();
    _counter = _prefs.then((SharedPreferences prefs) {
      return prefs.getInt('counter') ?? 0;
    });
  }

  Future<void> _saveId() async {
    final SharedPreferences prefs = await _prefs;
    prefs.setString("id", _idController.text);

    Navigator.of(context).push(
        MaterialPageRoute(builder: (_) => HomePage(id: _idController.text,))
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Login Page'),
      ),
      body: Column(
        children: [
          TextField(
            controller: _idController,
            decoration: const InputDecoration(
              hintText: 'Enter your ID',
            ),
          ),
          ElevatedButton(
            onPressed: _saveId,
            child: const Text('Submit'),
          )
        ],
      ),
    );
  }
}