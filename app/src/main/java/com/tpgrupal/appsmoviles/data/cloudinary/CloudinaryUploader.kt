package com.tpgrupal.appsmoviles.data.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CloudinaryUploader {

    private var initialized = false

    fun init(context: Context) {

        if (initialized) return

        MediaManager.init(
            context,
            mapOf(
                "cloud_name" to "dwrtanizo"
            )
        )

        initialized = true
    }

    suspend fun uploadImage(
        uri: Uri
    ): String {

        return suspendCancellableCoroutine { continuation ->

            MediaManager.get()
                .upload(uri)
                .unsigned("torneos_upload")
                .callback(object :
                    com.cloudinary.android.callback.UploadCallback {

                    override fun onStart(requestId: String?) {}

                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long
                    ) {}

                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?
                    ) {

                        val url =
                            resultData?.get("secure_url")
                                ?.toString()

                        if (url != null) {
                            continuation.resume(url)
                        } else {
                            continuation.resumeWithException(
                                Exception("No se obtuvo URL")
                            )
                        }
                    }

                    override fun onError(
                        requestId: String?,
                        error: com.cloudinary.android.callback.ErrorInfo?
                    ) {

                        continuation.resumeWithException(
                            Exception(error?.description)
                        )
                    }

                    override fun onReschedule(
                        requestId: String?,
                        error: com.cloudinary.android.callback.ErrorInfo?
                    ) {
                    }
                })
                .dispatch()
        }
    }
}