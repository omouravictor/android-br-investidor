package com.omouravictor.invest_view.presenter.profile.change_personal_data

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentChangePersonalDataBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.user.UserViewModel
import com.omouravictor.invest_view.util.setupToolbarCenterText
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
                val updatedUser = userViewModel.user.value.copy(name = etName.text.toString())
                userViewModel.updateUser(updatedUser)
            }
        }
    }

    private fun observeUserUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.userUiState.collectLatest {
                    when (it) {
                        is UiState.Success -> {
                            activity.onBackPressed()
                        }

                        is UiState.Error -> {

                        }

                        else -> {}
                    }
                }
            }
        }
    }

}