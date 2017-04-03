# Rules applied to obsufcate aplication that is using Accept SDK

#  apache
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**

-keep class org.apache.http.** { *; }

#  Jackson
-keepattributes *Annotation*,EnclosingMethod

-keep class com.fasterxml.jackson.** { *; }
-keepnames class org.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn com.fasterxml.jackson.dataformat.**


-dontwarn javax.xml.**
-dontwarn javax.xml.stream.events.**


# usb interface
# maybe it will be missing
-dontwarn com.felhr.usbserial.**
-keep class com.felhr.usbserial.* { *;}
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbCTSCallback
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbDSRCallback
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbReadCallback

#android
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
