package com.client.pcremote.models

import com.google.gson.annotations.SerializedName

enum class RemoteAction {
    @SerializedName("mouse_move") MOUSE_MOVE,
    @SerializedName("left_click") LEFT_CLICK,
    @SerializedName("right_click") RIGHT_CLICK,
    @SerializedName("volume_up") VOLUME_UP,
    @SerializedName("volume_down") VOLUME_DOWN,
    @SerializedName("mute") MUTE
}