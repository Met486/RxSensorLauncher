package com.example.rxsensorlauncher

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ApplicationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun newInstance(): ApplicationFragment{
        val args = Bundle()

        val fragment = ApplicationFragment()
        fragment.arguments = args
        return fragment
    }

    override public fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view:View = inflater.inflate(R.layout.fragment_application,container,false)
        val adapter = AppAdapter(layoutInflater,create(this.requireContext())) { view, info ->
            var componentName : ComponentName = info.launch(this.requireContext())!!

            var bundle : Bundle = Bundle()
            bundle.putString("packageName" ,componentName.packageName)
            bundle.putString("className",componentName.className)

            setFragmentResult("launchApp", bundle)
            requireFragmentManager().popBackStack()
        }
        var recyclerView : RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        return view;
    }
}