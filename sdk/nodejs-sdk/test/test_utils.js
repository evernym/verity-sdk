"use strict";
const chai = require('chai')
const expect = chai.expect
const utils = require('../src/utils')

describe('utils', () => {
    describe('.packMessageForVerity', () => {
        it('should be able to pack and unpack messages', async () => {
            const message = {some: "message"}
            const bytes = await utils.packMessageForVerity(context, message)
            expect(await utils.unpackMessageFromVerity(bytes)).to.deep.equal(message)
        })
    })
})