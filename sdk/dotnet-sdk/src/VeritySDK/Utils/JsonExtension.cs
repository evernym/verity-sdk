using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Json;
using System.Runtime.CompilerServices;
using System.Text;

namespace VeritySDK
{
    public static class JsonExtension
    {
        public static JsonValue Get(this JsonObject json, string key)
        {
            JsonValue value = null;
            json.TryGetValue(key, out value);
            return value;
        }
    }
}
