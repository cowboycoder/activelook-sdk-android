package net.activelook.sdk

enum class LoadingMode {
    /**
     * When the loading mode is set to normal, layouts and bitmaps are sent when you add them
     */
    NORMAL,
    /**
     * When the loading mode is set to lazy, layouts and bitmaps are sent only when you want to display them
     */
    LAZY
}