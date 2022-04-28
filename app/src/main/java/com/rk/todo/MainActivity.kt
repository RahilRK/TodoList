package com.rk.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rk.todo.Util.Constant
import com.rk.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var TAG = "MainActivity"
    private var activity = this

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var globalClass: GlobalClass

    var shouldGoBack: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        navController = findNavController(R.id.fragment)
        binding.bottomNavigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.menuFragment))
//        setupActionBarWithNavController(navController,appBarConfiguration)

        onClick()
    }

    fun init() {
        globalClass = GlobalClass.getInstance(activity)
    }

    fun onClick() {

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            globalClass.log(TAG, "onDestinationChanged: "+destination.label);
            show_hideHomeViews(destination.label)
        }
    }

    fun show_hideHomeViews(label: CharSequence?) {

        if(label == "Home") {

            shouldGoBack = true;
            slideUpBottomNav()
        }
        else if(label == "Menu") {

            shouldGoBack = true;
        }
        else if(label == "Add Note") {

            shouldGoBack = false;
            slideDownBottomNav()
        }
    }

    fun slideDownBottomNav() {

        binding.bottomNavigationView.clearAnimation();
        binding.bottomNavigationView
            .animate()
            .translationY(binding.bottomNavigationView.height.toFloat())
            .setDuration(400);
    }

    fun slideUpBottomNav() {

        binding.bottomNavigationView.clearAnimation();
        binding.bottomNavigationView
            .animate()
            .translationY(0F)
            .setDuration(400);
    }

    override fun onBackPressed() {

        if(shouldGoBack) {
            super.onBackPressed()
        }
        else {

            val intent = Intent(Constant.onBackPressReceiver)
            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)
            shouldGoBack = true
        }
    }
}