package com.omouravictor.wise_invest.presenter.profile.change_personal_data

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.FragmentChangePersonalDataBinding
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.user.UserViewModel
import com.omouravictor.wise_invest.util.getErrorMessage
import com.omouravictor.wise_invest.util.setupToolbarCenterText
import com.omouravictor.wise_invest.util.showErrorSnackBar
import com.omouravictor.wise_invest.util.showSuccessSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChangePersonalDataFragment : Fragment(R.layout.fragment_change_personal_data) {

    private lateinit var binding: FragmentChangePersonalDataBinding
    private lateinit var activity: Activity
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChangePersonalDataBinding.bind(view)
        setupToolbar()
        setupViews()
        observeUserUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.resetUserUiState()
    }

    private fun setupToolbar() {
        activity.setupToolbarCenterText(getString(R.string.changePersonalData))
    }

    private fun setupViews() {
        binding.apply {
            etName.setText(userViewModel.user.value.name)
        }

        binding.apply {
            incBtnSave.root.text = getString(R.string.save)
            incBtnSave.root.setOnClickListener {
                val updatedUser = userViewModel.user.value.copy(name = etName.text.toString().trim())
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
        activity.showSuccessSnackBar(
            getString(R.string.userUpdatedSuccessfully),
            anchorResView = binding.incBtnSave.root.id
        )
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