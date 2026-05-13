# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep key store related classes
-keep class com.example.testingappandroid2.keystore.** { *; }
-keep class com.example.testingappandroid2.services.** { *; }
