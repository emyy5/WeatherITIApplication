package com.eman.weatherproject.database.room

import com.eman.weatherproject.database.model.AlertData

interface OnClickAlertInterface {

        fun onRemoveAlertBtnClick(alert: AlertData){
    }
}