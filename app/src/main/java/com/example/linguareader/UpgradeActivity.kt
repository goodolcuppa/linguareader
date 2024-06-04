package com.example.linguareader

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UpgradeActivity : AppCompatActivity() {
    //var sharedPref: SharedPreferences? = null
    //var editor: SharedPreferences.Editor? = null

    private lateinit var btnCancel: Button
    private lateinit var btnUpgrade: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)

        //sharedPref = getPreferences(MODE_PRIVATE)
        //editor = sharedPref.edit()

        btnCancel = findViewById(R.id.btn_cancel)
        btnUpgrade = findViewById(R.id.btn_upgrade)

        // Finished the activity onClick
        btnCancel.setOnClickListener { finish() }

        // Sets the user's premium status onClick
        btnUpgrade.setOnClickListener {
//            editor.putBoolean(R.string.premium_status.toString(), true)
//            editor.apply()
//            Toast.makeText(
//                applicationContext,
//                if (sharedPref.getBoolean(
//                        R.string.premium_status.toString(),
//                        false
//                    )
//                ) "true" else "false",
//                Toast.LENGTH_SHORT
//            ).show()
            finish()
        }
    }
}