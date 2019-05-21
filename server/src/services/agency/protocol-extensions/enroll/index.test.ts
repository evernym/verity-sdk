// import { INewEnrollmentMessage, newEnrollment } from '.'

// describe('enroll protocol', () => {

//     it('issues a new credential', async () => {

//         const credDefId = 'V4SGRU86Z58d6TV7PBUe6f:3:CL:1588:tag1'
//         // const handleNum = 1944178348

//         const message: INewEnrollmentMessage = {
//             '@id': '12345',
//             '@type': 'vs.service/enroll/0.1/new-enrollment',
//             'connectionDetail': {
//                 phoneNo: '8888888888',
//                 sourceId: 'CONN_TEST_ID',
//             },
//             'credentialData': {
//                 credDefId,
//                 credentialFields: [
//                     {
//                         name: 'SSN',
//                         type: 0,
//                         value: '123-45-6789',
//                     },
//                     {
//                         name: 'Address',
//                         type: 0,
//                         value: '123 Apple Drive',
//                     },
//                 ],
//                 id: 'CRED_TEST_ID',
//                 price: 0,
//             },
//         }

//         newEnrollment(message)
//     })
// })

// setTimeout(() => {
//     statusReport.status = EnrollStatus.InviteAccepted
//     statusReport.message = 'Invite was accepted by the user'
//     responseHandle.write(JSON.stringify(statusReport))
// }, 1000)



// setTimeout(() => {
//     statusReport.status = EnrollStatus.OfferReceived
//     statusReport.message = 'The offer was received'
//     responseHandle.write(JSON.stringify(statusReport))
// }, 3000)

// setTimeout(() => {
//     statusReport.status = EnrollStatus.CredentialSent
//     statusReport.message = 'The credential was sent'
//     responseHandle.write(JSON.stringify(statusReport))
// }, 4000)

// setTimeout(() => {
//     statusReport.status = EnrollStatus.ProcessComplete
//     statusReport.message = 'The process is complete'
//     responseHandle.write(JSON.stringify(statusReport))
// }, 5000)

// setTimeout(() => {
//     statusReport.status = EnrollStatus.Error
//     statusReport.message = 'Error'
//     responseHandle.write(JSON.stringify(statusReport))
// }, 6000)
