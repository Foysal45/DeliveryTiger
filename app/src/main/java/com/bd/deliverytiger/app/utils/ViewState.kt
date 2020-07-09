package com.bd.deliverytiger.app.utils

sealed class ViewState {
    object NONE : ViewState()
    data class ShowMessage(val message: String?, val type: Int = 0) : ViewState()
    data class KeyboardState(val isShow: Boolean = false) : ViewState()
    data class ProgressState(val isShow: Boolean = false, val type: Int = 0) : ViewState()
    data class NextState(val type: Int = 0) : ViewState()
    data class EmptyViewState(val type: Int = 0) : ViewState()
}