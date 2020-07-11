package com.bd.deliverytiger.app.di

import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.RetrofitSingletonAD
import com.bd.deliverytiger.app.api.RetrofitSingletonADM
import com.bd.deliverytiger.app.api.`interface`.ApiInterfaceADM
import com.bd.deliverytiger.app.api.`interface`.ApiInterfaceCore
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.add_order.AddOrderViewModel
import com.bd.deliverytiger.app.ui.payment_history.PaymentHistoryViewModel
import com.bd.deliverytiger.app.ui.payment_history.details.PaymentHistoryDetailViewModel
import com.bd.deliverytiger.app.ui.service_bill_pay.ServiceBillViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single(named("adm")) { RetrofitSingletonADM.getInstance(get()) }
    single(named("adcore")) { RetrofitSingleton.getInstance(get()) }
    single(named("api")) { RetrofitSingletonAD.getInstance(get()) }
    single { ApiInterfaceADM(get(named("adm"))) }
    single { ApiInterfaceCore(get(named("adcore"))) }
    single { AppRepository(get(), get()) }

    viewModel { AddOrderViewModel(get()) }
    viewModel { ServiceBillViewModel(get()) }
    viewModel { PaymentHistoryViewModel(get()) }
    viewModel { PaymentHistoryDetailViewModel(get()) }

}