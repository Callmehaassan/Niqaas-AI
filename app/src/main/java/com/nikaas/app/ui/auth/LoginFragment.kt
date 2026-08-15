package com.nikaas.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nikaas.app.R
import com.nikaas.app.databinding.FragmentLoginBinding
import com.nikaas.app.ui.common.hide
import com.nikaas.app.ui.common.show

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var isSignUpMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is already logged in, auto-route them
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Auto-route based on email domain or preference (default to citizen)
            if (currentUser.email?.endsWith("@wasa.gov.pk") == true || currentUser.email?.endsWith("@nikaas.gov.pk") == true) {
                findNavController().navigate(R.id.action_login_to_dashboard)
            } else {
                findNavController().navigate(R.id.action_login_to_citizen)
            }
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Toggle signup vs login modes
        binding.btnToggleMode.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUiMode()
        }

        // Action button
        binding.btnAction.setOnClickListener {
            handleAuthAction()
        }
    }

    private fun updateUiMode() {
        if (isSignUpMode) {
            binding.txtAuthTitle.text = "Create Verified Account"
            binding.layoutInputName.show()
            binding.layoutInputPhone.show()
            binding.btnAction.text = "Register"
            binding.btnToggleMode.text = "Already have an account? Sign In"
        } else {
            binding.txtAuthTitle.text = "Sign In to Your Account"
            binding.layoutInputName.hide()
            binding.layoutInputPhone.hide()
            binding.btnAction.text = "Sign In"
            binding.btnToggleMode.text = "Don't have an account? Sign Up"
        }
    }

    private fun handleAuthAction() {
        val email = binding.inputEmail.text?.toString()?.trim() ?: ""
        val password = binding.inputPassword.text?.toString()?.trim() ?: ""

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnAction.isEnabled = false
        binding.progressAuth.show()

        val isCitizenSelected = binding.chipRoleCitizen.isChecked

        if (isSignUpMode) {
            val name = binding.inputName.text?.toString()?.trim() ?: ""
            val phone = binding.inputPhone.text?.toString()?.trim() ?: ""

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT).show()
                binding.btnAction.isEnabled = true
                binding.progressAuth.hide()
                return
            }

            android.util.Log.d("NikaasAuth", "Starting registration for: $email")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        android.util.Log.d("NikaasAuth", "Firebase Auth registration success: ${user?.uid}")
                        if (user != null) {
                            val userProfile = mapOf(
                                "uid" to user.uid,
                                "name" to name,
                                "phone" to phone,
                                "email" to email,
                                "role" to if (isCitizenSelected) "Citizen" else "Authority"
                            )
                            // Run firestore save in background, do not block navigation progress!
                            firestore.collection("users").document(user.uid).set(userProfile)
                                .addOnFailureListener { fe ->
                                    android.util.Log.e("NikaasAuth", "Firestore profile write failed", fe)
                                }
                        }
                        navigateToPortal(isCitizenSelected)
                    } else {
                        binding.btnAction.isEnabled = true
                        binding.progressAuth.hide()
                        val errorMsg = task.exception?.localizedMessage ?: "Unknown Error"
                        android.util.Log.e("NikaasAuth", "Registration failed: $errorMsg", task.exception)
                        Toast.makeText(requireContext(), "Registration Failed: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            android.util.Log.d("NikaasAuth", "Starting sign in for: $email")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        android.util.Log.d("NikaasAuth", "Sign in success")
                        val isAuthorityDomain = email.endsWith("@wasa.gov.pk") || email.endsWith("@nikaas.gov.pk")
                        navigateToPortal(!isAuthorityDomain && isCitizenSelected)
                    } else {
                        binding.btnAction.isEnabled = true
                        binding.progressAuth.hide()
                        val errorMsg = task.exception?.localizedMessage ?: "Unknown Error"
                        android.util.Log.e("NikaasAuth", "Sign in failed: $errorMsg", task.exception)
                        Toast.makeText(requireContext(), "Authentication Failed: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun navigateToPortal(isCitizen: Boolean) {
        binding.btnAction.isEnabled = true
        binding.progressAuth.hide()
        if (isCitizen) {
            findNavController().navigate(R.id.action_login_to_citizen)
        } else {
            findNavController().navigate(R.id.action_login_to_dashboard)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
