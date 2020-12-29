package asd.android.movieslist.services

class ScreenStatus(var loadStatus:CurrentStatus = CurrentStatus.NORMAL){
    enum class CurrentStatus {
        LOADING,
        NORMAL,
        ERROR,
        EMPTY_STATE
    }
}