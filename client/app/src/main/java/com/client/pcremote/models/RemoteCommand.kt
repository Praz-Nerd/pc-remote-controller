package com.client.pcremote.models

open class RemoteCommand(val action: RemoteAction)
data class MouseMoveCommand(
    val dx: Int,
    val dy: Int
) : RemoteCommand(RemoteAction.MOUSE_MOVE)