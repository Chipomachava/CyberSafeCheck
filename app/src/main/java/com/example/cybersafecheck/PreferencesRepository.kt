package com.example.cybersafecheck

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository private constructor(private val context: Context) {

    val historySearchQuery: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[HISTORY_SEARCH_QUERY_KEY] ?: ""
    }

    suspend fun setHistorySearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            if (query.isEmpty()) {
                preferences.remove(HISTORY_SEARCH_QUERY_KEY)
            } else {
                preferences[HISTORY_SEARCH_QUERY_KEY] = query
            }
        }
    }

    companion object {
        private val HISTORY_SEARCH_QUERY_KEY = stringPreferencesKey("history_search_query")
        private var INSTANCE: PreferencesRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PreferencesRepository(context.applicationContext)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException("PreferencesRepository must be initialized")
        }
    }
}