using System.Net.Sockets;
using System.Text.Json;
using System.Text.Json.Serialization;
using PcRemoteServer.Models;

namespace PcRemoteServer.Services;

public class UdpListenerService : BackgroundService
{
    private readonly IInputService _inputService;
    private readonly JsonSerializerOptions _jsonOptions;
    private readonly IpAllowlistStore _ipStore;

    // Inject our Windows Input Service
    public UdpListenerService(IInputService inputService, IpAllowlistStore ipStore)
    {
        _inputService = inputService;
        _ipStore = ipStore;
        _jsonOptions = new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true,
            Converters = { new JsonStringEnumConverter(JsonNamingPolicy.SnakeCaseLower) }
        };
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        using var udpServer = new UdpClient(5201);
        Console.WriteLine("UDP Background Service listening on port 5201...");

        // Run until the app is shut down
        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                // ReceiveAsync accepts the stopping token so it can close cleanly
                var receiveResult = await udpServer.ReceiveAsync(stoppingToken);
                var sourceIp = receiveResult.RemoteEndPoint.Address.ToString();

                // Check if the source IP is allowed
                if (!_ipStore.IsIpAllowed(sourceIp))
                {
                    Console.WriteLine($"Received packet from unallowed IP: {sourceIp}");
                    continue; // Ignore this packet
                }

                var buffer = receiveResult.Buffer;

                using var document = JsonDocument.Parse(buffer);
                var command = DeserializeCommand(document.RootElement.GetRawText(), _jsonOptions);

                if (command != null)
                {
                    ExecuteCommand(command);
                    _ipStore.UpdateEntry(sourceIp); // Refresh the IP entry on successful command execution
                }
            }
            catch (OperationCanceledException) { /* App is shutting down */ }
            catch (Exception) { /* Ignore bad packets and keep listening */ }
        }
    }

    private RemoteCommand? DeserializeCommand(string json, JsonSerializerOptions options)
    {
        try
        {
            var command = JsonSerializer.Deserialize<RemoteCommand>(json, options);

            if (command != null)
                switch (command.Action)
                {
                    case RemoteAction.MouseMove:
                        command = JsonSerializer.Deserialize<MouseMoveCommand>(json, options);
                        break;
                }

            return command;
        }
        catch (JsonException ex)
        {
            Console.WriteLine($"JSON deserialization error: {ex.Message}");
            return null;
        }
    }

    private void ExecuteCommand(RemoteCommand command)
    {
        switch (command.Action)
        {
            case RemoteAction.MouseMove:
                _ = Task.Run(() => _inputService.MoveMouse((MouseMoveCommand)command));
                break;
            case RemoteAction.LeftClick:
                _ = Task.Run(() => _inputService.LeftClick());
                break;
            case RemoteAction.PressAndHoldLeftClick:
                _ = Task.Run(() => _inputService.PressAndHoldLeftClick());
                break;
            case RemoteAction.ReleaseLeftClick:
                _ = Task.Run(() => _inputService.ReleaseLeftClick());
                break;
            case RemoteAction.PressAndHoldRightClick:
                _ = Task.Run(() => _inputService.PressAndHoldRightClick());
                break;
            case RemoteAction.ReleaseRightClick:
                _ = Task.Run(() => _inputService.ReleaseRightClick());
                break;
            case RemoteAction.RightClick:
                _ = Task.Run(() => _inputService.RightClick());
                break;
            case RemoteAction.VolumeUp:
                _ = Task.Run(() => _inputService.PressKey(KeyCode.VolumeUp));
                break;
            case RemoteAction.VolumeDown:
                _ = Task.Run(() => _inputService.PressKey(KeyCode.VolumeDown));
                break;
            case RemoteAction.Mute:
                _ = Task.Run(() => _inputService.PressKey(KeyCode.Mute));
                break;
        }
    }
}