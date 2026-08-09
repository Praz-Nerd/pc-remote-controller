using PcRemoteServer.Models;
using PcRemoteServer.Services;
using System.Net.WebSockets;
using System.Text.Json;

var builder = WebApplication.CreateBuilder(args);

//services
builder.Services.AddSingleton<IInputService, WindowsInputService>();
builder.Services.AddSingleton(new IpAllowlistStore(5));
builder.Services.AddHostedService<UdpListenerService>();
var app = builder.Build();

var jsonOptions = new JsonSerializerOptions
{
    PropertyNameCaseInsensitive = true,
    Converters = { new System.Text.Json.Serialization.JsonStringEnumConverter(JsonNamingPolicy.SnakeCaseLower) }
};

app.MapGet("/", () => "PC Remote Server is running.");

app.MapPost("/register", (IpAllowlistStore ipStore, HttpContext context) =>
{
    var ipAddress = context.Connection.RemoteIpAddress?.ToString();
    if (ipStore.RegisterIp(ipAddress))
        return Results.Ok("IP registered successfully.");

    if (ipStore.IsIpAllowed(ipAddress))
        return Results.Ok("IP already registered.");

    return Results.BadRequest("Could not register IP.");
});

app.MapPost("/refresh", (IpAllowlistStore ipStore, HttpContext context) =>
{
    if (ipStore.UpdateEntry(context.Connection.RemoteIpAddress?.ToString()))
        return Results.Ok("IP refreshed successfully.");
    return Results.BadRequest("Could not refresh IP.");
});

app.Run();
