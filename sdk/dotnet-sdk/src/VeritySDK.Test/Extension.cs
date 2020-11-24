using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VeritySDK.Test
{
    public static class DictionaryExtension
    {
        public static bool EquivalentTo(this IDictionary<string, string> thisItems, IDictionary<string, string> otherItems)
        {
            if (thisItems.Count != otherItems.Count)
            {
                return false;
            }
            var thisKeys = thisItems.Keys;
            foreach (var key in thisKeys)
            {
                if (!(otherItems.TryGetValue(key, out var value) &&
                      string.Equals(thisItems[key], value, StringComparison.OrdinalIgnoreCase)))
                {
                    return false;
                }
            }
            return true;
        }

        public static bool EquivalentTo(this ICollection<string> thisItems, ICollection<string> otherItems)
        {
            if (thisItems.Count != otherItems.Count)
            {
                return false;
            }

            thisItems = thisItems.OrderBy(x => x).ToList();
            otherItems = otherItems.OrderBy(x => x).ToList();

            for (int i = 0; i < thisItems.Count; i++)
                if (!string.Equals(thisItems.ElementAt(i), otherItems.ElementAt(i), StringComparison.OrdinalIgnoreCase))
                    return false;

            return true;
        }


    }
}
