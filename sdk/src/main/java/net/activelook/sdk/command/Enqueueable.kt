package net.activelook.sdk.command

internal interface EnqueueableCommand

internal object NotificationCommand: EnqueueableCommand

/**
 * A cache poison object used to signal to a cache processor to stop
 */
internal object CachePoison: EnqueueableCommand