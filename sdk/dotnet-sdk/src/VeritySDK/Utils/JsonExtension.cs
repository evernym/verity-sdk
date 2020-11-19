using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Json;
using System.Runtime.CompilerServices;
using System.Text;

namespace VeritySDK.Utils
{
    /// <summary>
    ///  Extension for safe get values from JSON
    /// </summary>
    public static class JsonExtension
    {
        /// <summary>
        /// Get values
        /// </summary>
        /// <param name="json">JSON object</param>
        /// <param name="key">Key name</param>
        /// <returns>JSON value or null</returns>
        public static JsonValue GetValue(this JsonObject json, string key)
        {
            JsonValue value = null;
            json.TryGetValue(key, out value);
            return value;
        }

        /// <summary>
        /// Get JSON value by key as JsonObject
        /// </summary>
        /// <param name="json">This object</param>
        /// <param name="key">Key</param>
        /// <returns></returns>
        public static JsonObject getAsJsonObject(this JsonObject json, string key)
        {
            JsonValue value = null;
            json.TryGetValue(key, out value);
            return value as JsonObject;
        }

        /// <summary>
        /// Get JSON value by key as JsonArray
        /// </summary>
        /// <param name="json">This object</param>
        /// <param name="key">Key</param>
        /// <returns></returns>
        public static JsonArray getAsJsonArray(this JsonObject json, string key)
        {
            JsonValue value = null;
            json.TryGetValue(key, out value);
            return value as JsonArray;
        }

        /// <summary>
        /// Get and return JSON value by key without quoted ""
        /// </summary>
        /// <param name="json">This object</param>
        /// <param name="key">Key</param>
        /// <returns></returns>
        public static string getAsString(this JsonObject json, string key)
        {
            JsonValue value = null;
            try
            {
                json.TryGetValue(key, out value);
                return value.ToString().Trim('"');
            }
            catch
            {
                return null;
            }
        }

        /// <summary>
        /// Get JSON value by key as Boolean
        /// </summary>
        /// <param name="json">This object</param>
        /// <param name="key">Key</param>
        /// <returns></returns>
        public static bool? getAsBoolean(this JsonObject json, string key)
        {
            try
            {
                return bool.Parse(json.getAsString(key));
            }
            catch
            {
                return null;
            }
        }

        /// <summary>
        /// Get JSON value by key as Integer
        /// </summary>
        /// <param name="json">This object</param>
        /// <param name="key">Key</param>
        /// <returns></returns>
        public static int? getAsInteger(this JsonObject json, string key)
        {
            try
            {
                return int.Parse(json.getAsString(key));
            }
            catch
            {
                return null;
            }
        }
        


    }
}
