package net.activelook.sdk.screen

/**
 * Text orientation
 */
enum class Orientation(internal val value: Int) {
    /**
     * Left to right, with a rotation of 0 degree
     */
    R0(0),
    /**
     * Right to left, with a rotation of 0 degree
     */
    R1(1),
    /**
     * Left to right, with a rotation of 90 degrees
     */
    R2(2),
    /**
     * Right to left, with a rotation of 90 degrees
     */
    R3(3),
    /**
     * Left to right, with a rotation of 180 degrees
     */
    R4(4),
    /**
     * Right to left, with a rotation of 180 degrees
     */
    R5(5),
    /**
     * Left to right, with a rotation of 270 degrees
     */
    R6(6),
    /**
     * Right to left, with a rotation of 270 degrees
     */
    R7(7)
}