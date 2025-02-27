package com.omouravictor.br_investidor.presenter.profile.change_password

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.databinding.FragmentChangePasswordBinding
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.user.UserViewModel
import com.omouravictor.br_investidor.util.getErrorMessage
import com.omouravictor.br_investidor.util.setupToolbarCenterText
import com.omouravictor.br_investidor.util.showErrorSnackBar
import com.omouravictor.br_investidor.util.showSuccessSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var activity: Activity
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChangePasswordBinding.bind(view)
        setupViews()
        observeUserUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.resetUserUiState()
    }

    private fun setupViews() {
        activity.setupToolbarCenterText(getString(R.string.changePassword))

        binding.etConfirmEmail.setText(userViewModel.user.value.email)

        binding.apply {
            incBtnChange.root.text = getString(R.string.change)
            incBtnChange.root.setOnClickListener {
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                    userViewModel.changePassword(currentPassword, newPassword)
                } else {
                    activity.showErrorSnackBar(
                        getString(R.string.fillAllFields),
                        anchorResView = incBtnChange.root.id
                    )
                }
            }
        }
    }

    private fun handleUserLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                mainLayout.visibility = View.INVISIBLE
                incBtnChange.root.visibility = View.INVISIBLE
                incProgressBar.root.visibility = View.VISIBLE
            } else {
                mainLayout.visibility = View.VISIBLE
                incBtnChange.root.visibility = View.VISIBLE
                incProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun handleUserSuccess() {
        handleUserLoading(false)
        activity.showSuccessSnackBar(getString(R.string.passwordUpdatedSuccessfully))
        findNavController().popBackStack()
    }

    private fun handleUserError(e: Exception) {
        handleUserLoading(false)
        activity.showErrorSnackBar(activity.getErrorMessage(e), anchorResView = binding.incBtnChange.root.id)
    }

    private fun observeUserUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.userUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleUserLoading(true)
                        is UiState.Success -> handleUserSuccess()
                        is UiState.Error -> handleUserError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

}