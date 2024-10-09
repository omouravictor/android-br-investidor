package com.omouravictor.invest_view.presenter.profile

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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentProfileBinding
import com.omouravictor.invest_view.presenter.init.LoginActivity
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.user.UserViewModel
import com.omouravictor.invest_view.presenter.user.getFormattedName
import com.omouravictor.invest_view.util.getErrorMessage
import com.omouravictor.invest_view.util.setupToolbarTitle
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var activity: Activity
    private lateinit var navController: NavController
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        setupToolbar()
        setupViews()
        observeUserUiState()
    }

    private fun setupToolbar() {
        activity.setupToolbarTitle(getString(R.string.helloUser, userViewModel.user.value.getFormattedName()))
    }

    private fun showAlertDialogForLogout() {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.logoutAlertTitle))
            setMessage(getString(R.string.logoutAlertMessage))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                userViewModel.logout()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity.finish()
            }
            setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
        }.show()
    }

    private fun setupViews() {
        binding.incChangePersonalData.apply {
            tvOption.text = getString(R.string.changePersonalData)
            root.setOnClickListener { navController.navigate(R.id.navToChangePersonalDataFragment) }
        }

        binding.layoutLogout.setOnClickListener {
            showAlertDialogForLogout()
        }
    }

    private fun handleUserLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                mainLayout.visibility = View.INVISIBLE
                incProgressBar.root.visibility = View.VISIBLE
            } else {
                mainLayout.visibility = View.VISIBLE
                incProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun handleUserSuccess() {
        handleUserLoading(false)
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