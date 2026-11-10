package com.fixora.marketplace.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.fixora.marketplace.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

object GoogleAuthHelper {
    suspend fun signIn(context: Context): AuthResult<GoogleSignInData> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetSignInWithGoogleOption.Builder(context.getString(R.string.default_web_client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
                val user = authResult.user
                    ?: return AuthResult.Error("Google sign-in failed. Please try again.")

                AuthResult.Success(
                    GoogleSignInData(
                        uid = user.uid,
                        name = user.displayName ?: "Fixora User",
                        email = user.email ?: "",
                        isEmailVerified = user.isEmailVerified
                    )
                )
            } else {
                AuthResult.Error("Unexpected credential type. Please try again.")
            }
        } catch (e: GetCredentialCancellationException) {
            AuthResult.Error("CANCELLED")
        } catch (e: NoCredentialException) {
            AuthResult.Error("Google sign-in could not find an account. Check Google Play services and try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Google sign-in failed. Please check your connection.")
        }
    }
}

data class GoogleSignInData(
    val uid: String,
    val name: String,
    val email: String,
    val isEmailVerified: Boolean
)
