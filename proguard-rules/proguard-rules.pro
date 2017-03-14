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
