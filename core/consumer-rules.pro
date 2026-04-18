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
-keep class root.ConfigParams { *; }
-keep class root.FeedbackParams { *; }
-keep class root.ReportParams { *; }
-keep class root.DeviceParams { *; }

# Keep go/Seq for gobind JNI runtime
-keep class go.Seq { *; }
-keepclassmembers class go.Seq { *; }

# Keep LeafService and LeafJni
-keep class com.leaf.LeafService { *; }
-keep class com.leaf.LeafJni { *; }
