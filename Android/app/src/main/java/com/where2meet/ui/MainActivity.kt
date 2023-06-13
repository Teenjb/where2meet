package com.where2meet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.where2meet.R
import com.where2meet.core.data.preference.DataStoreManager
import com.where2meet.ui.ext.toggleAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var prefs: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // setting up nav component
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_main)
        // run with lifecylce scope
        lifecycleScope.launch {
            toggleAppTheme(prefs.theme.first())
            // set start destination
            if (isLoggedIn()) {
                graph.setStartDestination(R.id.home)
            } else {
                graph.setStartDestination(R.id.onboarding)
            }
            // bind navGraph to fragment
            val navController = navHostFragment.navController
            navController.setGraph(graph, intent.extras)
        }
    }

    private suspend fun isLoggedIn(): Boolean = prefs.isLoggedIn.first()
}
