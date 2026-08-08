using PcRemoteServer.Models;

namespace PcRemoteServer.Services
{
    public interface IInputService
    {
        void MoveMouse(MouseMoveCommand command);
        void LeftClick();
        void RightClick();
        void PressKey(KeyCode keyCode);
    }
}
