package com.harsh.playspot.dao

import kotlinx.serialization.Serializable

/**
 * Data class representing a user's participation in an event.
 * Stored in the user_events collection for efficient querying of user's attending events.
 * 
 * Document ID format: "{userId}_{eventId}" for easy lookup and uniqueness
 */
@Serializable
data class UserEvent(
    val id: String = "",           // Document ID: "{userId}_{eventId}"
    val userId: String = "",       // User who is attending
    val eventId: String = "",      // Event being attended
    
    // Denormalized event info for quick display without fetching full event
    val eventName: String = "",
    val sportType: String = "",
    val date: String = "",
    val time: String = "",
    val eventStartTimeStamp: Long = 0L,
    val venueName: String = "",
    val coverImageUrl: String = "",
    
    // User's relationship to the event
    val isCreator: Boolean = false,
    
    // Timestamps
    val joinedAt: Long = 0,
    
    // User-specific settings (for future use)
    val notificationsEnabled: Boolean = true
)
/**
 * Helper function to generate consistent document ID
 */
fun generateUserEventId(userId: String, eventId: String): String {
    return "${userId}_${eventId}"
}
