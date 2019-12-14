package net.activelook.sdk.util

import net.activelook.sdk.screen.Screen

/**
 * Coordinates of a point
 */
data class Point(val x: Int, val y: Int) {

    fun offset(padX: Int, padY: Int) : Point {
        return Point(x + padX, y + padY)
    }

    fun invert() : Point {
        return Point(Screen.WIDTH - x, Screen.HEIGHT - y)
    }
}