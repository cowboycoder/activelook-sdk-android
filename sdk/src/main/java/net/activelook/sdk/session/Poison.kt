package net.activelook.sdk.session

import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.command.Enqueueable
import net.activelook.sdk.operation.ActiveLookOperation

/**
 * A cache poison object used to signal the end of an operation
 */
internal data class OperationPoison(val operation: ActiveLookOperation): Enqueueable

/**
 * A cache poison object used to signal the end of a command
 */
internal data class CommandPoison(val command: ActiveLookCommand): Enqueueable