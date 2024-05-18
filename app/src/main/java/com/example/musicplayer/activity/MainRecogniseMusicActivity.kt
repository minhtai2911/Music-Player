package com.example.musicplayer.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.adapter.MainViewPagerAdapter
import com.example.musicplayer.databinding.ActivityMainrecogniseBinding
import com.example.musicplayer.fragment.HomeFragment
import com.example.musicplayer.service.FindMusicTileService
import com.example.musicplayer.viewmodel.IdentifyViewModel
import kotlinx.coroutines.launch

class MainRecogniseMusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainrecogniseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainrecogniseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initIdentifyFragment()

        // https://stackoverflow.com/a/50537193/12825435
    }

    private fun initIdentifyFragment() {
        val mainViewPagerAdapter = MainViewPagerAdapter(
            supportFragmentManager
        )
        mainViewPagerAdapter.addFragment(IdentifyFragment(), "")
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!FindMusicTileService.handled) {
                    FindMusicTileService.handled = true
                    val identifyViewModel by viewModels<IdentifyViewModel>()
                    identifyViewModel.start()
                }
            }
        }
    }
}
