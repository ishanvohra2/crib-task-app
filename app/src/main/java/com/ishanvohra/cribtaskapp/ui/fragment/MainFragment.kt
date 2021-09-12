package com.ishanvohra.cribtaskapp.ui.fragment

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_SMS
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ishanvohra.cribtaskapp.R
import com.ishanvohra.cribtaskapp.models.SMS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class MainFragment : Fragment() {

    private var phoneEt: EditText? = null
    private var dateEt: EditText? = null
    private var resultTv: TextView? = null
    private var submitBtn: Button? = null

    private val TAG = javaClass.simpleName

    private var messageList = ArrayList<SMS>()
    private var minDate = Date()

    private var viewModel: MainFragmentViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        bindViews(view)

        viewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        viewModel?.context = requireContext()

        return view
    }

    private fun bindViews(view: View) {
        phoneEt = view.findViewById(R.id.phone_number_et)
        dateEt = view.findViewById(R.id.days_et)
        resultTv = view.findViewById(R.id.result_tv)
        submitBtn = view.findViewById(R.id.submit_btn)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForPermissions()

        submitBtn?.setOnClickListener {
            if(phoneEt?.text.toString().isEmpty()){
                phoneEt?.error = "Please enter a valid phone number"
                return@setOnClickListener
            }

            if(dateEt?.text.toString().isEmpty()){
                dateEt?.error = "Please enter a valid number of days."
                return@setOnClickListener
            }

            val calendarInstance = Calendar.getInstance()
            calendarInstance.add(Calendar.DATE, -1*(dateEt?.text.toString().toInt()))

            minDate = calendarInstance.time

            if(!messageList.isNullOrEmpty()){
                var count = 0
                for(message in messageList){
                    if(phoneEt?.text.toString() == message.number && message.date > minDate){
                        count +=1
                    }
                }

                resultTv?.text = String.format(getString(R.string.message_found_text), count)
            }
            else{
                resultTv?.setText(R.string.no_message_text)
            }
        }
    }

    private fun checkForPermissions() {
        if(ContextCompat.checkSelfPermission(requireContext(), READ_SMS) == PackageManager.PERMISSION_GRANTED){
            getMessages()
        }
        else{
            requestPermission()
        }
    }

    private fun requestPermission() {
        val permReqLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it) {
                getMessages()
            }
        }

        permReqLauncher.launch(READ_SMS)
    }

    private fun getMessages(){
        GlobalScope.launch(Dispatchers.Main) {
            viewModel?.readMessages()?.observeForever {
                if(!it.isNullOrEmpty()){
                    messageList = it as ArrayList<SMS>
                    Log.d(TAG, "getMessages: $messageList")
                }
            }
        }
    }
}