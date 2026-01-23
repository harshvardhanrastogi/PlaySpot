package com.harsh.playspot.dao

import kotlinx.serialization.Serializable

/**
 * Data class representing a participant in an event
 */
@Serializable
data class Participant(
    val id: String = "",
    val name: String = "",
    val profileUrl: String = ""
)

/**
 * Data class representing a sports event/match stored in Firestore
 */
@Serializable
data class Event(
    val id: String = "",
    val matchName: String = "",
    val sportType: String = "",
    val date: String = "",
    val time: String = "",
    val playerLimit: Int = 0,
    val currentPlayers: Int = 1, // Creator is first player
    val description: String = "",
    val skillLevel: String = "",
    
    // Venue details
    val venue: Venue = Venue(),
    
    // Creator info
    val creatorId: String = "",
    val creatorName: String = "",
    val creatorProfileUrl: String = "",
    
    // Participants (list of participant objects with id, name, and profileUrl)
    val participants: List<Participant> = emptyList(),
    
    // Status
    val status: String = EventStatus.UPCOMING,
    
    // Timestamps
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    
    // Cover image URL (if uploaded to storage)
    val coverImageUrl: String = ""
)

/**
 * Venue details for the event
 */
@Serializable
data class Venue(
    val name: String = "",
    val address: String = "",
    val meetingPoint: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val placeId: String = ""
)

/**
 * Event status constants
 */
object EventStatus {
    const val DRAFT = "draft"
    const val UPCOMING = "upcoming"
    const val ONGOING = "ongoing"
    const val COMPLETED = "completed"
    const val CANCELLED = "cancelled"
}
