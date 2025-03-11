package com.example.syncup.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://ommxnmwtiqzxmjhzvbgc.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9tbXhubXd0aXF6eG1qaHp2YmdjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzQ5Mjk5NzIsImV4cCI6MjA1MDUwNTk3Mn0.LnFQjQQNMICxMVMDedhuFSAZOzKwlIA-oOQHZVLXa-c"
        ) {
            install(Storage)
        }
    }


    @Provides
    @Singleton
    fun provideSupabaseStorage(client: SupabaseClient): Storage {
        return client.storage
    }

}
