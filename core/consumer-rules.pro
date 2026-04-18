# Keep root package classes for JNI
-keep class root.** { *; }
-keepclassmembers class root.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep DoRequestListener, VerifyResponseListener, VPN interfaces
-keep interface root.DoRequestListener { *; }
-keep interface root.VerifyResponseListener { *; }
-keep interface root.VPN { *; }

# Keep ConfigParams, FeedbackParams, ReportParams classes
-keep class root.ConfigParams { 
    <init>();
    <init>(int);
    *; 
}
-keep class root.FeedbackParams { 
    <init>();
    <init>(int);
    *; 
}
-keep class root.ReportParams { 
    <init>();
    <init>(int);
    *; 
}
-keep class root.DeviceParams { 
    <init>();
    <init>(int);
    *; 
}

# Keep constructors for JNI
-keepclassmembers class root.* {
    <init>(int);
}

# Keep go/Seq and all inner classes for gobind JNI runtime
-keep class go.Seq { *; }
-keep class go.Seq$* { *; }
-keepclassmembers class go.Seq { *; }
-keepclassmembers class go.Seq$* { *; }

# Keep go.Universe
-keep class go.Universe { *; }
-keep class go.Universe$* { *; }

# Keep LeafService and LeafJni
-keep class com.leaf.LeafService { *; }
-keep class com.leaf.LeafJni { *; }
