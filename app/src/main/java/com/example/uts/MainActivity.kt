package com.example.uts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.uts.databinding.ActivityMainBinding
import com.example.uts.fragments.HomeFragment
import com.example.uts.fragments.HistoryFragment
import com.example.uts.fragments.ProfileFragment
import com.example.uts.fragments.ScanFragment

class MainActivity : AppCompatActivity() {
    // View binding untuk akses semua view
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Tampilkan fragment HomeFragment sebagai default
        replaceFragment(HomeFragment())

        // Listener untuk bottom navigation
        b.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_scan -> replaceFragment(ScanFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    // Fungsi untuk mengganti fragment di container
    private fun replaceFragment(f: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, f).commit()
    }
}
