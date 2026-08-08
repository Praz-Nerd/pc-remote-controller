package com.client.pcremote.models

open class RemoteCommand(val action: RemoteAction)

class LeftClickCommand : RemoteCommand(RemoteAction.LEFT_CLICK)
class RightClickCommand : RemoteCommand(RemoteAction.RIGHT_CLICK)

data class MouseMoveCommand(
    val dx: Int,
    val dy: Int
) : RemoteCommand(RemoteAction.MOUSE_MOVE)