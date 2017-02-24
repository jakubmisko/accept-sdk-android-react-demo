# Rules applied to obsufcate aplication that is using Accept SDK

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

# usb interface
# maybe it will be missing
-dontwarn com.felhr.usbserial.**
-keep class com.felhr.usbserial.* { *;}
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbCTSCallback
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbDSRCallback
#-keep public interface com.felhr.usbserial.UsbSerialInterface$UsbReadCallback
