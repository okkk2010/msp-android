# Retrofit service interfaces and Gson DTOs are used through reflection.
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations
-keep class com.mspoverlay.android.core.network.dto.** { *; }
-keep class com.mspoverlay.android.feature.discover.data.** { *; }
-keep class com.mspoverlay.android.feature.library.data.** { *; }
-keep class com.mspoverlay.android.overlay.model.** { *; }
