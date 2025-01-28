package com.omouravictor.wise_invest.presenter.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.FragmentProfileBinding
import com.omouravictor.wise_invest.presenter.init.LoginActivity
import com.omouravictor.wise_invest.presenter.user.UserViewModel
import com.omouravictor.wise_invest.presenter.user.getFormattedName
import com.omouravictor.wise_invest.util.setupToolbarTitle

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
        setupViews()
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
        activity.setupToolbarTitle(getString(R.string.helloUser, userViewModel.user.value.getFormattedName()))

        binding.incChangePersonalData.apply {
            tvOption.text = getString(R.string.changePersonalData)
            root.setOnClickListener { navController.navigate(R.id.navToChangePersonalDataFragment) }
        }

        binding.incChangePassword.apply {
            tvOption.text = getString(R.string.changePassword)
            root.setOnClickListener { navController.navigate(R.id.navToChangePasswordFragment) }
        }

        binding.incDeleteAccount.apply {
            tvOption.text = getString(R.string.deleteAccount)
            root.setOnClickListener { navController.navigate(R.id.navToDeleteAccountFragment) }
        }

        binding.layoutLogout.setOnClickListener {
            showAlertDialogForLogout()
        }
    }

}