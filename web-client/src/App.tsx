import React, { Component } from 'react'
import { VeritySDK } from 'verity-browser-sdk'

/**
 * Verity-SDK enroll usage demo.
 *
 */
class AppMain extends Component {

  private url = 'http://localhost:8080/'
  private VSDK: VeritySDK = new VeritySDK(this.url)
  private eventSource: EventSource | undefined = undefined

  public async componentDidMount() {

    /**
     *
     * Create an event stream so that the verity agent has a direct line to the application in order
     * to send async messages to. VeritySDK provides a public method to handle decryption and message
     * handling of the message.
     */
    await this.VSDK.Ready
    console.log('VSDK ready, setting up SSE')
    this.eventSource = new EventSource('http://localhost:8080/sse-handshake')
    this.eventSource.addEventListener('message', async (e: MessageEvent) => {
      console.log('New SSE Message, passing to Verity SDK: ', e.data)
      this.VSDK.handleInboundMessage(e, (data: any) => {
        console.log('SDK message callback: ', data)
      })
    })
  }

  public render() {
    return (
      <button onClick={this.onHandleReq}>ENROLL</button>
    )
  }

  private onHandleReq = async () => {
    await this.VSDK.newEnrollment('000000000', 'I AM A CRED DEF ID', [ { name: 'SSM', value: '111-22-3333' } ])
  }
}

export default AppMain
