namespace PcRemoteServer.Models
{
    public class RemoteCommand
    {
        public RemoteAction Action { get; set; }
    }
    public class MouseMoveCommand : RemoteCommand
    {
        public int Dx { get; set; }
        public int Dy { get; set; }
    }
}
