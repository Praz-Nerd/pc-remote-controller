using System.Collections.Concurrent;

namespace PcRemoteServer.Services
{
    public class IpAllowlistStore
    {
        private readonly ConcurrentDictionary<string, DateTime> _allowlist = new();
        private int _timeoutMinutes;
        private int _ipCountLimit;

        public IpAllowlistStore(int timeoutMinutes, int ipCountLimit = 1)
        {
            _timeoutMinutes = timeoutMinutes;
            _ipCountLimit = ipCountLimit;
        }

        public bool RegisterIp(string? ipAddress)
        {
            bool result = false;
            if(ipAddress != null && CanAcceptEntries())
                result = _allowlist.TryAdd(ipAddress, DateTime.Now);
            return result;
        }

        public bool UpdateEntry(string? ipAddress)
        {
            bool result = false;
            if (ipAddress != null && _allowlist.ContainsKey(ipAddress))
            {
                var newTime = DateTime.Now;
                _allowlist[ipAddress] = newTime;
                result = newTime == _allowlist[ipAddress];
            }
            return result;
        }

        private bool CanAcceptEntries()
        {
            return _allowlist.Count < _ipCountLimit;
        }

        public bool IsIpAllowed(string ipAddress)
        {
            return _allowlist.ContainsKey(ipAddress) 
                && (DateTime.Now - _allowlist[ipAddress]).TotalMinutes < _timeoutMinutes;
        }
    }
}
