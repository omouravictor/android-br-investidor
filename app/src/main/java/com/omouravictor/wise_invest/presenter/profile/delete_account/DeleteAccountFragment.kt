package com.omouravictor.wise_invest.presenter.profile.delete_account

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.FragmentDeleteAccountBinding
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.user.UserViewModel
import com.omouravictor.wise_invest.util.getErrorMessage
import com.omouravictor.wise_invest.util.setupToolbarCenterText
import com.omouravictor.wise_invest.util.showErrorSnackBar
import com.omouravictor.wise_invest.util.showSuccessSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeleteAccountFragment : Fragment(R.layout.fragment_delete_account) {

    private lateinit var binding: FragmentDeleteAccountBinding
    private lateinit var activity: Activity
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDeleteAccountBinding.bind(view)
        setupToolbar()
        setupViews()
        observeUserUiState()
    }

    private fun setupToolbar() {
        activity.setupToolbarCenterText(getString(R.string.deleteAccount))
    }

    private fun setupViews() {
        binding.apply {
            etEmail.setText("omouravictor@gmail.com")
        }

        binding.apply {
            incBtnSave.root.text = getString(R.string.delete)
            incBtnSave.root.setOnClickListener {
                val updatedUser = userViewModel.user.value.copy(name = etEmail.text.toString().trim())
                userViewModel.updateUser(updatedUser)
            }
        }
    }

    private fun handleUserLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                mainLayout.visibility = View.INVISIBLE
                incBtnSave.root.visibility = View.INVISIBLE
                incProgressBar.root.visibility = View.VISIBLE
            } else {
                mainLayout.visibility = View.VISIBLE
                incBtnSave.root.visibility = View.VISIBLE
                incProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun handleUserSuccess() {
        handleUserLoading(false)
        activity.showSuccessSnackBar(getString(R.string.userUpdatedSuccessfully), binding.incBtnSave.root.id)
    }

    private fun handleUserError(e: Exception) {
        handleUserLoading(false)
        activity.showErrorSnackBar(activity.getErrorMessage(e))
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