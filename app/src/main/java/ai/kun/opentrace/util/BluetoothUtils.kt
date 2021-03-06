package ai.kun.opentrace.util

import ai.kun.opentrace.util.Constants.CHARACTERISTIC_DEVICE_STRING
import ai.kun.opentrace.util.Constants.CHARACTERISTIC_USER_STRING
import ai.kun.opentrace.util.Constants.CLIENT_CONFIGURATION_DESCRIPTOR_SHORT_ID
import ai.kun.opentrace.util.Constants.SERVICE_STRING
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import androidx.annotation.Nullable

import java.util.*

object BluetoothUtils {

    fun findCharacteristic(
        bluetoothGatt: BluetoothGatt,
        uuidString: String
    ): BluetoothGattCharacteristic? {
        val serviceList = bluetoothGatt.services
        val service = findService(serviceList) ?: return null
        val characteristicList =
            service.characteristics
        for (characteristic in characteristicList) {
            if (characteristicMatches(characteristic, uuidString)) {
                return characteristic
            }
        }
        return null
    }

    fun isDeviceCharacteristic(characteristic: BluetoothGattCharacteristic?): Boolean {
        return characteristicMatches(characteristic, CHARACTERISTIC_DEVICE_STRING)
    }

    fun isUserCharacteristic(characteristic: BluetoothGattCharacteristic?): Boolean {
        return characteristicMatches(characteristic, CHARACTERISTIC_USER_STRING)
    }

    private fun characteristicMatches(
        characteristic: BluetoothGattCharacteristic?,
        uuidString: String
    ): Boolean {
        if (characteristic == null) {
            return false
        }
        val uuid = characteristic.uuid
        return uuidMatches(uuid.toString(), uuidString)
    }

    private fun isMatchingCharacteristic(characteristic: BluetoothGattCharacteristic?): Boolean {
        if (characteristic == null) {
            return false
        }
        val uuid = characteristic.uuid
        return matchesCharacteristicUuidString(uuid.toString())
    }

    private fun matchesCharacteristicUuidString(characteristicIdString: String): Boolean {
        return uuidMatches(
            characteristicIdString,
            CHARACTERISTIC_DEVICE_STRING,
            CHARACTERISTIC_USER_STRING
        )
    }

    fun requiresResponse(characteristic: BluetoothGattCharacteristic): Boolean {
        return (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                != BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)
    }

    fun requiresConfirmation(characteristic: BluetoothGattCharacteristic): Boolean {
        return (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE
                == BluetoothGattCharacteristic.PROPERTY_INDICATE)
    }

    // Descriptor
    fun findClientConfigurationDescriptor(descriptorList: List<BluetoothGattDescriptor>): BluetoothGattDescriptor? {
        for (descriptor in descriptorList) {
            if (isClientConfigurationDescriptor(descriptor)) {
                return descriptor
            }
        }
        return null
    }

    private fun isClientConfigurationDescriptor(descriptor: BluetoothGattDescriptor?): Boolean {
        if (descriptor == null) {
            return false
        }
        val uuid = descriptor.uuid
        val uuidSubstring = uuid.toString().substring(4, 8)
        return uuidMatches(uuidSubstring, CLIENT_CONFIGURATION_DESCRIPTOR_SHORT_ID)
    }

    // Service
    private fun matchesServiceUuidString(serviceIdString: String): Boolean {
        return uuidMatches(serviceIdString, SERVICE_STRING)
    }

    fun findService(serviceList: List<BluetoothGattService>): BluetoothGattService? {
        for (service in serviceList) {
            val serviceIdString = service.uuid
                .toString()
            if (matchesServiceUuidString(serviceIdString)) {
                return service
            }
        }
        return null
    }

    // String matching
    // If manually filtering, substring to match:
    // 0000XXXX-0000-0000-0000-000000000000
    private fun uuidMatches(
        uuidString: String,
        vararg matches: String
    ): Boolean {
        for (match in matches) {
            if (uuidString.equals(match, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}