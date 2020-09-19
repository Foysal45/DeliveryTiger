package com.bd.deliverytiger.app.di

import com.bd.deliverytiger.app.api.RetrofitUtils.createCache
import com.bd.deliverytiger.app.api.RetrofitUtils.createOkHttpClient
import com.bd.deliverytiger.app.api.RetrofitUtils.getGson
import com.bd.deliverytiger.app.api.RetrofitUtils.retrofitInstance
import com.bd.deliverytiger.app.api.endpoint.*
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.add_order.AddOrderViewModel
import com.bd.deliverytiger.app.ui.add_order.AddProductViewModel
import com.bd.deliverytiger.app.ui.add_order.OrderSuccessViewModel
import com.bd.deliverytiger.app.ui.dashboard.DashboardViewModel
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementViewModel
import com.bd.deliverytiger.app.ui.payment_statement.details.PaymentStatementDetailViewModel
import com.bd.deliverytiger.app.ui.profile.ProfileViewModel
import com.bd.deliverytiger.app.ui.service_bill_pay.ServiceBillViewModel
import com.bd.deliverytiger.app.utils.AppConstant
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single { getGson() }
    single { createCache(get()) }
    single { createOkHttpClient(get(), get()) }

    single(named("adcore")) { retrofitInstance(AppConstant.BASE_URL_ADCORE, get(), get()) }
    single(named("adm")) { retrofitInstance(AppConstant.BASE_URL_ADM, get(), get()) }
    single(named("api")) { retrofitInstance(AppConstant.BASE_URL_API, get(), get()) }
    single(named("bridge")) { retrofitInstance(AppConstant.BASE_URL_BRIDGE, get(), get()) }
    single(named("lambda")) { retrofitInstance(AppConstant.BASE_URL_LAMBDA, get(), get()) }

    single { ApiInterfaceADM(get(named("adm"))) }
    single { ApiInterfaceCore(get(named("adcore"))) }
    single { ApiInterfaceAPI(get(named("api"))) }
    single { ApiInterfaceBRIDGE(get(named("bridge"))) }
    single { ApiInterfaceLambda(get(named("lambda"))) }

    single { AppRepository(get(), get(), get(), get(), get()) }

    single { HomeViewModel(get()) }

    viewModel { DashboardViewModel(get()) }
    viewModel { AddOrderViewModel(get()) }
    viewModel { ServiceBillViewModel(get()) }
    viewModel { PaymentStatementViewModel(get()) }
    viewModel { PaymentStatementDetailViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { OrderSuccessViewModel(get()) }
    viewModel { AddProductViewModel(get()) }

}