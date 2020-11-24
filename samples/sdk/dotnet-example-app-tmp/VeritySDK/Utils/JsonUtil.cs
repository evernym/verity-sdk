using System;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using System.Text;

namespace VeritySDK.Utils
{
    /// <summary>
    /// Utilities for creating JSON objects
    /// </summary>
    public class JsonUtil
    {
        private JsonUtil() { }

        /// <summary>
        /// Builds a JsonArray from an array of object that implement AsJsonObject interface 
        /// </summary>
        /// <param name="items">items an array of object that can be converted to an JsonObject</param>
        /// <returns>a JSON array</returns>
        public static JsonArray makeArray(AsJsonObject[] items)
        {
            return buildArray(items.AsEnumerable());
        }

        /// <summary>
        /// Builds a JSONArray from a list of object that implement AsJsonObject interface 
        /// </summary>
        /// <param name="items">a List of object that can be converted to an JsonObject</param>
        /// <returns>a JSON array</returns>
        public static JsonArray makeArrayFromList(List<AsJsonObject> items)
        {
            return buildArray(items);
        }

        private static JsonArray buildArray(IEnumerable<AsJsonObject> items)
        {
            JsonArray rtn = new JsonArray();
            if (items != null)
            {
                foreach (var i in items)
                {
                    if (i != null)
                    {
                        rtn.Add(i.toJson());
                    }
                }
            }
            return rtn;
        }
    }
}
