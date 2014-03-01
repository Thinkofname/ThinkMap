
import "dart:isolate";
import "package:map_viewer/map_viewer.dart";

void main(List args, SendPort port) {
//  LoaderIsolate.isolate(args, port);
  IsolateWorldProxy.isolateMain(args, port);
}