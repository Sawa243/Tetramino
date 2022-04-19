package com.example.tetris.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tetris.R
import com.example.tetris.databinding.FragmentGameBinding
import com.example.tetris.models.AppModel
import com.example.tetris.storage.AppPreferences
import com.example.tetris.view.TetrisView

class GameFragment : Fragment(R.layout.fragment_game) {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var tetrisView: TetrisView
    private var appPreferences: AppPreferences? = null
    private val appModel: AppModel = AppModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)

        appPreferences = AppPreferences(requireContext())
        appModel.setPreferences(appPreferences)
        tetrisView = binding.viewTetris
        tetrisView.apply {
            setBinding(binding)// переделать
            setModel(appModel)
            appPreferences?.let { setPreferences(it) }// переделать
        }

        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        binding.btnRestart.setOnClickListener { btnRestartClick() }

        updateHighScore()
        updateCurrentScore()

        return binding.root
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()) {
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        } else if (appModel.isGameActive()) {
            when (resolveTouchDirection(view, event)) {
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }
        }
        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height
        val direction: Int = if (y > x) {
            if (x > 1 - y) 2 else 0
        } else {
            if (x > 1 - y) 3 else 1
        }
        return direction
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    private fun btnRestartClick() {
        appModel.restartGame()
    }

    private fun updateHighScore() {
        binding.tvHighScore.text = "${appPreferences?.getHighScore()}"
    }

    private fun updateCurrentScore() {
        binding.tvCurrentScore.text = "0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}