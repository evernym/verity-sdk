'use strict'

/**
 * Utilities for design by contract. Allows for common pre-condition and post-condition checking.
 */
class DbcUtil {
  /**
   * Checks that given requirement is true, throws an Error if false
   * @param {Boolean} requirement - testable requirement
   * @param {String} msg - string that is used to build exception message
   */
  static require (requirement, msg) {
    if (!requirement) {
      throw new Error('requirement failed: ' + msg)
    }
  }

  /**
   * Checks and throws an Error if the given object is null
   * @param {Object} arg - any object that can be null
   * @param {String} argName - name of the argument being checked, used in exception message if null
   */
  static requireNotNull (arg, argName) {
    this.require(arg != null, 'required that ' + argName + ' must NOT be null')
  }

  /**
   * Checks and throws an Error if the given string is null or empty
   * @param {String} arg - String argument which needs to be checked
   * @param {String} argName - name of the argument being checked, used in exception message if null or empty
   */
  static requireStringNotNullOrEmpty (arg, argName) {
    this.require(arg != null && arg.trim(), 'required that ' + argName + ' must NOT be null or empty')
  }

  /**
   * Checks and throws an Error if the given array is null or contains null element
   * @param {Array} array - Array argument which needs to be checked
   * @param {String} argName - name of the argument being checked, used in exception message
   */
  static requireArrayNotContainNull (array, argName) {
    this.requireNotNull(array, argName)
    array.forEach(function (x) {
      DbcUtil.require(x != null, 'required that elements of ' + argName + ' must NOT be null')
    })
  }
}

module.exports = DbcUtil
