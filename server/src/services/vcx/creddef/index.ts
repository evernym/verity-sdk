import * as vcx from 'node-vcx-wrapper'

export function makeid(length: number = 15) {
    let text = ''
    const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'

    for (let i = 0; i < length; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length))
    }
    return text
  }

export async function generateCredentialDef(): Promise<{ id: string, handle: number }> {
    try {
        const schema = await vcx.Schema.create({
            data: {
                attrNames: ['SSN', 'Address'],
                name: makeid(),
                version: '1.0.0',
            },
            paymentHandle: 0,
            sourceId: makeid(),
        })

        const schema2 = await vcx.Schema.deserialize(await schema.serialize())

        const credData = {
            name: 'vcxDemoCred',
            paymentHandle: 0,
            revocationDetails: {},
            schemaId: schema2.schemaId,
            sourceId: schema2.sourceId,
        }

        const credentialDef = await vcx.CredentialDef.create(credData)
        const finalCredDef = await vcx.CredentialDef.deserialize(await credentialDef.serialize())
        const finalId = await finalCredDef.getCredDefId()

        console.log('finalID: ', finalId)

        return { handle: finalCredDef.handle, id: finalId }

    } catch (e) {
        throw new Error(e)
    }
}
