package com.dave.spellchecker.di

import com.dave.spellchecker.api.SpellCheckerAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideSpellCheckApi(retrofit: Retrofit): SpellCheckerAPI = retrofit.create(SpellCheckerAPI::class.java)
}
