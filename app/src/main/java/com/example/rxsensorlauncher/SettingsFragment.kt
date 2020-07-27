package com.example.rxsensorlauncher

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat() {

    private var xStr :String = ""

    companion object {
        fun newInstance(bundle : Bundle): SettingsFragment {
            val args = Bundle()
            val fragment = SettingsFragment()
            args.putString("Message", bundle.toString())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences,rootKey)
        val xAppPreference : Preference? = findPreference("xApp" )

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        xStr = prefs.getString("xApp","").toString()
        xAppPreference?.summary = xStr
        xAppPreference?.setOnPreferenceClickListener {

            val fragmentManager : FragmentManager? = fragmentManager

            if(fragmentManager != null){
                val fragmentTransaction : FragmentTransaction =  fragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)

                fragmentTransaction.replace(R.id.container,ApplicationFragment::newInstance.invoke(
                    ApplicationFragment()
                ))



                fragmentTransaction.commit()
            }
            setFragmentResultListener("launchApp"){ key,bundle ->
              val intent = Intent()
                val packageName:String = bundle.getString("packageName")!!
                val className:String = bundle.getString("className")!!
                intent.setClassName(packageName,className)
                xAppPreference.summary = packageName
               prefs.edit().apply(){
                   putString("xApp",packageName)
                   putString("xAppClass",className)
                   apply()
                }

            }

            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var args : Bundle? = arguments
        if(args != null){
            var str = arguments?.getString("Message")
        }
    }

}
