package com.omouravictor.invest_view.ui.new_record

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityNewRecordBinding

class NewRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_new_record) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.new_record_nav)

        setupActionBarWithNavController(navController)
    }
}