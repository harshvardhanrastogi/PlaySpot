package com.harsh.playspot.data.imagekit

import com.harsh.playspot.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Response from ImageKit upload API
 */
@Serializable
data class ImageKitUploadResponse(
    val fileId: String = "",
    val name: String = "",
    val url: String = "",
    val thumbnailUrl: String = "",
    val filePath: String = "",
    val fileType: String = "",
    val size: Long = 0,
    val height: Int = 0,
    val width: Int = 0
)

/**
 * Error response from ImageKit
 */
@Serializable
data class ImageKitError(
    val message: String = "",
    val help: String = ""
)

/**
 * Repository for handling image uploads and transformations via ImageKit.io
 * 
 * To use this repository:
 * 1. Sign up at https://imagekit.io
 * 2. Get your Public Key, Private Key, and URL Endpoint from the dashboard
 * 3. Add them to local.properties:
 *    - IMAGEKIT_PUBLIC_KEY=your_public_key
 *    - IMAGEKIT_PRIVATE_KEY=your_private_key
 *    - IMAGEKIT_URL_ENDPOINT=https://ik.imagekit.io/your_id
 *    
 * ⚠️ SECURITY NOTE: For production apps, the private key should NOT be in the client.
 * Instead, use a backend server to generate authentication signatures.
 */
class ImageKitRepository {
    
    companion object {
        private const val UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload"
        
        // Singleton instance

        private val INSTANCE: ImageKitRepository  by lazy {
            ImageKitRepository()
        }

        fun getInstance(): ImageKitRepository {
            return INSTANCE
        }

    }
    
    private val urlEndpoint: String get() = BuildConfig.IMAGEKIT_URL_ENDPOINT
    private val publicKey: String get() = BuildConfig.IMAGEKIT_PUBLIC_KEY
    private val privateKey: String get() = BuildConfig.IMAGEKIT_PRIVATE_KEY
    
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }
    
    /**
     * Upload an image to ImageKit
     * 
     * @param imageBytes The image data as ByteArray
     * @param fileName The name to save the file as (e.g., "profile_123.jpg")
     * @param folder Optional folder path in ImageKit (e.g., "/profiles", "/events")
     * @param tags Optional list of tags for organization
     * @return Result containing the upload response or error
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        folder: String = "/playspot",
        tags: List<String> = emptyList()
    ): Result<ImageKitUploadResponse> {
        return try {
            // Create Basic auth header: base64(privateKey:)
            val authHeader = Base64.encode("$privateKey:".encodeToByteArray())
            
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = UPLOAD_URL,
                formData = formData {
                    append("file", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                    append("fileName", fileName)
                    append("folder", folder)
                    append("publicKey", publicKey)
                    if (tags.isNotEmpty()) {
                        append("tags", tags.joinToString(","))
                    }
                    // Use unique filename to avoid overwriting
                    append("useUniqueFileName", "true")
                }
            ) {
                header(HttpHeaders.Authorization, "Basic $authHeader")
            }
            
            if (response.status.isSuccess()) {
                val uploadResponse = response.body<ImageKitUploadResponse>()
                Result.success(uploadResponse)
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Upload failed (${response.status}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Upload a profile picture
     * 
     * @param imageBytes The image data
     * @param userId The user's ID for unique naming
     * @return Result containing the upload response
     */
    suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        userId: String
    ): Result<ImageKitUploadResponse> {
        return uploadImage(
            imageBytes = imageBytes,
            fileName = "profile_$userId.jpg",
            folder = "/profiles",
            tags = listOf("profile", "user_$userId")
        )
    }
    
    /**
     * Upload an event cover image
     * 
     * @param imageBytes The image data
     * @param eventId The event's ID for unique naming
     * @return Result containing the upload response
     */
    suspend fun uploadEventCover(
        imageBytes: ByteArray,
        eventId: String
    ): Result<ImageKitUploadResponse> {
        return uploadImage(
            imageBytes = imageBytes,
            fileName = "event_$eventId.jpg",
            folder = "/events",
            tags = listOf("event", "cover")
        )
    }
    
    /**
     * Get an optimized image URL with transformations
     * 
     * ImageKit URL transformations:
     * - w-{width}: Set width
     * - h-{height}: Set height
     * - q-{1-100}: Set quality
     * - f-auto: Auto format (WebP for supported browsers)
     * - fo-auto: Auto focus for smart cropping
     * - c-maintain_ratio: Maintain aspect ratio
     * 
     * @param imagePath The path or full URL of the image
     * @param width Desired width (null for auto)
     * @param height Desired height (null for auto)
     * @param quality Image quality 1-100 (default 80)
     * @param autoFormat Use auto format selection (WebP when supported)
     * @return Optimized image URL
     */
    fun getOptimizedUrl(
        imagePath: String,
        width: Int? = null,
        height: Int? = null,
        quality: Int = 80,
        autoFormat: Boolean = true
    ): String {
        // If it's already a full URL, extract the path
        val path = if (imagePath.startsWith("http")) {
            imagePath.substringAfter(urlEndpoint).removePrefix("/")
        } else {
            imagePath.removePrefix("/")
        }
        
        val transformations = buildList {
            width?.let { add("w-$it") }
            height?.let { add("h-$it") }
            add("q-$quality")
            if (autoFormat) add("f-auto")
        }.joinToString(",")
        
        return "$urlEndpoint/tr:$transformations/$path"
    }
    
    /**
     * Get a thumbnail URL with smart cropping
     * 
     * @param imagePath The path or full URL of the image
     * @param size Thumbnail size (square)
     * @return Thumbnail URL
     */
    fun getThumbnailUrl(imagePath: String, size: Int = 100): String {
        val path = if (imagePath.startsWith("http")) {
            imagePath.substringAfter(urlEndpoint).removePrefix("/")
        } else {
            imagePath.removePrefix("/")
        }
        
        return "$urlEndpoint/tr:w-$size,h-$size,fo-auto,c-maintain_ratio/$path"
    }
    
    /**
     * Get an explore card cover image URL optimized for 112dp display
     * Uses 336px (3x density) for crisp display on most devices
     * 
     * @param imagePath The path or full URL of the image
     * @return Optimized square cover URL for explore cards
     */
    fun getExploreCoverUrl(imagePath: String): String {
        if (imagePath.isBlank()) return ""
        
        val path = if (imagePath.startsWith("http")) {
            imagePath.substringAfter(urlEndpoint).removePrefix("/")
        } else {
            imagePath.removePrefix("/")
        }
        
        // 112dp * 3x density = 336px for crisp display
        return "$urlEndpoint/tr:w-336,h-336,fo-auto,c-maintain_ratio,q-80,f-auto/$path"
    }
    
    /**
     * Get a profile picture URL optimized for display
     * 
     * @param imagePath The path or full URL of the image
     * @param size Desired size (default 200px)
     * @return Optimized profile picture URL
     */
    fun getProfilePictureUrl(imagePath: String, size: Int = 200): String {
        val path = if (imagePath.startsWith("http")) {
            imagePath.substringAfter(urlEndpoint).removePrefix("/")
        } else {
            imagePath.removePrefix("/")
        }
        
        return "$urlEndpoint/tr:w-$size,h-$size,fo-face,c-maintain_ratio,q-80,f-auto/$path"
    }
    
    /**
     * Get an event cover URL optimized for display
     * 
     * @param imagePath The path or full URL of the image
     * @param width Desired width
     * @param height Desired height
     * @return Optimized event cover URL
     */
    fun getEventCoverUrl(imagePath: String, width: Int = 400, height: Int = 200): String {
        val path = if (imagePath.startsWith("http")) {
            imagePath.substringAfter(urlEndpoint).removePrefix("/")
        } else {
            imagePath.removePrefix("/")
        }
        
        return "$urlEndpoint/tr:w-$width,h-$height,fo-auto,c-maintain_ratio,q-85,f-auto/$path"
    }
    
    /**
     * Check if ImageKit is properly configured
     */
    fun isConfigured(): Boolean {
        return publicKey.isNotBlank() && 
               publicKey != "your_public_key_here" &&
               privateKey.isNotBlank() && 
               privateKey != "your_private_key_here" &&
               urlEndpoint.isNotBlank() &&
               !urlEndpoint.contains("your_imagekit_id")
    }
}
