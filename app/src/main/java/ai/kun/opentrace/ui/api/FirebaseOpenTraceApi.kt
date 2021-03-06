package ai.kun.opentrace.ui.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class FirebaseOpenTraceApi : OpenTraceApi {
    companion object {
        const val FUNCTION_TRACK_VISIT = "visitIdentity"
        const val FUNCTION_SUBMIT_SYMPTOMS = "submitSymptoms"
        const val FUNCTION_SET_DEVICE_UUID = "setDeviceUuid"
        const val FUNCTION_SUBMIT_TRACE = "submitTrace"
    }

    private var functions = Firebase.functions

    val identity: String
    get() {
        // TODO: we should pass currentUser while creating instance of `FirebaseOpenTraceApi` so we won't have to worry about case when it's nil
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return "Anonymous"
        return currentUser.uid
    }

    override fun trackVisit(otherIdentity: String): LiveData<Boolean> {
        val data = hashMapOf(
            "firstIdentity" to identity,
            "secondIdentity" to otherIdentity
        )

        val liveData = MutableLiveData<Boolean>()

        functions.getHttpsCallable(FUNCTION_TRACK_VISIT)
            .call(data)
            .addOnCompleteListener { result ->
                liveData.value = result.isSuccessful
            }

        return liveData
    }

    override fun submitSymptoms(symptoms: Set<String>): ResultLiveData<Int> {
        val liveData = ResultLiveData<Int>()
        liveData.postLoading()

        FirebaseAuth.getInstance().currentUser.let {
            val data = hashMapOf(
                "symptoms" to symptoms.toList(),
                "identity" to identity
            )

            functions.getHttpsCallable(FUNCTION_SUBMIT_SYMPTOMS)
                .call(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val scoreResult = task.result?.data as Map<*, *>
                        if (scoreResult["score"] != null) {
                            liveData.postSuccess(scoreResult["score"] as Int)
                        } else {
                            liveData.postError(Exception("No score returned."))
                        }

                    } else {
                        liveData.postError(task.exception ?: Exception())
                    }
                }

            return liveData
        }
    }

    override fun setDeviceUuid(deviceUuid: String) {
        FirebaseAuth.getInstance().currentUser.let {
            val data = hashMapOf(
                "deviceUuid" to deviceUuid,
                "identity" to identity
            )

            functions.getHttpsCallable(FUNCTION_SET_DEVICE_UUID)
                .call(data)
        }
    }

    override fun submitTrace(
        deviceUuid: String,
        distance: Float?,
        rssi: Int,
        txPower: Int,
        timeStampNanos: Long,
        sessionId: String
    ): ResultLiveData<Int> {
        val liveData = ResultLiveData<Int>()
        liveData.postLoading()

        FirebaseAuth.getInstance().currentUser.let {
            val data = hashMapOf(
                "deviceUuid" to deviceUuid,
                "distance" to distance,
                "rrsi" to rssi,
                "txPower" to txPower,
                "timeStampNanos" to timeStampNanos,
                "sessionId" to sessionId,
                "identity" to identity
            )

            functions.getHttpsCallable(FUNCTION_SUBMIT_TRACE)
                .call(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val scoreResult = task.result?.data as Map<*, *>
                        if (scoreResult["score"] != null) {
                            liveData.postSuccess(scoreResult["score"] as Int)
                        } else {
                            liveData.postError(Exception("No score returned."))
                        }

                    } else {
                        liveData.postError(task.exception ?: Exception())
                    }
                }

            return liveData
        }
    }
}