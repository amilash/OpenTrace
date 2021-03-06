package ai.kun.opentrace.util

import android.util.Log
import androidx.annotation.Nullable
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.experimental.and

object ByteUtils {

    private const val TAG = "ByteUtils"

    fun reverse(value: ByteArray): ByteArray {
        val length = value.size
        val reversed = ByteArray(length)
        for (i in 0 until length) {
            reversed[i] = value[length - (i + 1)]
        }
        return reversed
    }

   private val hexArray = "0123456789ABCDEF".toCharArray()

    fun byteArrayInHexFormat(bytes: ByteArray?): String? {
        if (bytes == null) return null

        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = (bytes[j] and 0xFF.toByte()).toInt()

            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun bytesFromString(string: String): ByteArray {
        var stringBytes = ByteArray(0)
        try {
            stringBytes = string.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Failed to convert message string to byte array")
        }
        return stringBytes
    }

    @Nullable
    fun stringFromBytes(bytes: ByteArray): String {
        return try {
            String(bytes, Charset.forName("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Unable to convert message bytes to string")
            ""
        }
    }
}