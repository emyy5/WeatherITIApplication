//package com.eman.weatherproject.database.room
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.filters.SmallTest
//import com.eman.weatherproject.database.model.WeatherAddress
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runBlockingTest
//import org.hamcrest.CoreMatchers
//import org.hamcrest.CoreMatchers.`is`
//import org.hamcrest.MatcherAssert
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.collection.IsEmptyCollection
//import org.hamcrest.core.Is
//import org.hamcrest.core.IsNull
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class)
//@SmallTest
//
//
//class FavouriteDaoTest {
//var instanceExecuteRule= InstantTaskExecutorRule()
//    lateinit var room: WeatherDb
//    lateinit var dao: FavouriteDao
//
//    @Before
//    fun setUp() {
//
//        room = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(), WeatherDb::class.java
//        ).allowMainThreadQueries().build()
//        dao = room.addressesDao()
//
//    }
//
//    @After
//    fun tearDown() = room.close()
//@Test
//    fun myAllAddress() {
//        val data1= WeatherAddress(address = "Ismailia", lat = 32.00, lon = 32.00)
//        val data2= WeatherAddress(address = "Ismailia", lat = 32.00, lon = 32.00)
//        val data3= WeatherAddress(address = "Ismailia", lat = 32.00, lon = 32.00)
//    dao.insertFavAddress(data1)
//    dao.insertFavAddress(data2)
//    dao.insertFavAddress(data3)
//    //when
//val result= dao.myAllAddress()
//    //Then
//    MatcherAssert.assertThat(result.value, Is.`is`(3))
//
//}
//    @Test
//    fun insertFavAddress_insertItem_returnItem() =
//        runBlockingTest {
//
//              val data1 = WeatherAddress(address = "Ismailia", lat = 32.00, lon = 32.00)
//            //when
//             dao.insertFavAddress(data1)
//
//            //Then
//             val result = dao.myAllAddress()
//            MatcherAssert.assertThat(result, IsNull.notNullValue())
//
//        }
//
//    @Test
//    fun deleteFavAddress_deleteItem() = runBlockingTest {
//        val data1 = WeatherAddress(lat = 240.55, lon = 24.22, address = "hghmm")
//        dao.insertFavAddress(data1)
//        val outComeData=dao.myAllAddress()
//
//        //when
//        dao.deleteFavAddress(outComeData)
//        //Then
//        val result= dao.myAllAddress()
//        assertThat(result, IsEmptyCollection.empty())
//        assertThat(result.value, `is`(3))
//
//
//    }
//}