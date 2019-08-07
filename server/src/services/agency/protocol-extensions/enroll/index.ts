// // import * as vcx from 'node-vcx-wrapper'
// import { IAgentMessage } from '..'
// // import { makeid, generateCredentialDef } from '../../services/vcx/creddef'
// import { Response } from 'express-serve-static-core'
// import uuid = require('uuid')
// import { StateType } from 'node-vcx-wrapper'
// import { KeyPair } from 'libsodium-wrappers'
// import { PackUnpack } from 'pack-unpack'

// export type enrollProtocols =
// | 'did:sov:123456789abcdefghi1234;spec/enroll/0.1/new-enrollment'
// | 'did:sov:123456789abcdefghi1234;spec/enroll/0.1/problem-report'
// | 'did:sov:123456789abcdefghi1234;spec/enroll/0.1/status'

// export type commonProtocols =
// | 'did:sov:123456789abcdefghi1234;spec/common/0.1/check-status'
// | 'did:sov:123456789abcdefghi1234;spec/common/0.1/invite-request'
// | 'did:sov:123456789abcdefghi1234;spec/common/0.1/invite-response'
// | 'did:sov:123456789abcdefghi1234;spec/common/0.1/problem-report'

// export interface INewEnrollmentMessage extends IAgentMessage {
//     '@type': 'did:sov:123456789abcdefghi1234;spec/enroll/0.1/new-enrollment'
//     '@id': string
//     connectionDetail: {
//         sourceId: string,
//         phoneNo: string,
//     }
//     credentialData: {
//         id: string,
//         credDefId: string,
//         credentialFields: ICredField[],
//         price: number,
//     }
// }

// export interface IEnrollStatusMessage {
//         '@type': 'did:sov:123456789abcdefghi1234;spec/enroll/0.1/status'
//         '@id': string
//         '~thread': {
//             thid: string,
//             seqnum?: 3,
//         },
//         status: number,
//         message: string
// }

// export const status = [
//     'None',
//     'Invite has been sent to the user',
//     'Invite was accepted by the user',
//     'The offer was sent to the user',
//     'The offer was received',
//     'The credential was sent',
//     'The enroll process is complete',
//     'Error',
// ]

// export interface ICredField {
//     name: string
//     type: number
//     value: any
// }

// export async function generateStatusReport(
//     message: INewEnrollmentMessage, reportNo: StateType, responseHandle: Response,
//                                      keypair: KeyPair, APK: Uint8Array) {
//     const statusReport: IEnrollStatusMessage = {
//         '@type': 'did:sov:123456789abcdefghi1234;spec/enroll/0.1/status',
//         '@id': uuid(),
//         '~thread': {
//             thid: message['@id']
//         },
//         status: reportNo,
//         message: status[reportNo]
//     }

//     const pu = new PackUnpack()
//     await pu.Ready

//     const packedMsg = await pu.packMessage(JSON.stringify(statusReport), [APK], keypair)

//     responseHandle.write(`data: ${packedMsg}\n\n`)
// }

// export async function newEnrollment(
//     message: INewEnrollmentMessage, _responseHandle: Response, keypair: KeyPair, APK: Uint8Array) {

//     setTimeout(() => {
//         generateStatusReport(message, 1, _responseHandle, keypair, APK)
//         setTimeout(() => {
//             generateStatusReport(message, 2, _responseHandle, keypair, APK)
//             setTimeout(() => {
//                 generateStatusReport(message, 3, _responseHandle, keypair, APK)
//                 setTimeout(() => {
//                     generateStatusReport(message, 4, _responseHandle, keypair, APK)
//                     setTimeout(() => {
//                         generateStatusReport(message, 6, _responseHandle, keypair, APK)
//                     }, 3000)
//                 }, 4000)
//             }, 4000)
//         }, 3000)
//     }, 5000)

//     // console.log('connection created! ', connection)

//     // const dataVCX = message.credentialData.credentialFields.reduce(
//     //     (accum, item) => ({ ...accum, [item.name]: item.value }),
//     //     {},
//     //     )

//     // try {
//     //     const { handle } = await generateCredentialDef()
//     //     const issuerCred = await vcx.IssuerCredential.create({
//     //         attr: dataVCX,
//     //         credDefHandle: handle,
//     //         credentialName: 'DEFAULT CRED',
//     //         price: message.credentialData.price.toString(),
//     //         sourceId: makeid(),
//     //     })

//     //     await issuerCred.sendOffer(connection)
//     //     await issuerCred.updateState()
//     //     if (await issuerCred.getState() === 3) {
//     //         await issuerCred.sendCredential(connection)
//     //     }

//     //     console.log('issuer credential created! ', issuerCred)
//     // } catch (e) {
//     //     throw e
//     // }
// }
