class Result {
  Channel? channel;
  List<Feeds>? feeds;

  Result({this.channel, this.feeds});

  Result.fromJson(Map<String, dynamic> json) {
    channel =
        json['channel'] != null ? new Channel.fromJson(json['channel']) : null;
    if (json['feeds'] != null) {
      feeds = <Feeds>[];
      json['feeds'].forEach((v) {
        feeds!.add(new Feeds.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.channel != null) {
      data['channel'] = this.channel!.toJson();
    }
    if (this.feeds != null) {
      data['feeds'] = this.feeds!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Channel {
  int? id;
  String? name;
  String? latitude;
  String? longitude;
  String? field1;
  String? createdAt;
  String? updatedAt;
  int? lastEntryId;

  Channel(
      {this.id,
      this.name,
      this.latitude,
      this.longitude,
      this.field1,
      this.createdAt,
      this.updatedAt,
      this.lastEntryId});

  Channel.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    field1 = json['field1'];
    createdAt = json['created_at'];
    updatedAt = json['updated_at'];
    lastEntryId = json['last_entry_id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['field1'] = this.field1;
    data['created_at'] = this.createdAt;
    data['updated_at'] = this.updatedAt;
    data['last_entry_id'] = this.lastEntryId;
    return data;
  }
}

class Feeds {
  String? createdAt;
  int? entryId;
  String? field1;

  Feeds({this.createdAt, this.entryId, this.field1});

  Feeds.fromJson(Map<String, dynamic> json) {
    createdAt = json['created_at'];
    entryId = json['entry_id'];
    field1 = json['field1'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['created_at'] = this.createdAt;
    data['entry_id'] = this.entryId;
    data['field1'] = this.field1;
    return data;
  }
}
