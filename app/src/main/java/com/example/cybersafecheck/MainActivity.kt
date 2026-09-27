package com.example.cybersafecheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The NavHostFragment inside activity_main.xml loads ChecklistFragment
        // (the nav graph's startDestination) and restores itself on rotation,
        // so no manual FragmentManager transaction is needed any more.
        setContentView(R.layout.activity_main)
    }
}
