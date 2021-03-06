package ai.kun.opentrace.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_table")
data class Device(
    @PrimaryKey
    @ColumnInfo(name = "device_uuid")
    val deviceUuid: String,

    @ColumnInfo(name = "distance")
    val distance: Float?,

    @ColumnInfo(name = "rssi")
    val rssi: Int,

    @ColumnInfo(name = "tx_power")
    val txPower: Int,

    @ColumnInfo(name = "time_stamp_nanos")
    val timeStampNanos: Long,

    @ColumnInfo(name = "time_stamp")
    val timeStam: Long,

    @ColumnInfo(name = "session_id")
    val sessionId: String
)