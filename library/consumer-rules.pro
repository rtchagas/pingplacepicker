# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Places SDK 5.x reflectively constructs its generated proto classes (every
# subclass of the internal `zzbdq` base) inside Places.createClient. The
# SDK's own shipped proguard.txt keeps proto FIELDS but not constructors, so
# R8 strips `<init>` and the consumer's release build crashes with
# NoSuchMethodException. Keep the constructors too.
-keepclassmembers class * extends com.google.android.libraries.places.internal.zzbdq {
    <init>(...);
}
