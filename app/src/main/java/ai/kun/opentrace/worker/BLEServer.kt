package ai.kun.opentrace.worker

import ai.kun.opentrace.util.Constants
import ai.kun.opentrace.util.Constants.BACKGROUND_TRACE_INTERVAL
import ai.kun.opentrace.util.Constants.MANUFACTURE_ID
import ai.kun.opentrace.util.Constants.MANUFACTURE_SUBSTRING
import ai.kun.opentrace.worker.BLETrace.context
import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import android.os.PowerManager
import android.util.Log
import java.nio.charset.StandardCharsets
import java.util.*


class BLEServer : BroadcastReceiver(), GattServerActionListener  {
    private val TAG = "BLEServer"
    private val WAKELOCK_TAG = "ai:kun:opentrace:worker:BLEServer"
    private val INTERVAL_KEY = "interval"
    private val SERVER_REQUEST_CODE = 10


    override fun onReceive(context: Context, intent: Intent) {
        val interval = intent.getIntExtra(INTERVAL_KEY, BACKGROUND_TRACE_INTERVAL)
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
        wl.acquire(interval.toLong())
        synchronized(BLETrace) {
            // Chain the next alarm...
            enable(interval)

            GattServerCallback.serverActionListener = this
            setupServer()
            startAdvertising(BLEServerCallbackDeviceName, BLETrace.deviceNameServiceUuid)
        }
        wl.release()
    }

    fun enable(interval: Int) {
        BLETrace.alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
            ((System.currentTimeMillis() / interval) * interval) + interval,
            getPendingIntent(interval))
    }

    fun disable(interval: Int) {
        synchronized (BLETrace) {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(getPendingIntent(interval))
            stopAdvertising(BLETrace.bluetoothLeAdvertiser)
        }
    }

    private fun getPendingIntent(interval: Int) : PendingIntent {
        val intent = Intent(context, BLEServer::class.java)
        intent.putExtra(INTERVAL_KEY, interval)
        return PendingIntent.getBroadcast(context, SERVER_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    // GattServer
    private fun setupServer() {
        if (BLETrace.bluetoothGattServer.getService(BLETrace.deviceNameServiceUuid) == null) {
            val deviceService = BluetoothGattService(
                BLETrace.deviceNameServiceUuid,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )
            BLETrace.bluetoothGattServer.addService(deviceService)
        }
    }

    private fun stopServer(gattServer: BluetoothGattServer) {
        gattServer.close()
        log("server closed.")
    }

    // Advertising
    private fun startAdvertising(callback: AdvertiseCallback, uuid: UUID) {

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setConnectable(true)
            .setTimeout(Constants.BROADCAST_PERIOD.toInt())
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(true)
            .addManufacturerData(
                MANUFACTURE_ID,
                MANUFACTURE_SUBSTRING.toByteArray(StandardCharsets.UTF_8)
            )
            .addServiceUuid(ParcelUuid(uuid))
            .build()
        BLETrace.bluetoothLeAdvertiser.stopAdvertising(callback)
        BLETrace.bluetoothLeAdvertiser.startAdvertising(settings, data, callback)
        Log.d(TAG, ">>>>>>>>>>BLE Beacon Started")
    }

    private fun stopAdvertising(bluetoothLeAdvertiser: BluetoothLeAdvertiser) {
        synchronized(this) {
            bluetoothLeAdvertiser.stopAdvertising((BLEServerCallbackDeviceName))
            log("<<<<<<<<<<BLE Beacon Forced Stopped")
        }
    }

    // Gatt Server Action Listener
    override fun log(message: String) {
        Log.d(BLEServerCallbackDeviceName.TAG, message)
    }

    override fun addDevice(device: BluetoothDevice) {
        log("Deviced added: " + device.address)
    }

    override fun removeDevice(device: BluetoothDevice) {
        log("Deviced removed: " + device.address)
    }

    override fun addClientConfiguration(device: BluetoothDevice, value: ByteArray) {
        val deviceAddress = device.address
        BLEServerCallbackDeviceName.mClientConfigurations[deviceAddress] = value
    }

    override fun sendResponse(
        device: BluetoothDevice,
        requestId: Int,
        status: Int,
        offset: Int,
        value: ByteArray
    ) {
        BLETrace.bluetoothGattServer.sendResponse(device, requestId, status, offset, value)
    }
}