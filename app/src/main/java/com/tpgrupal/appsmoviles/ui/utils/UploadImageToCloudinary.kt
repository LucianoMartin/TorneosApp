package com.tpgrupal.appsmoviles.ui.utils

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

fun uploadImageToCloudinary(
    imageBytes: ByteArray,
    onSuccess: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    val client = OkHttpClient()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "file",
            "profile.jpg",
            imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
        )
        .addFormDataPart("upload_preset", "Torneos")
        .build()

    val request = Request.Builder()
        .url("https://api.cloudinary.com/v1_1/tp-apps-moviles/image/upload")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e)
        }

        override fun onResponse(call: Call, response: Response) {
            try {
                val body = response.body?.string()
                val json = JSONObject(body ?: "")
                val url = json.getString("secure_url")
                onSuccess(url)
            } catch (e: Exception) {
                onError(e)
            }
        }
    })
}