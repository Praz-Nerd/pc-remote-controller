using PcRemoteServer.Models;

namespace PcRemoteServer.Services
{
    public interface IInputService
    {
        void MoveMouse(MouseMoveCommand command);
        void LeftClick();
        void PressAndHoldLeftClick();
        void ReleaseLeftClick();
        void RightClick();
        void PressAndHoldRightClick();
        void ReleaseRightClick();
        void PressKey(KeyCode keyCode);
        void PressAndHoldKey(KeyCode keyCode);
        void ReleaseKey(KeyCode keyCode);
    }
}
