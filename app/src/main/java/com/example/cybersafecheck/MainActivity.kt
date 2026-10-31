package com.example.cybersafecheck

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cybersafecheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.checklistFragment, R.id.historyFragment, R.id.reportFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            binding.appBar.updatePadding(top = bars.top)
            view.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_checklist -> {
                navController.popBackStack(R.id.checklistFragment, false)
                true
            }
            R.id.menu_history -> {
                openTopLevel(R.id.historyFragment)
                true
            }
            R.id.menu_report -> {
                openTopLevel(R.id.reportFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openTopLevel(destinationId: Int) {
        if (navController.currentDestination?.id == destinationId) return
        navController.navigate(destinationId, null, navOptions {
            popUpTo(R.id.checklistFragment)
            launchSingleTop = true
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}