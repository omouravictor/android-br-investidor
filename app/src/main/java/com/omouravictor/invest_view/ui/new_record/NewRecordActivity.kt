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
        setContentView(R.layout.activity_new_record)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_wallet)

        setupActionBarWithNavController(navController)
    }
}