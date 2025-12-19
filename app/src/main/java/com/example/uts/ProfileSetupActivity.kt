package com.example.uts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.fragments.StepAgeHeightWeightFragment

class ProfileSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerSetup, StepAgeHeightWeightFragment())
            .commit()
    }
}
