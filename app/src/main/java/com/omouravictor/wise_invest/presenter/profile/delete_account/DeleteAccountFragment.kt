package com.omouravictor.wise_invest.presenter.profile.delete_account

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.FragmentDeleteAccountBinding
import com.omouravictor.wise_invest.presenter.init.LoginActivity
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.user.UserViewModel
import com.omouravictor.wise_invest.util.getErrorMessage
import com.omouravictor.wise_invest.util.setupToolbarCenterText
import com.omouravictor.wise_invest.util.showErrorSnackBar
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
        setupViews()
        observeUserUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.resetUserUiState()
    }

    private fun setupViews() {
        activity.setupToolbarCenterText(getString(R.string.deleteAccount))

        binding.etEmail.setText(userViewModel.user.value.email)

        binding.apply {
            incBtnDelete.root.text = getString(R.string.delete)
            incBtnDelete.root.setOnClickListener {
                val password = etPassword.text.toString()
                if (password.isNotEmpty()) {
                    showAlertDialogForDeleteAccount(password)
                } else {
                    activity.showErrorSnackBar(
                        getString(R.string.confirmYourPasswordToDeleteAccount),
                        anchorResView = incBtnDelete.root.id
                    )
                }
            }
        }
    }

    private fun showAlertDialogForDeleteAccount(password: String) {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.deleteAccount))
            setMessage(getString(R.string.deleteAccountAlertMessage))
            setPositiveButton(getString(R.string.yes)) { _, _ -> userViewModel.deleteUser(password) }
            setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
        }.show()
    }

    private fun handleUserLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                mainLayout.visibility = View.INVISIBLE
                incBtnDelete.root.visibility = View.INVISIBLE
                incProgressBar.root.visibility = View.VISIBLE
            } else {
                mainLayout.visibility = View.VISIBLE
                incBtnDelete.root.visibility = View.VISIBLE
                incProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun handleUserSuccess() {
        startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }

    private fun handleUserError(e: Exception) {
        handleUserLoading(false)
        activity.showErrorSnackBar(activity.getErrorMessage(e), anchorResView = binding.incBtnDelete.root.id)
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