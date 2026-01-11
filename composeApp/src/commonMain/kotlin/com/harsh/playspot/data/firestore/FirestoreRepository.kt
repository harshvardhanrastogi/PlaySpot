package com.harsh.playspot.data.firestore

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for Firestore operations.
 * Uses GitLive Firebase SDK for cross-platform (Android & iOS) support.
 */
class FirestoreRepository {

    val firestore: FirebaseFirestore by lazy { Firebase.firestore }

    /**
     * Get a document from a collection
     */
    suspend inline fun <reified T> getDocument(
        collection: String,
        documentId: String
    ): Result<T?> = runCatching {
        val snapshot = firestore.collection(collection).document(documentId).get()
        if (snapshot.exists) {
            snapshot.data<T>()
        } else {
            null
        }
    }

    /**
     * Get all documents from a collection
     */
    suspend inline fun <reified T> getCollection(
        collection: String
    ): Result<List<T>> = runCatching {
        firestore.collection(collection).get().documents.map { it.data<T>() }
    }

    /**
     * Add a document to a collection (auto-generated ID)
     */
    suspend inline fun <reified T : Any> addDocument(
        collection: String,
        data: T
    ): Result<String> = runCatching {
        val docRef = firestore.collection(collection).add(data)
        docRef.id
    }

    /**
     * Set a document in a collection (with specific ID)
     */
    suspend inline fun <reified T : Any> setDocument(
        collection: String,
        documentId: String,
        data: T,
        merge: Boolean = false
    ): Result<Unit> = runCatching {
        if (merge) {
            firestore.collection(collection).document(documentId).set(data, merge = true)
        } else {
            firestore.collection(collection).document(documentId).set(data)
        }
    }

    /**
     * Update specific fields in a document
     */
    suspend fun updateDocument(
        collection: String,
        documentId: String,
        updates: Map<String, Any?>
    ): Result<Unit> = runCatching {
        firestore.collection(collection).document(documentId).update(updates)
    }

    /**
     * Delete a document
     */
    suspend fun deleteDocument(
        collection: String,
        documentId: String
    ): Result<Unit> = runCatching {
        firestore.collection(collection).document(documentId).delete()
    }

    /**
     * Listen to a document for real-time updates
     */
    inline fun <reified T> observeDocument(
        collection: String,
        documentId: String
    ): Flow<T?> {
        return firestore.collection(collection).document(documentId).snapshots.map { snapshot ->
            if (snapshot.exists) {
                snapshot.data<T>()
            } else {
                null
            }
        }
    }

    /**
     * Listen to a collection for real-time updates
     */
    inline fun <reified T> observeCollection(
        collection: String
    ): Flow<List<T>> {
        return firestore.collection(collection).snapshots.map { querySnapshot ->
            querySnapshot.documents.map { it.data<T>() }
        }
    }

    /**
     * Query documents with a where clause
     */
    suspend inline fun <reified T> queryDocuments(
        collection: String,
        field: String,
        value: Any
    ): Result<List<T>> = runCatching {
        firestore.collection(collection)
            .where { field equalTo value }
            .get()
            .documents
            .map { it.data<T>() }
    }

    companion object {
        val instance: FirestoreRepository by lazy { FirestoreRepository() }
    }
}
