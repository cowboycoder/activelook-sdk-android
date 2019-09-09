package net.activelook.sdk.notification

import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service

internal sealed class ActiveLookNotification(val service: Service, val characteristic: Characteristic) {

    object BatteryLevel: ActiveLookNotification(Service.Battery, Characteristic.BatteryLevel)

    object TxServer: ActiveLookNotification(Service.CommandInterface, Characteristic.TxServer)
}