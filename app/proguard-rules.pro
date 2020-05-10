# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep,allowshrinking public final @interface *

-keep class ua.naiksoftware.**
-dontwarn ua.naiksoftware.**


# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
#-keep class okhht3.**
#-dontwarn okhhtp.**
-ignorewarnings
# keep - Serialization code. Keep all fields and methods that are used for

# serialization.

-keepclassmembers class * extends java.io.Serializable {

static final long serialVersionUID;

static final java.io.ObjectStreamField[] serialPersistentFields;

private void writeObject(java.io.ObjectOutputStream);

private void readObject(java.io.ObjectInputStream);

java.lang.Object writeReplace();

java.lang.Object readResolve();

}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod

 # We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# enumeration classes.

-keepclassmembers enum * {

public static **[] values();

public static ** valueOf(java.lang.String);

}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class com.plustxt.sdk.** { *; }
-keep class com.roomorama.caldroid { *; }
-keep class com.antonyt.infiniteviewpager { *; }
-keep class uk.co.senab.actionbarpulltorefresh.library.** { *; }
-keep class fr.castorflex.android.smoothprogressbar.** { *; }
-keep class android.support.v7.appcompat.** { *; }
-keep interface android.support.v7.appcompat.** { *; }
-keep class uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.** { *; }
-keep class com.nostra13.universalimageloader.** { *; }
-keepclassmembers enum * { *; }

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }
-keep class com.squareup.okhttp.internal.http.HttpsURLConnectionImpl { *; }
-keep class com.mobileapptracker.** { *; }
-keep class com.android.volley.** { *; }
-keep class com.android.volley.toolbox.** { *; }
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.nineoldandroids.** { *; }

-keep class com.google.android.gms.tagmanager.** { *; }
-keep class com.google.android.gms.analytics.**
-keep class com.google.analytics.tracking.**
-dontwarn com.google.android.gms.tagmanager.**
-dontwarn com.google.android.gms.analytics.**
-dontwarn com.google.analytics.tracking.**
-dontwarn android.support.**
-dontwarn com.google.ads.**
-dontwarn com.crittercism.**
-dontwarn com.google.android.apps.analytics
-dontwarn com.google.android.gms.**
-dontwarn com.google.android.gms.common.GooglePlayServicesUtil
-dontwarn com.mobileapptracker.**
-dontwarn com.plustxt.sdk.**
-dontwarn hirondelle.date4j.**
-dontwarn com.squareup.okhttp.internal.http.HttpsURLConnectionImpl
-dontwarn com.nineoldandroids.**
-dontwarn com.urbanairship.**
-keepattributes SourceFile
-keepattributes LineNumberTable
-dontwarn io.realm.**

-keep class com.mobileapptracker.** { *; }

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

-keep class android.support.**
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class net.one97.paytm.common.entity.** { *; }


-dontwarn org.jivesoftware.**

-keepclasseswithmembers class de.measite.smack.** {
    *;
}

-keepclasseswithmembers class * extends org.jivesoftware.smack.sasl.SASLMechanism {
    public <init>(org.jivesoftware.smack.SASLAuthentication);
}

-keep class com.nineoldandroids { *; }


-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.facebook.** { *; }

-dontwarn com.google.api.client.repackaged.**
-dontwarn it.sephiroth.android.library.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn com.google.zxing.client.android.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep public class com.tm.sdk.** { *; }
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient


-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

## greendao event bus
-keepclassmembers class ** {
    public void onEvent*(***);
}
-dontwarn okio.**


-adaptclassstrings
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,EnclosingMethod


-keep class com.google.android.gms.**

-renamesourcefileattribute SourceFile
  -keepattributes SourceFile,LineNumberTable
  -keep class org.apache.http.** { *; }
  -dontwarn org.apache.http.**
  -dontwarn android.net.**

-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
