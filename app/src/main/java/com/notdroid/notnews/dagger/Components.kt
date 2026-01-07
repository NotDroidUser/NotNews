package com.notdroid.notnews.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NewsModule::class])
interface Components {

  @Component.Builder
  interface Builder{
    @BindsInstance
    fun context(context:Context):Builder
    fun build():Components
  }
}