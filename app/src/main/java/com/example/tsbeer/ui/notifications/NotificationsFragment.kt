package com.example.tsbeer.ui.notifications

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tsbeer.DetailActivity
import com.example.tsbeer.LoginActivity
import com.example.tsbeer.MyApplication
import com.example.tsbeer.R
import com.example.tsbeer.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    lateinit var mToLoginBtn: Button
    lateinit var myApp: MyApplication

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val notificationsViewModel =
//            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mToLoginBtn = binding.toLogin
        myApp = activity?.application as MyApplication
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        if (myApp.name == "") {
            mToLoginBtn.setOnClickListener {
                mToLoginBtn.text = resources.getString(R.string.logout)
                val intent = Intent(getActivity()?.getApplicationContext(), LoginActivity::class.java)
                startActivity(intent)
                mToLoginBtn.text = resources.getString(R.string.toLogin)
            }
        } else {
            mToLoginBtn.text = resources.getString(R.string.logout)
            mToLoginBtn.setOnClickListener {
                myApp.name = ""
                Toast.makeText(activity, "logout Successfully!", Toast.LENGTH_LONG).show()
                mToLoginBtn.text = resources.getString(R.string.toLogin)
                mToLoginBtn.setOnClickListener {
                    val intent = Intent(getActivity()?.getApplicationContext(), LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}