using System;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using System.Text;

namespace VeritySDK
{
    /**
     * Utilities for creating JSON objects
     */
    public class JsonUtil
    {
        private JsonUtil() { }

        /**
         * Builds a JSONArray from an array of object that implement AsJsonObject interface
         * @param items an array of object that can be converted to an JSONObject
         * @return a JSON array
         */
        public static JsonArray makeArray(AsJsonObject[] items)
        {
            return buildArray(items.AsEnumerable());
        }

        /**
         * Builds a JSONArray from a list of object that implement AsJsonObject interface
         * @param items a List of object that can be converted to an JSONObject
         * @return a JSON array
         */
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
