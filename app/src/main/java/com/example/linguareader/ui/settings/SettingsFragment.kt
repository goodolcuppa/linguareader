package com.example.linguareader.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.linguareader.MainActivity
import com.example.linguareader.R
import com.example.linguareader.UpgradeActivity
import com.example.linguareader.databinding.FragmentSettingsBinding
import com.example.linguareader.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    //var sharedPref: SharedPreferences? = null
    private lateinit var btnLogout: Button
    private lateinit var btnUpgrade: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.getRoot()

        btnLogout = root.findViewById(R.id.btn_logout)
        btnUpgrade = root.findViewById(R.id.btn_upgrade)

        // Signs out with firebase and starts the login activity onClick
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Gets the user's email from Firebase Authentication
        val userText = root.findViewById<View>(R.id.text_email) as TextView
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        // Displays the user's email if logged in
        if (user != null) {
            userText.text = user.email
            btnLogout.setText(R.string.logout)
        }

        // Adds an onClick to start the UpgradeActivity
        btnUpgrade.setOnClickListener {
            val intent = Intent(activity, UpgradeActivity::class.java)
            startActivity(intent)
        }

        // Disables btnUpgrade if premium status is enabled
        //sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        //btnUpgrade.isEnabled = !sharedPref.getBoolean(String.valueOf(R.string.premium_status), false)

        return root
    }

    override fun onResume() {
        super.onResume()
        // Sets the action bar title
        // TODO: display icon
        (activity as MainActivity).supportActionBar!!.setTitle(R.string.title_settings)
        (activity as MainActivity).supportActionBar!!.setIcon(R.drawable.ic_settings)

        // Gets the user's premium status from SharedPreferences to control btnUpgrade state
        //sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        //btnUpgrade.isEnabled = !sharedPref.getBoolean(String.valueOf(R.string.premium_status), false)
    }
}