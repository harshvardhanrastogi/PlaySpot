package com.harsh.playspot.data.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for handling Firebase Authentication operations.
 * Uses KMPAuth's underlying GitLive Firebase SDK for email/password auth.
 */
class AuthRepository private constructor() {
    private val auth = Firebase.auth

    /**
     * Get the current authenticated user
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Flow that emits the current authentication state
     */
    val isAuthenticated: Flow<Boolean>
        get() = auth.authStateChanged.map { it != null }

    /**
     * Flow that emits the current user
     */
    val authStateFlow: Flow<FirebaseUser?>
        get() = auth.authStateChanged

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign in failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a new user with email and password
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign up failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out the current user
     */
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update the current user's display name.
     * This updates the Firebase Auth profile, not Firestore.
     */
    suspend fun updateDisplayName(displayName: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            user.updateProfile(displayName = displayName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update the current user's photo URL.
     * This updates the Firebase Auth profile, not Firestore.
     */
    suspend fun updatePhotoUrl(photoUrl: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            user.updateProfile(photoUrl = photoUrl)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update both display name and photo URL at once.
     * This updates the Firebase Auth profile, not Firestore.
     */
    suspend fun updateProfile(displayName: String? = null, photoUrl: String? = null): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            user.updateProfile(displayName = displayName, photoUrl = photoUrl)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private val INSTANCE: AuthRepository by lazy { AuthRepository() }

        fun getInstance(): AuthRepository = INSTANCE
    }
}
