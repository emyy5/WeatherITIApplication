package com.eman.weatherproject.features.alert.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.work.Data
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.eman.weatherproject.database.model.AlertData
import com.eman.weatherproject.database.room.LocalSource
import com.eman.weatherproject.R
import com.eman.weatherproject.RemoteSource
import com.eman.weatherproject.WeatherWorker
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.features.alert.viewmodel.AlertViewModel
import com.eman.weatherproject.features.alert.viewmodel.AlertViewModelFactory
import com.eman.weatherproject.databinding.FragmentAlertBinding
import com.eman.weatherproject.database.repository.Repository
import com.eman.weatherproject.database.room.OnClickAlertInterface
import com.eman.weatherproject.utilities.Converters
import com.eman.weatherproject.utilities.NOTIFICATION_ID
import com.eman.weatherproject.utilities.NOTIFICATION_WORK
import com.eman.weatherproject.utilities.SHARED_PREFERENCES
import java.util.*
import java.util.concurrent.TimeUnit


class AlertFragment : Fragment() , OnClickAlertInterface {


    private lateinit var binding:FragmentAlertBinding
    private lateinit var navController: NavController
    private lateinit var addAlert: Dialog
    private lateinit var datePickerDialog: Dialog
    private lateinit var timePickerDialog: Dialog
    private lateinit var newAlert: AlertData
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertLayoutManager: LinearLayoutManager
    private var settings: Settings? = null
    var alertFromDate: Date? = Date()
    var alertToDate: Date? = Date()
    var notifyType:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAlertBinding.bind(view)
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)

        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(requireActivity()),
                requireContext(),
                requireContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)))
        alertViewModel = ViewModelProvider(this,alertViewModelFactory).get(AlertViewModel::class.java)

        settings = alertViewModel.getStoredSettings()

        setupRecycler()

        alertViewModel.getAllAlertsInVM().observe(viewLifecycleOwner){
            if(it != null) {
                alertAdapter.setAlertsList(it)
            }
            alertAdapter.notifyDataSetChanged()
        }

        addAlert = Dialog(requireContext())
        addAlert.setContentView(R.layout.alert_dialog)
        addAlert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var fromLinear: LinearLayout = addAlert.findViewById(R.id.linearAlertFrom)
        var fromDatatxt: TextView = addAlert.findViewById(R.id.fromDateDialog)
        var fromTimetxt: TextView = addAlert.findViewById(R.id.fromTimeDialog)

        var toLinear: LinearLayout = addAlert.findViewById(R.id.linearAlertTo)
        var toDatatxt: TextView = addAlert.findViewById(R.id.toDateDialog)
        var toTimetxt: TextView = addAlert.findViewById(R.id.toTimeDialog)

        var saveBtn: Button = addAlert.findViewById(R.id.addAlertBtn)
        var notification: RadioButton = addAlert.findViewById(R.id.notificationForAlert)
        var alarm: RadioButton = addAlert.findViewById(R.id.alarmForAlert)

        notification.isChecked = true

        notification.setOnClickListener{ notifyType = true }

        alarm.setOnClickListener{notifyType = false}

        binding.floatingAddAlert.setOnClickListener{
            addAlert.show()
            fromLinear.setOnClickListener{

                datePickerDialog = Dialog(requireContext())
                datePickerDialog.setContentView(R.layout.time_picker)
                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                var datePicker: DatePicker = datePickerDialog.findViewById(R.id.datePicker)
                var dateOk: Button = datePickerDialog.findViewById(R.id.dateOk)
                var dateCancel: Button = datePickerDialog.findViewById(R.id.dateCancel)

                datePickerDialog.show()

                dateOk.setOnClickListener {
                    var fDay = datePicker.dayOfMonth
                    var fMonth = datePicker.month
                    var fYear = datePicker.year

                    timePickerDialog = Dialog(requireContext())
                    timePickerDialog.setContentView(R.layout.time_picker)
                    timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    var timePicker: TimePicker = timePickerDialog.findViewById(R.id.timePicker)
                    var timeOk: Button = timePickerDialog.findViewById(R.id.timeOk)
                    var timeCancel: Button = timePickerDialog.findViewById(R.id.timeCancel)

                    timePickerDialog.show()

                    timeOk.setOnClickListener {
                        var fHour = timePicker.hour
                        var fMinute = timePicker.minute

                        fromDatatxt.text = Converters.getDateFromInt(fDay,fMonth,fYear)
                        fromTimetxt.text = "$fHour:$fMinute"

                        alertFromDate = Date(fYear,fMonth,fDay,fHour,fMinute)

                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }

                    timeCancel.setOnClickListener {
                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }

                }
                dateCancel.setOnClickListener { datePickerDialog.dismiss() }

            }
            toLinear.setOnClickListener{

                datePickerDialog = Dialog(requireContext())
                datePickerDialog.setContentView(R.layout.time_picker)
                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                var datePicker: DatePicker = datePickerDialog.findViewById(R.id.datePicker)
                var dateOk: Button = datePickerDialog.findViewById(R.id.dateOk)
                var dateCancel: Button = datePickerDialog.findViewById(R.id.dateCancel)

                datePickerDialog.show()

                dateOk.setOnClickListener {
                    var tDay = datePicker.dayOfMonth
                    var tMonth = datePicker.month
                    var tYear = datePicker.year

                    timePickerDialog = Dialog(requireContext())
                    timePickerDialog.setContentView(R.layout.time_picker)
                    timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    var timePicker: TimePicker = timePickerDialog.findViewById(R.id.timePicker)
                    var timeOk: Button = timePickerDialog.findViewById(R.id.timeOk)
                    var timeCancel: Button = timePickerDialog.findViewById(R.id.timeCancel)

                    timePickerDialog.show()

                    timeOk.setOnClickListener {
                        var tHour = timePicker.hour
                        var tMinute = timePicker.minute

                        toDatatxt.text = Converters.getDateFromInt(tDay,tMonth,tYear)
                        toTimetxt.text = "$tHour:$tMinute"

                        alertToDate = Date(tYear,tMonth,tDay,tHour,tMinute)

                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }

                    timeCancel.setOnClickListener {
                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }

                }
                dateCancel.setOnClickListener { datePickerDialog.dismiss()}

            }
            saveBtn.setOnClickListener {
                if(settings?.notification as Boolean) {

                    if (alertFromDate != null && alertToDate != null) {
                        newAlert = AlertData(alertFromDate as Date, alertToDate as Date, notifyType)

                        val customCalendar = Calendar.getInstance()
                        customCalendar.set(
                            alertFromDate!!.year,
                            alertFromDate!!.month,
                            alertFromDate!!.date,
                            alertFromDate!!.hours,
                            alertFromDate!!.minutes,
                            0
                        )
                        val customTime = customCalendar.timeInMillis
                        val currentTime = System.currentTimeMillis()
                        if (customTime > currentTime) {
                            val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                            val delay = customTime - currentTime
                            scheduleNotification(delay, data)
                        }
                        alertViewModel.addAlertInVM(newAlert)
                    }
                }
                else{
                    val dialogBuilder = AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(getString(R.string.sorry))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                            dialog.cancel()
                        }
                    val alert = dialogBuilder.create()
                    alert.show()
                }
                addAlert.dismiss()

            }
        }
    }

    fun setupRecycler(){
        alertAdapter = AlertAdapter(requireContext(), emptyList(),this)
        alertLayoutManager = LinearLayoutManager(requireContext())
        binding.alertRecycler.adapter = alertAdapter
        binding.alertRecycler.layoutManager = alertLayoutManager
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(WeatherWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(requireContext())
        instanceWorkManager.beginUniqueWork(NOTIFICATION_WORK,
            ExistingWorkPolicy.APPEND, notificationWork).enqueue()
    }

    override fun onRemoveAlertBtnClick(myAlert: AlertData) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(getString(R.string.deleteMsg))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.delete)) { dialog, id ->
                alertViewModel.removeAlertInVM(myAlert)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.cancel)){ dialog, id -> dialog.cancel()}
        val alert = dialogBuilder.create()
        alert.show()
    }
}