using System.Runtime.InteropServices;
using PcRemoteServer.Models;

namespace PcRemoteServer.Services
{
    public class WindowsInputService : IInputService
    {
        [DllImport("user32.dll", SetLastError = true)]
        private static extern uint SendInput(uint nInputs, INPUT[] pInputs, int cbSize);

        #region Win32 Structs and Enums
        [StructLayout(LayoutKind.Sequential)]
        private struct INPUT
        {
            public uint type;
            public InputUnion U;
        }

        [StructLayout(LayoutKind.Explicit)]
        private struct InputUnion
        {
            [FieldOffset(0)] public MOUSEINPUT mi;
            [FieldOffset(0)] public KEYBDINPUT ki;
        }

        [StructLayout(LayoutKind.Sequential)]
        private struct MOUSEINPUT
        {
            public int dx;
            public int dy;
            public uint mouseData;
            public uint dwFlags;
            public uint time;
            public IntPtr dwExtraInfo;
        }

        [StructLayout(LayoutKind.Sequential)]
        private struct KEYBDINPUT
        {
            public ushort wVk;
            public ushort wScan;
            public uint dwFlags;
            public uint time;
            public IntPtr dwExtraInfo;
        }

        private const int INPUT_MOUSE = 0;
        private const int INPUT_KEYBOARD = 1;

        private const uint MOUSEEVENTF_MOVE = 0x0001;
        private const uint MOUSEEVENTF_LEFTDOWN = 0x0002;
        private const uint MOUSEEVENTF_LEFTUP = 0x0004;
        private const uint MOUSEEVENTF_RIGHTDOWN = 0x0008;
        private const uint MOUSEEVENTF_RIGHTUP = 0x0010;

        private const uint KEYEVENTF_KEYUP = 0x0002;
        private const uint KEYEVENTF_EXTENDEDKEY = 0x0001;
        #endregion

        public void MoveMouse(MouseMoveCommand command)
        {
            SendMouseInput(command.Dx, command.Dy, MOUSEEVENTF_MOVE);
        }

        public void LeftClick()
        {
            PressAndHoldLeftClick();
            ReleaseLeftClick();
        }

        public void RightClick()
        {
            PressAndHoldRightClick();
            ReleaseRightClick();
        }

        public void PressAndHoldLeftClick() => SendMouseInput(0, 0, MOUSEEVENTF_LEFTDOWN);
        public void ReleaseLeftClick() => SendMouseInput(0, 0, MOUSEEVENTF_LEFTUP);
        public void PressAndHoldRightClick() => SendMouseInput(0, 0, MOUSEEVENTF_RIGHTDOWN);
        public void ReleaseRightClick() => SendMouseInput(0, 0, MOUSEEVENTF_RIGHTUP);

        public void PressKey(KeyCode keyCode)
        {
            PressAndHoldKey(keyCode);
            ReleaseKey(keyCode);
        }

        public void PressAndHoldKey(KeyCode keyCode) => SendKeyboardInput((ushort)keyCode, 0);
        public void ReleaseKey(KeyCode keyCode) => SendKeyboardInput((ushort)keyCode, KEYEVENTF_KEYUP);

        private void SendMouseInput(int dx, int dy, uint flags)
        {
            var inputs = new INPUT[]
            {
                new INPUT
                {
                    type = INPUT_MOUSE,
                    U = new InputUnion
                    {
                        mi = new MOUSEINPUT { dx = dx, dy = dy, dwFlags = flags }
                    }
                }
            };
            SendInput(1, inputs, Marshal.SizeOf(typeof(INPUT)));
        }

        private void SendKeyboardInput(ushort vkCode, uint flags)
        {
            // Check for extended keys (e.g. arrow keys, insert, delete, right ctrl/alt)
            if (IsExtendedKey(vkCode))
            {
                flags |= KEYEVENTF_EXTENDEDKEY;
            }

            var inputs = new INPUT[]
            {
                new INPUT
                {
                    type = INPUT_KEYBOARD,
                    U = new InputUnion
                    {
                        ki = new KEYBDINPUT { wVk = vkCode, dwFlags = flags }
                    }
                }
            };
            SendInput(1, inputs, Marshal.SizeOf(typeof(INPUT)));
        }

        private bool IsExtendedKey(ushort vk)
        {
            return vk switch
            {
                0x21 or 0x22 or 0x23 or 0x24 or 0x25 or 0x26 or 0x27 or 0x28 or 0x2D or 0x2E => true, // PageUp/Down, Home, End, Arrows, Ins, Del
                _ => false
            };
        }
    }
}