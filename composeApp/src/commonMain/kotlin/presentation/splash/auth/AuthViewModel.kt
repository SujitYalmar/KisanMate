package com.example.kisanmate.presentation.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private var verificationId: String? = null
    var activity: Activity? = null

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnPhoneChange ->
                _state.update { it.copy(phoneNumber = action.phone.filter { c -> c.isDigit() }) }

            is AuthAction.OnOtpChange ->
                _state.update { it.copy(otpCode = action.code) }

            is AuthAction.OnNameChange ->
                _state.update { it.copy(name = action.name) }

            AuthAction.SendOtp -> sendOtp()
            AuthAction.VerifyOtp -> verifyCode()

            AuthAction.ToggleAuthMode ->
                _state.update { it.copy(isSignupMode = !it.isSignupMode, error = null) }
        }
    }

    private fun sendOtp() {
        val phone = _state.value.phoneNumber
        val act = activity ?: return

        if (phone.length != 10) {
            _state.update { it.copy(error = "Enter valid number") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(act)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            firebaseAuth.signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _state.update {
                it.copy(isLoading = false, error = e.message)
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@AuthViewModel.verificationId = verificationId
            _state.update {
                it.copy(isLoading = false, isOtpSent = true)
            }
        }
    }

    private fun verifyCode() {
        val code = _state.value.otpCode
        val verId = verificationId ?: return

        if (code.length < 6) {
            _state.update { it.copy(error = "Enter 6-digit OTP") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        val credential = PhoneAuthProvider.getCredential(verId, code)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _state.update {
                        it.copy(isLoading = false, error = "Invalid OTP")
                    }
                    return@addOnCompleteListener
                }

                val user = task.result.user ?: return@addOnCompleteListener

                viewModelScope.launch {

                    val userDoc = firestore.collection("users")
                        .document(user.uid)
                        .get()

                    if (userDoc.exists) {
                        // ✅ Existing user → login success
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = true
                            )
                        }
                    } else {
                        // ❌ User does not exist in Firestore
                        if (_state.value.name.isBlank()) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Please create account first",
                                    isSignupMode = true
                                )
                            }
                            return@launch
                        }

                        // ✅ Create new account
                        firestore.collection("users")
                            .document(user.uid)
                            .set(
                                mapOf(
                                    "name" to _state.value.name,
                                    "phone" to _state.value.phoneNumber,
                                    "createdAt" to System.currentTimeMillis()
                                )
                            )

                        _state.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = true
                            )
                        }
                    }
                }
            }
    }
}