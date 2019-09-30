package net.activelook.sdk.example

interface Operation {
    val name: String
}

data class OperationClick(override val name: String, val onPlay: () -> Unit) : Operation

data class OperationSwitch(
    override val name: String,
    val defaultValue: Boolean,
    val onPlay: (isChecked: Boolean) -> Unit
) : Operation

data class OperationSliderAndSwitch(
    override val name: String,
    val defaultIsChecked: Boolean,
    val defaultProgress: Int,
    val min: Int,
    val max: Int,
    val onPlay: (progress: Int, isChecked: Boolean) -> Unit
) : Operation