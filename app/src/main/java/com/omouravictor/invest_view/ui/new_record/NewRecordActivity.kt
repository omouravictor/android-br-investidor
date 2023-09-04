package com.omouravictor.invest_view.ui.new_record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityMainBinding
import com.omouravictor.invest_view.databinding.ActivityNewRecordBinding

class NewRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecordBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_new_record)
    }
}