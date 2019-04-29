import * as ffi from 'ffi'

export type lib = 'libnullpay'
export type initType = 'nullpay_init'

interface IFFIEntryPointsLibNull {
  nullpay_init: () => number
}

const libnullpayConfig: { [Key in keyof IFFIEntryPointsLibNull]: any } = {
  nullpay_init: ['int', []],
}

export class PaymentRuntime {
  public ffi: IFFIEntryPointsLibNull

  constructor() {
    this.ffi = ffi.Library(
      'libnullpay',
      libnullpayConfig,
    )
  }

  public async initPayment() {
    return this.ffi['nullpay_init']()
  }
}