package com.bd.deliverytiger.app.di

import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.RetrofitSingletonAD
import com.bd.deliverytiger.app.api.RetrofitSingletonADM
import com.bd.deliverytiger.app.api.`interface`.ApiInterfaceADM
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.service_bill_pay.ServiceBillViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single(named("adm")) { RetrofitSingletonADM.getInstance(get()) }
    single(named("adcore")) { RetrofitSingleton.getInstance(get()) }
    single(named("api")) { RetrofitSingletonAD.getInstance(get()) }
    single { ApiInterfaceADM(get(named("adm"))) }
    single { AppRepository(get()) }

    viewModel { ServiceBillViewModel(get()) }

}