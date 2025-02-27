package com.omouravictor.br_investidor.presenter.profile.change_personal_data

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.databinding.FragmentChangePersonalDataBinding
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.user.UserViewModel
import com.omouravictor.br_investidor.util.getErrorMessage
import com.omouravictor.br_investidor.util.hideKeyboard
import com.omouravictor.br_investidor.util.setupToolbarCenterText
import com.omouravictor.br_investidor.util.showErrorSnackBar
import com.omouravictor.br_investidor.util.showSuccessSnackBar
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
        setupViews()
        observeUserUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.resetUserUiState()
    }

    private fun setupViews() {
        activity.setupToolbarCenterText(getString(R.string.changePersonalData))

        binding.apply {
            etChangeName.setText(userViewModel.user.value.name)
        }

        binding.apply {
            incBtnSave.root.text = getString(R.string.save)
            incBtnSave.root.setOnClickListener {
                activity.hideKeyboard(root)
                val updatedUser = userViewModel.user.value.copy(name = etChangeName.text.toString().trim())
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