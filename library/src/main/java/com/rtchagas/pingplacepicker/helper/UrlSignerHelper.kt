package com.rtchagas.pingplacepicker.helper

import android.util.Base64
import android.util.Log

import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object UrlSignerHelper {

    const val TAG = "UrlSignerHelper"

    fun signUrl(inputUrl: String, inputKey: String): String {

        // Convert the string to a URL so we can parse it
        var url: URL

        try {
            url = URL(inputUrl)
        }
        catch (e: MalformedURLException) {
            Log.e(TAG, "Could not parse the input URL")
            return inputUrl
        }

        // Encode the URL
        val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
        url = uri.toURL()

        // Convert the key from 'web safe' base 64 to binary
        val byteKey: ByteArray = Base64.decode(inputKey, Base64.URL_SAFE)

        return try {
            val signature = signRequest(url.path, url.query, byteKey)
            "$inputUrl&signature=$signature"
        }
        catch (ex: Exception) {
            Log.e(TAG, "Could not sign the URL. ${ex.message}")
            inputUrl
        }
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun signRequest(path: String, query: String, key: ByteArray): String {

        // Retrieve the proper URL components to sign
        val resource = "$path?$query"

        // Get an HMAC-SHA1 signing key from the raw key bytes
        val sha1Key = SecretKeySpec(key, "HmacSHA1")

        // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(sha1Key)

        // compute the binary signature for the request
        val sigBytes = mac.doFinal(resource.toByteArray())

        // base 64 encode the binary signature
        return Base64.encodeToString(sigBytes, Base64.URL_SAFE or Base64.NO_WRAP)
    }
}