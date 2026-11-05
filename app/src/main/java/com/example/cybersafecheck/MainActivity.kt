package com.example.cybersafecheck

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cybersafecheck.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, R.string.notifications_denied, Toast.LENGTH_LONG).show()
        }
        scheduleBreachCheckReminder()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.checklistFragment,
                R.id.historyFragment,
                R.id.reportFragment,
                R.id.passwordCheckFragment
            )
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

        if (savedInstanceState == null) {
            askForNotificationsThenSchedule()
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
            R.id.menu_password_check -> {
                openTopLevel(R.id.passwordCheckFragment)
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

    private fun askForNotificationsThenSchedule() {
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED

        if (needsPermission) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            scheduleBreachCheckReminder()
        }
    }

    private fun scheduleBreachCheckReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val reminderRequest = PeriodicWorkRequestBuilder<BreachCheckReminderWorker>(
            REMINDER_INTERVAL,
            REMINDER_INTERVAL_UNIT
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            BreachCheckReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }

    companion object {
        private const val REMINDER_INTERVAL = 15L
        private val REMINDER_INTERVAL_UNIT = TimeUnit.MINUTES
    }
}