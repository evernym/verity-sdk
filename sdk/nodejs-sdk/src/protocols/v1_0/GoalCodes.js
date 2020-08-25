'use strict'
/**
 * An enumeration of possible goals (reasons) for a relationship.
 */
class GoalCodes {
  /**
  * To issue a credential
  */
  static ISSUE_VC () { return new GoalCodes('issue-vc', 'To issue a credential') }

  /**
  * To request a proof
  */
  static REQUEST_PROOF () { return new GoalCodes('request-proof', 'To request a proof') }

  /**
  * To create an account with a service
  */
  static CREATE_ACCOUNT () { return new GoalCodes('create-account', 'To create an account with a service') }

  /**
  * To establish a peer-to-peer messaging relationship
  */
  static P2P_MESSAGING () { return new GoalCodes('p2p-messaging', 'To establish a peer-to-peer messaging relationship') }

  static get values () {
    return [
      this.ISSUE_VC(),
      this.REQUEST_PROOF,
      this.CREATE_ACCOUNT,
      this.P2P_MESSAGING
    ]
  }

  static fromString (name) {
    const value = this[name]
    if (value) return value

    throw new RangeError(`No instance of ${this.constructor.name} exists with the name ${name}.`)
  }

  constructor (code, goalName) {
    this.code = code
    this.goalName = goalName
    Object.freeze(this)
  }
}

module.exports = GoalCodes
