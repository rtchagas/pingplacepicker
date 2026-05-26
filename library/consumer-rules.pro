# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Places SDK 5.x reflectively instantiates its internal classes through two
# separate paths during Places.createClient:
#   1) ServiceLoader / SPI providers (e.g. gRPC name resolvers), which require
#      the public no-arg constructor on every provider implementation.
#   2) Protobuf message reflection on generated classes that extend the
#      internal zzbdq base.
# The SDK's own shipped proguard.txt only keeps zzbdq subclass FIELDS, so R8
# strips both sets of constructors and the release build crashes with
# NoSuchMethodException ("ServiceConfigurationError: Provider could not be
# instantiated" or similar). Keep every internal-class constructor.
-keepclassmembers class com.google.android.libraries.places.internal.** {
    <init>(...);
}
