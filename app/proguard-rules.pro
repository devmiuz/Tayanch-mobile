# Pillar 7 (AppSec): R8 hardening. Keep kotlinx.serialization metadata so the
# obfuscated release build can still (de)serialize our DTOs.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class uz.tayanch.app.data.dto.** {
    *** Companion;
}
-keepclasseswithmembers class uz.tayanch.app.data.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor pulls in optional engines via reflection; silence the warnings.
-dontwarn io.ktor.**
-dontwarn org.slf4j.**
