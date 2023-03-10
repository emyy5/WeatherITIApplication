package com.eman.weatherproject.database.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class AlertsDAOTest {

    lateinit var room: WeatherDb
    lateinit var dao: AlertsDAO

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {

        room = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), WeatherDb::class.java
        ).allowMainThreadQueries().build()
        dao = room.alertsDao()

    }

    @After
    fun closeDB()  = room.close()

    fun storedAllAlert() {
    }

    fun insertAlert() {
    }

    fun deleteAlert() {
    }
}