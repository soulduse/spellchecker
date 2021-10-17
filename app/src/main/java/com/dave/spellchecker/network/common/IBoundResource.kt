package com.dave.spellchecker.network.common

import androidx.lifecycle.LiveData

interface IBoundResource<TResourceData : Any> {
    fun getLiveData(): LiveData<Resource<TResourceData>>
    suspend fun getAsync(): Resource<TResourceData>
}
