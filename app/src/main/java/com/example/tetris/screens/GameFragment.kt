package com.example.tetris.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.tetris.R
import com.example.tetris.databinding.FragmentGameTetrisBinding
import com.example.tetris.helpers.Motions
import com.example.tetris.models.AppModel
import com.example.tetris.storage.AppPreferences
import com.example.tetris.view.TetrisView

class GameFragment : Fragment(R.layout.fragment_game_tetris) {

    private var _binding: FragmentGameTetrisBinding? = null
    private val binding get() = _binding!!

    private lateinit var tetrisView: TetrisView
    private var appPreferences: AppPreferences? = null
    private val appModel: AppModel = AppModel()
    private var callback: (score: String) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameTetrisBinding.inflate(inflater, container, false)

        appPreferences = AppPreferences(requireContext())
        appModel.setPreferences(appPreferences)
        initCallbackScore()
        initTetrisView()

        binding.btnRestart.setOnClickListener { btnRestartClick() }
        listOf(binding.btnBottom, binding.btnLeft, binding.btnTop, binding.btnRight).forEach {
            it.setOnClickListener(::handleButtonClick)
        }
        startGame()
        updateHighScore()
        updateCurrentScore(getString(R.string.zero))

        return binding.root
    }

    private fun startGame() {
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()) {
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(Motions.DOWN)
        }
    }

    private fun handleButtonClick(view: View) {
        when (view as Button) {
            binding.btnLeft -> moveTetromino(Motions.LEFT)
            binding.btnRight -> moveTetromino(Motions.RIGHT)
            binding.btnBottom -> moveTetromino(Motions.DOWN)
            binding.btnTop -> moveTetromino(Motions.ROTATE)
        }
    }

    private fun initTetrisView() {
        tetrisView = binding.viewTetris
            .apply {
                setCallbackForScore(callback)
                setModel(appModel)
            }
    }

    private fun initCallbackScore() {
        callback = {
            updateCurrentScore(it)
            updateHighScore()
        }
    }

    private fun moveTetromino(motion: Motions) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    private fun btnRestartClick() {
        appModel.restartGame()
    }

    private fun updateHighScore() {
        binding.tvHighScore.text = appPreferences?.getHighScore().toString()
    }

    private fun updateCurrentScore(score: String) {
        binding.tvCurrentScore.text = score
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}