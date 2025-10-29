package com.example.recipedia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.recipedia.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        enableEdgeToEdgeWithInsets(binding.root,binding.drawerLayout)

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
            this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this,R.color.red)

        supportFragmentManager.addOnBackStackChangedListener {
            updateBottomNavSelection()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, Home())
                .commit()
            navView.setCheckedItem(R.id.nav_home)
            onItemClick(binding.navbarHome)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            val view = binding.navView.findViewById<View>(menuItem.itemId)
            val animation = AnimationUtils.loadAnimation(this,R.anim.nav_item_click)
            view.startAnimation(animation)

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.framelayout)
                    if (currentFragment !is Home) {
                        replaceWithFragment(Home())
                    }
                    true
                }
                R.id.search_nav -> {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.framelayout)
                    if (currentFragment !is Search) {
                        replaceWithFragment(Search())
                    }
                    true
                }
                R.id.nav_favorite -> {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.framelayout)
                    if (currentFragment !is Favorite) {
                        replaceWithFragment(Favorite())
                    }
                    true
                }
                R.id.nav_logout -> {
                    val pref = getSharedPreferences("Pref_name",MODE_PRIVATE)
                    pref.edit {putBoolean("isLoggedIn",false)}
                    val intent = Intent(this, LoginPage::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }


        binding.navbarHome.setOnClickListener {
            val home = binding.navbarHome
            onItemClick(home)
            val currentFragment = supportFragmentManager.findFragmentById(R.id.framelayout)
            if (currentFragment !is Home) {
                replaceWithFragment(Home())
            }
        }
        binding.navbarSearch.setOnClickListener {
            val search = binding.navbarSearch
            onItemClick(search)
            val currentFragment = supportFragmentManager.findFragmentById(R.id.framelayout)
            if (currentFragment !is Search) {
                replaceWithFragment(Search())
            }
        }
        binding.navbarFavorite.setOnClickListener {
            val favourite = binding.navbarFavorite
            onItemClick(favourite)
            val currentFragment = supportFragmentManager.findFragmentById(R.id.framelayout)
            if (currentFragment !is Favorite) {
                replaceWithFragment(Favorite())
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                if (!supportFragmentManager.popBackStackImmediate()) {
                    finish()
                }
            }
        }
    }
    private fun updateBottomNavSelection() {
        clearSelection()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.framelayout)
        when(currentFragment) {
            is Home -> onItemClick(binding.navbarHome)
            is Search -> onItemClick(binding.navbarSearch)
            is Favorite -> onItemClick(binding.navbarFavorite)
        }

        when(currentFragment) {
            is Home -> binding.navView.setCheckedItem(R.id.nav_home)
            is Search -> binding.navView.setCheckedItem(R.id.search_nav)
            is Favorite -> binding.navView.setCheckedItem(R.id.nav_favorite)
        }
    }
    private fun replaceWithFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        fragmentTransaction.replace(R.id.framelayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun onItemClick(imageViewCompat: AppCompatImageView) {
        clearSelection()
        imageViewCompat.isSelected = true
        val animation = AnimationUtils.loadAnimation(this,R.anim.nav_item_click)
        imageViewCompat.startAnimation(animation)
    }
    private fun clearSelection() {
        binding.navbarHome.isSelected = false
        binding.navbarSearch.isSelected = false
        binding.navbarFavorite.isSelected = false
    }
    private fun enableEdgeToEdgeWithInsets(rootView: View, LayoutView: View) {
        val activity = rootView.context as ComponentActivity
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            LayoutView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom
            }

            insets
        }
    }
}