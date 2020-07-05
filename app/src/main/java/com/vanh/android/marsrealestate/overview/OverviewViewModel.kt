/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vanh.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanh.android.marsrealestate.network.MarsApi
import com.vanh.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.Response

enum class MarsApiStatus{LOADING,ERROR,DONE}
/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    private var _properties = MutableLiveData<List<MarsProperty>>()
    val properties:LiveData<List<MarsProperty>>
        get() = _properties
    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties() {
            // using coroutine
            coroutineScope.launch {
                val getPropertiesDeferred = MarsApi.retrofitService.getProperties()
                try{
                    _status.value = MarsApiStatus.LOADING
                    val listResult = getPropertiesDeferred.await()
                   // if(listResult.isNotEmpty()) _properties.value = listResult
                    _properties.value = listResult
                    _status.value = MarsApiStatus.DONE//"Success: ${listResult.size} Mars Property retrieved"
                }
                catch ( e:Exception) {
                    _status.value = MarsApiStatus.ERROR
                    _properties.value = ArrayList()}    //"Failure:  ${e.message}"}
            }

    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}

