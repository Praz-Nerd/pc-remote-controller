using PcRemoteServer.Models;
using PcRemoteServer.Services;
using System.Net.WebSockets;
using System.Text.Json;

var builder = WebApplication.CreateBuilder(args);

//services
builder.Services.AddSingleton<IInputService, WindowsInputService>();
var app = builder.Build();

app.UseWebSockets();

var jsonOptions = new JsonSerializerOptions
{
    PropertyNameCaseInsensitive = true,
    Converters = { new System.Text.Json.Serialization.JsonStringEnumConverter(JsonNamingPolicy.SnakeCaseLower) }
};

app.MapGet("/", () => "PC Remote Server is running. Connect via WebSocket at /ws.");

app.Map("/ws", async (HttpContext context, IInputService inputService) =>
{
    if (!context.WebSockets.IsWebSocketRequest)
    {
        context.Response.StatusCode = StatusCodes.Status400BadRequest;
        return;
    }

    using var webSocket = await context.WebSockets.AcceptWebSocketAsync();
    var buffer = new byte[1024 * 4];

    try
    {
        while (webSocket.State == WebSocketState.Open)
        {
            var result = await webSocket.ReceiveAsync(new ArraySegment<byte>(buffer), CancellationToken.None);

            if (result.MessageType == WebSocketMessageType.Close)
            {
                await webSocket.CloseAsync(result.CloseStatus!.Value, result.CloseStatusDescription, CancellationToken.None);
                break;
            }

            using var document = JsonDocument.Parse(buffer.AsMemory(0, result.Count));
            var command = DeserializeCommand(document.RootElement.GetRawText(), jsonOptions);

            if (command != null)
            {
                ExecuteCommand(command, inputService);
            }
        }
    }
    catch (Exception ex)
    {
        Console.WriteLine($"Socket error: {ex.Message}");
    }
});

static RemoteCommand? DeserializeCommand(string json, JsonSerializerOptions options)
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

static void ExecuteCommand(RemoteCommand command, IInputService inputService)
{
    switch (command.Action)
    {
        case RemoteAction.MouseMove:
            inputService.MoveMouse((MouseMoveCommand)command);
            break;
        case RemoteAction.LeftClick:
            inputService.LeftClick();
            break;
        case RemoteAction.RightClick:
            inputService.RightClick();
            break;
        case RemoteAction.VolumeUp:
            inputService.PressKey(KeyCode.VolumeUp);
            break;
        case RemoteAction.VolumeDown:
            inputService.PressKey(KeyCode.VolumeDown);
            break;
        case RemoteAction.Mute:
            inputService.PressKey(KeyCode.Mute);
            break;
    }
}

app.Run();
