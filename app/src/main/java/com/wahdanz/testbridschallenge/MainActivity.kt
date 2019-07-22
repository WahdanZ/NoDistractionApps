package com.wahdanz.testbridschallenge

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.wahdanz.testbridschallenge.utils.Utils
import com.wahdanz.testbridschallenge.utils.setUp
import com.wahdanz.testbridschallenge.viewmodel.AppsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_item.view.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<AppsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewModel.getAllApps()
        serviceEnable.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    if (accessibilityCheckBox.isChecked && !Utils.isAccessibilitySettingsOn(this)) {
                        Toast.makeText(this, "Allow Accessibility Access", Toast.LENGTH_LONG).show()
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        startActivity(intent)
                    } else if (!accessibilityCheckBox.isChecked && !Utils.getUsageStatsPermissionsStatus(this)) {
                        Toast.makeText(this, "Allow Usage Access", Toast.LENGTH_LONG).show()
                        intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        startActivityForResult(intent, 1)
                    }
                }
                viewModel.serviceEnable(isChecked, accessibilityCheckBox.isChecked)
            }
        }
        viewModel.allApps().observe(this, Observer {
            recyclerView.setUp(items = it, layoutResId = arrayOf(R.layout.app_item),
                bindHolder = { appEntity ->
                    appName.text = appEntity.appName
                    appEnable.isChecked = appEntity.enable
                    appEnable.setOnCheckedChangeListener { _, isChecked ->
                        run {
                            if (isChecked) viewModel.lockApp(appEntity.packageName)
                            else viewModel.unLockApp(appEntity.packageName)
                        }
                    }
                })
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.serviceEnable(serviceEnable.isChecked, accessibilityCheckBox.isChecked)
    }

    override fun onResume() {
        super.onResume()
        accessibilityCheckBox.isChecked = viewModel.isAccessibilityEnable()
        serviceEnable.isChecked = viewModel.isServesRunning()
    }
}
