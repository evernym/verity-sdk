import Axios from 'axios'
import { KeyPair } from 'libsodium-wrappers'
import { PackUnpack } from 'pack-unpack'
import uuid from 'uuid'
import { makeid } from '../../utils/common.ts'

export type CB = (data: any) => void

export interface INewEnrollmentMessage extends IAgentMessage {
    '@type': 'vs.service/enroll/0.1/new-enrollment'
    '@id': string
    connectionDetail: {
        sourceId: string,
        phoneNo: string,
    }
    credentialData: {
        id: string,
        credDefId: string,
        credentialFields: ICredField[],
        price: number,
    }
}

export interface ICredField {
    name: string,
    value: any
}

export interface IAgentMessage {
    '@type': protocols
    '@id': string
    [key: string]: any
}

export type enrollProtocols =
| 'vs.service/enroll/0.1/new-enrollment'
| 'vs.service/enroll/0.1/problem-report'
| 'vs.service/enroll/0.1/status'

export type commonProtocols =
| 'vs.service/common/0.1/check-status'
| 'vs.service/common/0.1/invite-request'
| 'vs.service/common/0.1/invite-response'
| 'vs.service/common/0.1/problem-report'

export type protocols =
| commonProtocols
| enrollProtocols

/**
 *
 * Enroll agent interpreter
 *
 * Used to create a pairwise connection with a user using the verity-application agent.
 * After the pairwise relationshilp is establised the user is automatically issued a
 * verifiable credential with the publicly resolvable credential identifier required.
 */
export class Enroll {

    /**
     *
     * @param phoneNo phone number of the individual being connected with
     * @param credendialDefId pulic resolver for the credential definition you wish to use
     * @param credFields credential parameters for the issued credential
     * @param cbHandle used to pass update/completion/error messages to
     */
    public async newEnrollment(
        phoneNo: string,
        credendialDefId: string,
        credFields: ICredField[],
        APK: Uint8Array,
        keyPair: KeyPair,
        url: string,
        ) {

            const enrollment: INewEnrollmentMessage = {
                '@id': uuid(),
                '@type': 'vs.service/enroll/0.1/new-enrollment',
                'connectionDetail': {
                    phoneNo,
                    sourceId: `conn_${makeid()}`,
                },
                'credentialData': {
                    credDefId: credendialDefId,
                    credentialFields: credFields,
                    id: `cred_${makeid()}`,
                    price: 0,
                },
            }

            const pack = new PackUnpack()
            await pack.setup()
            const packedMsg = await pack.packMessage(JSON.stringify(enrollment), [APK], keyPair)

            console.log(packedMsg)

            Axios.post(`${url}msg`, { msg: packedMsg })
        }
}

// One or two packages, browser/backend
// Do we leave transport up to them, library doesnt handle transport?
// AUTHENTICATION??
// WASM, typescript?
