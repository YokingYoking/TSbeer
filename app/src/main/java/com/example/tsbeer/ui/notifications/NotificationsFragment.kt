package com.example.tsbeer.ui.notifications

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tsbeer.*
import com.example.tsbeer.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    lateinit var mToLoginBtn: Button
    lateinit var myApp: MyApplication
    lateinit var mHelloTv: TextView
    lateinit var mUserFunctions: ListView

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
        mHelloTv = binding.helloTv
        mToLoginBtn = binding.toLogin
        mUserFunctions = binding.userFunctions
        myApp = activity?.application as MyApplication
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        if (myApp.name == "") {
            mHelloTv.text =  resources.getString(R.string.hello) + "  " + resources.getString(R.string.login_first)
            mToLoginBtn.setOnClickListener {
                mToLoginBtn.text = resources.getString(R.string.logout)
                val intent = Intent(getActivity()?.getApplicationContext(), LoginActivity::class.java)
                startActivity(intent)
                mToLoginBtn.text = resources.getString(R.string.toLogin)
            }
        } else {
            mToLoginBtn.text = resources.getString(R.string.logout)
            mHelloTv.text =  resources.getString(R.string.hello) + "  " + myApp.nickname
            val functions = resources.getStringArray(R.array.user_function)
            var adapter = activity?.applicationContext?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_expandable_list_item_1,
                    functions
                )
            }
            mUserFunctions.adapter = adapter
            mUserFunctions.setOnItemClickListener(){adapterView, view, position, id ->
                when(position) {
                    0 -> {
                        val intent = Intent(getActivity()?.getApplicationContext(), ChangePwActivity::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(getActivity()?.getApplicationContext(), ChangeNicknameActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
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