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
