package com.example.tetris.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tetris.R
import com.example.tetris.databinding.FragmentMainTetrisBinding
import com.example.tetris.storage.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess

class MainFragment : Fragment(R.layout.fragment_main_tetris) {

    private var _binding: FragmentMainTetrisBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainTetrisBinding.inflate(inflater, container, false)

        preferences = AppPreferences(requireContext())

        initBtn()
        updateHighScore()

        return binding.root
    }

    private fun initBtn() {
        binding.apply {
            btnExit.setOnClickListener { onBtnExitClick() }
            btnNewGame.setOnClickListener { onBtnNewGameClick() }
            btnResetScore.setOnClickListener { onBtnResetScoreClick(it) }
        }
    }

    private fun updateHighScore() {
        binding.tvHighScore.text =
            getString(R.string.high_score_with_score, preferences.getHighScore())
    }

    private fun onBtnExitClick() {
        exitProcess(0)
    }

    private fun onBtnNewGameClick() {
        findNavController().navigate(R.id.action_mainFragment_to_gameFragment)
    }

    private fun onBtnResetScoreClick(view: View) {
        preferences.clearHighScore()
        Snackbar.make(view, getText(R.string.score_successfully_reset), Snackbar.LENGTH_SHORT)
            .show()
        updateHighScore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}