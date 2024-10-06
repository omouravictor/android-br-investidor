package com.omouravictor.invest_view.presenter.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentProfileBinding
import com.omouravictor.invest_view.presenter.init.LoginActivity
import com.omouravictor.invest_view.presenter.user.UserViewModel
import com.omouravictor.invest_view.presenter.user.getFormattedName
import com.omouravictor.invest_view.util.setupToolbarTitle

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        val activity = requireActivity()

        activity.setupToolbarTitle("Olá, ${userViewModel.user.value.getFormattedName()}")

        binding.apply {
            incChangePersonalData.tvOption.text = getString(R.string.changePersonalData)
            incDeleteAccount.tvOption.text = getString(R.string.deleteAccount)
            incChangePassword.tvOption.text = getString(R.string.changePassword)

            layoutLogout.setOnClickListener {
                userViewModel.logout()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity.finish()
            }
        }
    }

}