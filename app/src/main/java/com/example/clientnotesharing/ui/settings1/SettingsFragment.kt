package com.example.clientnotesharing.ui.settings1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.clientnotesharing.databinding.FragmentSettingsBinding
import com.example.clientnotesharing.dbLocale.DbHelper
import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.CambioPasswordRequest
import com.example.clientnotesharing.ui.sign_up_login.Login
import com.example.clientnotesharing.util.Utility
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}