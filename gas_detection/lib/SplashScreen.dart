import 'dart:async';
import 'package:flutter/material.dart';
import 'package:gas_detection/HomePage.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'LoginPage.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  late String _id;

  _getId() async {
    final SharedPreferences prefs = await _prefs;
    _id = prefs.getString("id") ?? "";
  }

  openStartPage() async {
    await Future.delayed(
      Duration(seconds: 3),
          () => Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => _id=="" ? LoginPage() : HomePage(id: _id)),
      ),
    );
  }

  @override
  void initState() {
    super.initState();
    _getId();
    openStartPage();
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      extendBody: false,
      body: Center(
        child: Text(
          'Gas Leakage',
          style: TextStyle(fontSize: 32),
        ),
      ),
    );
  }
}
