# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\jakub.misko\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizations !class/unboxing/enum

# butterknife
-keep @interface butterknife.*

-keepclasseswithmembers class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembers class * {
    @butterknife.* <methods>;
}

-keepclasseswithmembers class * {
    @butterknife.On* <methods>;
}

-keep class **$$ViewInjector {
    public static void inject(...);
    public static void reset(...);
}

-keep class **$$ViewBinder {
    public static void bind(...);
    public static void unbind(...);
}

#  apache
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**

-keep class org.apache.http.** { *; }

#  Jackson
-keepattributes *Annotation*,EnclosingMethod

-keepnames class org.codehaus.jackson.** { *; }

-dontwarn javax.xml.**
-dontwarn javax.xml.stream.events.**
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn com.fasterxml.jackson.dataformat.**

-keep class com.fasterxml.jackson.** { *; }

# logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# usb interface
# maybe it will be missing
-dontwarn com.felhr.usbserial.**
-keep class com.felhr.usbserial.* { *;}
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbCTSCallback
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbDSRCallback
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbReadCallback

# java 8 lambdas
-dontwarn java.lang.invoke.*

# picaso
# ok http client
#-keep class com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.* { *;}
-dontwarn okio.
#-dontwarn okio.**
