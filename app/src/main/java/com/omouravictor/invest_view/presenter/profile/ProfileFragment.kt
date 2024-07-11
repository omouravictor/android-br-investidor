package com.omouravictor.invest_view.presenter.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.omouravictor.invest_view.databinding.FragmentProfileBinding
import com.omouravictor.invest_view.presenter.login.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val activity = requireActivity()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity.finish()
        }
    }

}