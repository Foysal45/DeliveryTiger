package com.bd.deliverytiger.app.di

import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.RetrofitSingletonADM
import com.bd.deliverytiger.app.api.RetrofitSingletonAPI
import com.bd.deliverytiger.app.api.endpoint.ApiInterfaceADM
import com.bd.deliverytiger.app.api.endpoint.ApiInterfaceAPI
import com.bd.deliverytiger.app.api.endpoint.ApiInterfaceCore
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.add_order.AddOrderViewModel
import com.bd.deliverytiger.app.ui.dashboard.DashboardViewModel
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementViewModel
import com.bd.deliverytiger.app.ui.payment_statement.details.PaymentStatementDetailViewModel
import com.bd.deliverytiger.app.ui.service_bill_pay.ServiceBillViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single(named("adm")) { RetrofitSingletonADM.getInstance(get()) }
    single(named("adcore")) { RetrofitSingleton.getInstance(get()) }
    single(named("api")) { RetrofitSingletonAPI.getInstance(get()) }
    single { ApiInterfaceADM(get(named("adm"))) }
    single { ApiInterfaceCore(get(named("adcore"))) }
    single { ApiInterfaceAPI(get(named("api"))) }
    single { AppRepository(get(), get(), get()) }

    viewModel { HomeViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { AddOrderViewModel(get()) }
    viewModel { ServiceBillViewModel(get()) }
    viewModel { PaymentStatementViewModel(get()) }
    viewModel { PaymentStatementDetailViewModel(get()) }

}