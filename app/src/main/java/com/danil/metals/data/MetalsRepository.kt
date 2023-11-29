package com.danil.metals.data

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

interface MetalsRepository {
    fun context(): Context
    fun auth(): FirebaseAuth
    fun requestToSignIn(email: String, password: String): Task<AuthResult>
    fun requestToSignUp(email: String, password: String): Task<AuthResult>
    fun requestToGetDocument(collection: String, document: String): Task<DocumentSnapshot>
    fun requestToGetDocRef(collection: String, document: String): DocumentReference
    fun requestToGetCollRef(collection: String): CollectionReference
    fun requestToCreateDocument(
        collection: String,
        document: String?,
        data: HashMap<String, Any>
    ): Task<out Any>

    fun requestToDeleteDocument(collection: String, document: String)
}

class NetworkMetalsRepository(private val context: Context) : MetalsRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    override fun auth(): FirebaseAuth {
        return auth
    }

    override fun context(): Context {
        return context
    }

    override fun requestToSignIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun requestToCreateDocument(
        collection: String,
        document: String?,
        data: HashMap<String, Any>
    ): Task<out Any> {
        return if (document != null) firestore.collection(collection).document(document)
            .set(data) else firestore.collection(collection).add(data)
    }

    override fun requestToDeleteDocument(collection: String, document: String) {
        firestore.collection(collection).document(document).delete()
    }

    override fun requestToSignUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override fun requestToGetDocument(
        collection: String,
        document: String
    ): Task<DocumentSnapshot> {
        return firestore.collection(collection).document(document).get()
    }

    override fun requestToGetDocRef(collection: String, document: String): DocumentReference {
        return firestore.collection(collection).document(document)
    }

    override fun requestToGetCollRef(collection: String): CollectionReference {
        return firestore.collection(collection)
    }
}