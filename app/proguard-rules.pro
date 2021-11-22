# DexGuard's default settings are fine for this BPD application.

# We'll just display some more statistics about the processed code.
-verbose

##-----------Begin: dexguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

##--------End: dexguard configuration for Gson  ----------

-ignorewarnings


-printmapping mapping.txt

-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.


#Preserve the specified classes
-dontwarn okio.**

-keepattributes EnclosingMethod

# Serializable fields keep.
-keep class * implements java.io.Serializable

-keep public class * extends android.app.Activity
-keepclassmembers class * implements android.os.Parcelable {
 static ** CREATOR;
 }

-repackageclasses 'o'

# Logging is disabled, while app is running in release mode.
#Assume that the specified methods don't have any side effects, while optimizing.
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int println(int, java.lang.String, java.lang.String);
    public static java.lang.String getStackTraceString(java.lang.Throwable);
}

-assumenosideeffects class * extends java.lang.Throwable {
    public void printStackTrace();
}

-assumenosideeffects class java.lang.Thread {
    public static void dumpStack();
}

-assumenosideeffects class java.io.PrintStream {
    public void println(%);
    public void println(**);
}