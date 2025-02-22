# Keep the DraggableDefaults.Thumb class and its members
-keep class com.nphausg.foundation.ui.draggle.DraggableDefaults$Thumb { *; }

# Keep the DraggableUnlockerKt class and its members
-keep class com.nphausg.foundation.ui.draggle.DraggableUnlockerKt { *; }

# If you have any enums or inner classes within DraggableDefaults, you might need to keep them too
-keepclassmembers class com.nphausg.foundation.ui.draggle.DraggableDefaults {
    *;
}