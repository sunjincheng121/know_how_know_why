namespace java service.iotdb
/**
  * The first thing to know about are types. The available types in Thrift are:
  *
  *  bool        Boolean, one byte
  *  i8 (byte)   Signed 8-bit integer
  *  i16         Signed 16-bit integer
  *  i32         Signed 32-bit integer
  *  i64         Signed 64-bit integer
  *  double      64-bit floating point value
  *  string      String
  *  binary      Blob (byte array)
  *  map<t1,t2>  Map from one type to another
  *  list<t1>    Ordered list of one type
  *  set<t1>     Set of unique elements of one type
  *
  */
service Hello{
    string helloString(1:string para)
    i32 helloInt(1:i32 para)
}