# Get rid of package names, makes file smaller
-repackageclasses
-keepattributes Signature

# --> Data classes
-keep class com.where2meet.core.domain.model.** { *; }
-keep class com.where2meet.core.data.remote.json.** { *; }
-keep class com.where2meet.ui.parcelable.** { *; }

# --> ViewBinding
# ViewBindingDelegate uses Reflection.
-keepclassmembers class ** implements androidx.viewbinding.ViewBinding {
    public static ** bind(android.view.View);
}

# --> Hilt
# Keep class names of Hilt injected ViewModels since their name are used as a multibinding map key.
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# --> Retrofit
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
