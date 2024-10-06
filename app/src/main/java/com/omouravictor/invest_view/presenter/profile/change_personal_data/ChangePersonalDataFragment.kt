package com.omouravictor.invest_view.presenter.profile.change_personal_data

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentChangePersonalDataBinding
import com.omouravictor.invest_view.presenter.user.UserViewModel
import com.omouravictor.invest_view.util.setupToolbarCenterText

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
    }

    private fun setupToolbar() {
        activity.setupToolbarCenterText(getString(R.string.changePersonalData))
    }

    private fun setupViews() {
        binding.etName.setText(userViewModel.user.value.name)
    }

}