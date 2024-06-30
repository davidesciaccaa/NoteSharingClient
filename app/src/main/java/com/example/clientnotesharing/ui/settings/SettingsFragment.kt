package com.example.clientnotesharing.ui.settings

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
import com.example.clientnotesharing.data.MessageResponse
import com.example.clientnotesharing.ui.sign_up_login.Login
import com.example.clientnotesharing.util.Utility
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
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
        binding.tvHelloUser.text = getString(R.string.hello_user,  Utility().getUsername(requireContext()))
        binding.btnLogOut.setOnClickListener{
            logout()
        }
        binding.btnCambioPsw.setOnClickListener{
            val oldPassword = binding.editTextOldPsw.text.toString()
            val newPassword = binding.editTextNewPsw.text.toString()
            if(oldPassword.isNotBlank() && newPassword.isNotBlank()){
                lifecycleScope.launch {
                    try {
                        val result = NotesApi.retrofitService.cambioPsw(CambioPasswordRequest(oldPassword, newPassword, Utility().getUsername(requireContext())))
                        if(result.isSuccessful) {
                            logout()
                        } else {
                            binding.tvConfirmPsw.text = getString(R.string.psw_not_changed)
                        }
                    } catch (e: HttpException) {
                        Log.e("LoginActivity", "HTTP Exception: ${e.message}")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.e("LoginActivity", "IO Exception: ${e.message}")
                        e.printStackTrace()
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Exception: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun logout() {
        //elimino db locale
        val dbHelper = DbHelper(requireContext())
        dbHelper.deleteDatabase()
        // Elimino lo username dalle SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Clears all data in SharedPreferences
        editor.apply()
        // apro la loginscreen
        val intent = Intent(requireContext(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //faccio clear del backstack
        startActivity(intent)
    }
}