package com.ishanvohra.cribtaskapp.ui.fragment

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra.cribtaskapp.models.SMS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class MainFragmentViewModel() : ViewModel() {

    var context: Context? = null

    private val TAG = javaClass.simpleName

    suspend fun readMessages() : LiveData<MutableList<SMS>>{
        val mutableLiveData = MutableLiveData<MutableList<SMS>>()

        withContext(Dispatchers.Default) {
            val list: ArrayList<SMS> = ArrayList()
            val uriSMSURI: Uri = Uri.parse("content://sms/inbox")
            val cur: Cursor? = context!!.contentResolver.query(uriSMSURI, null, null, null, null)

            while (cur != null && cur.moveToNext()) {
                val address: String = cur.getString(cur.getColumnIndex("address"))
                val body: String = cur.getString(cur.getColumnIndexOrThrow("body"))
                val dateString = cur.getString(cur.getColumnIndexOrThrow("date_sent"))
                val date = Date()
                date.time = dateString.toLong()

                list.add(SMS(address,body, date))
            }

            if (cur != null) {
                cur.close()
            }

            mutableLiveData.postValue(list)
        }

        return mutableLiveData
    }

}