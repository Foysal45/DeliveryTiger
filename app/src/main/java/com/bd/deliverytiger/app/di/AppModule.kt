package com.bd.deliverytiger.app.di

import com.bd.deliverytiger.app.api.RetrofitUtils.createCache
import com.bd.deliverytiger.app.api.RetrofitUtils.createOkHttpClient
import com.bd.deliverytiger.app.api.RetrofitUtils.getGson
import com.bd.deliverytiger.app.api.RetrofitUtils.retrofitInstance
import com.bd.deliverytiger.app.api.endpoint.*
import com.bd.deliverytiger.app.database.AppDatabase
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.add_order.AddOrderViewModel
import com.bd.deliverytiger.app.ui.add_order.AddProductViewModel
import com.bd.deliverytiger.app.ui.add_order.OrderSuccessViewModel
import com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet.ServiceSelectionBottomSheetViewModel
import com.bd.deliverytiger.app.ui.all_orders.AllOrderViewModel
import com.bd.deliverytiger.app.ui.all_orders.order_edit.OrderInfoEditViewModel
import com.bd.deliverytiger.app.ui.balance_load.BalanceLoadViewModel
import com.bd.deliverytiger.app.ui.balance_load_history.BalanceLoadHistoryViewModel
import com.bd.deliverytiger.app.ui.bill_pay.ServiceBillViewModel
import com.bd.deliverytiger.app.ui.bill_pay_history.ServiceBillHistoryViewModel
import com.bd.deliverytiger.app.ui.charge_calculator.DeliveryChargeViewModel
import com.bd.deliverytiger.app.ui.chat.compose.ChatComposeViewModel
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionViewModel
import com.bd.deliverytiger.app.ui.collection_history.CollectionHistoryViewModel
import com.bd.deliverytiger.app.ui.collector_tracking.MapViewModel
import com.bd.deliverytiger.app.ui.complain.ComplainViewModel
import com.bd.deliverytiger.app.ui.complain.complain_history.ComplainHistoryViewModel
import com.bd.deliverytiger.app.ui.dashboard.DashboardViewModel
import com.bd.deliverytiger.app.ui.delivery_details.DeliveryDetailsViewModel
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.live.home.LiveHomeActivityViewModel
import com.bd.deliverytiger.app.ui.live.home.LiveHomeActivityViewModelNew
import com.bd.deliverytiger.app.ui.live.live_product_insert.LiveProductInsertViewModel
import com.bd.deliverytiger.app.ui.live.live_schedule.LiveScheduleViewModel
import com.bd.deliverytiger.app.ui.live.live_schedule_list.LiveScheduleListViewModel
import com.bd.deliverytiger.app.ui.live.live_schedule_product.LiveScheduleProductListViewModel
import com.bd.deliverytiger.app.ui.lead_management.LeadManagementViewModel
import com.bd.deliverytiger.app.ui.lead_management.customer_details_bottomsheet.CustomerDetailsViewModel
import com.bd.deliverytiger.app.ui.loan_survey.LoanSurveryViewModel
import com.bd.deliverytiger.app.ui.login.AuthViewModel
import com.bd.deliverytiger.app.ui.notification.NotificationViewModel
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingViewModel
import com.bd.deliverytiger.app.ui.payment_details.PaymentDetailsViewModel
import com.bd.deliverytiger.app.ui.payment_request.InstantPaymentUpdateViewModel
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementViewModel
import com.bd.deliverytiger.app.ui.payment_statement.details.PaymentStatementDetailViewModel
import com.bd.deliverytiger.app.ui.profile.ProfileViewModel
import com.bd.deliverytiger.app.ui.quick_order.QuickOrderRequestViewModel
import com.bd.deliverytiger.app.ui.referral.ReferralViewModel
import com.bd.deliverytiger.app.ui.return_statement.ReturnStatementViewModel
import com.bd.deliverytiger.app.ui.service_charge.ServiceChargeViewModel
import com.bd.deliverytiger.app.ui.share.SmsShareViewModel
import com.bd.deliverytiger.app.ui.splash.SplashViewModel
import com.bd.deliverytiger.app.ui.survey.SurveyViewModel
import com.bd.deliverytiger.app.ui.unpaid_cod.UnpaidCODViewModel
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
    single(named("merchant")) { retrofitInstance(AppConstant.BASE_URL_MERCHANT, get(), get()) }
    single(named("ana")) { retrofitInstance(AppConstant.BASE_URL_ANA, get(), get()) }
    single(named("bridge")) { retrofitInstance(AppConstant.BASE_URL_BRIDGE, get(), get()) }
    single(named("lambda")) { retrofitInstance(AppConstant.BASE_URL_LAMBDA, get(), get()) }
    single(named("bariKoi")) { retrofitInstance(AppConstant.BASE_BARI_KOI, get(), get()) }
    single(named("fcm")) { retrofitInstance(AppConstant.BASE_URL_FCM, get(), get()) }

    single { ApiInterfaceADM(get(named("adm"))) }
    single { ApiInterfaceCore(get(named("adcore"))) }
    single { ApiInterfaceAPI(get(named("api"))) }
    single { ApiInterfaceMerchant(get(named("merchant"))) }
    single { ApiInterfaceBRIDGE(get(named("bridge"))) }
    single { ApiInterfaceLambda(get(named("lambda"))) }
    single { ApiInterfaceBariKoi(get(named("bariKoi"))) }
    single { ApiInterfaceANA(get(named("ana"))) }
    single { ApiInterfaceFCM(get(named("fcm"))) }

    single { AppDatabase.invoke(get()) }

    single { AppRepository(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }


    single { HomeViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { AddOrderViewModel(get()) }
    viewModel { ServiceBillViewModel(get()) }
    viewModel { ServiceBillHistoryViewModel(get()) }
    viewModel { PaymentStatementViewModel(get()) }
    viewModel { PaymentStatementDetailViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { OrderSuccessViewModel(get()) }
    viewModel { AddProductViewModel(get()) }
    viewModel { OrderTrackingViewModel(get()) }
    viewModel { MapViewModel(get()) }
    viewModel { PaymentDetailsViewModel(get()) }
    viewModel { ComplainViewModel(get()) }
    viewModel { DeliveryChargeViewModel(get()) }
    viewModel { ServiceChargeViewModel(get()) }
    viewModel { CODCollectionViewModel(get()) }
    viewModel { BalanceLoadViewModel(get()) }
    viewModel { UnpaidCODViewModel(get()) }
    viewModel { ReferralViewModel(get()) }
    viewModel { CollectionHistoryViewModel(get()) }
    viewModel { ReturnStatementViewModel(get()) }
    viewModel { SurveyViewModel(get()) }
    viewModel { DeliveryDetailsViewModel(get()) }
    viewModel { InstantPaymentUpdateViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { ServiceSelectionBottomSheetViewModel(get()) }
    viewModel { AllOrderViewModel(get()) }
    viewModel { BalanceLoadHistoryViewModel(get()) }
    viewModel { ComplainHistoryViewModel(get()) }
    viewModel { OrderInfoEditViewModel(get()) }
    viewModel { QuickOrderRequestViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { LiveHomeActivityViewModel(get()) }
    viewModel { LiveHomeActivityViewModelNew(get()) }
    viewModel { LiveScheduleListViewModel(get()) }
    viewModel { LiveScheduleViewModel(get()) }
    viewModel { LiveProductInsertViewModel(get()) }
    viewModel { LiveScheduleProductListViewModel(get()) }
    viewModel { LeadManagementViewModel(get()) }
    viewModel { CustomerDetailsViewModel(get()) }
    viewModel { SmsShareViewModel(get()) }
    viewModel { ChatComposeViewModel(get()) }
    viewModel { LoanSurveryViewModel(get()) }

}