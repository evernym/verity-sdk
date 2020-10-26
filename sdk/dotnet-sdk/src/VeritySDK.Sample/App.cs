using Hyperledger.Indy.WalletApi;
using JsonPrettyPrinterPlus;
using QRCoder;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Json;
using System.Net.Http;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace VeritySDK.Sample
{
    public delegate void handle(string msgName, JsonObject message);

    public class App
    {
        const string VERITY_CONTEXT_STORAGE = "verity-context.json";

        int listenerPort { get; set; } = 4000;
        Listener listener { get; set; }

        private Context context { get; set; }
        public Handlers handlers { get; set; } = new Handlers();

        private string _issuerDID;
        private string _issuerVerkey;
        private string _threadId;
        private string _relDID;
        private string _schemaIdRef;
        private string _defIdRef;

        #region Console utilites

        public static void consoleOutput(string Text, bool WithMargin = false)
        {
            Console.WriteLine(Text);
            if (WithMargin)
                Console.WriteLine();
        }

        public static bool consoleYesNo(string request, bool defaultYes)
        {
            string yesNo = defaultYes ? "[y]/n" : "y/n";
            string modifiedRequest = request + "? " + yesNo + ": ";
            Console.Write(modifiedRequest);
            string response = Console.ReadLine().Trim().ToLower();

            if (defaultYes && "".Equals(response))
            {
                return true;
            }
            else if ("y".Equals(response))
            {
                return true;
            }
            else if ("n".Equals(response))
            {
                return false;
            }

            throw new IOException("Did not get a valid response -- '" + response + "' is not y or n");
        }

        public static string consoleInput(string request, string default_value = "")
        {
            Console.Write(request + ": ");
            var s = Console.ReadLine();
            if ("".Equals(s))
            {
                return default_value ?? "";
            }
            else
            {
                return s;
            }
        }

        public static void consolePrintMessage(string messageName, JsonObject message)
        {
            try
            {
                consolePrintObject(message, "<<<", $"Incoming Message -- {messageName}");
            }
            catch (IOException ignored)
            {
            }
        }

        public static void consolePrintObject(JsonObject obj, string prefix, string preamble)
        {
            consoleOutput(prefix + "  " + preamble);
            var obj_lines = obj.ToString().PrettyPrintJson().Split("\r\n");
            foreach (var line in obj_lines)
            {
                consoleOutput(prefix + "  " + line);
            }
            consoleOutput("");
        }

        static void WaitReturn(string waitMsg)
        {
            TextWriter consoleOut = Console.Out;

            var writer = new StringWriter();
            Console.SetOut(writer);

            try
            {
                consoleOut.WriteLine();
                consoleOut.Write(waitMsg + " ... ");
                consoleOut.Flush();

                try { Console.ReadLine(); } catch (IOException ignored) { }

                consoleOut.Write("Done\n");
                consoleOut.Flush();
            }
            finally
            {
                consoleOut.Write(writer.ToString());
                Console.SetOut(consoleOut);
            }
        }

        void WaitFor(ref bool canContinue, string waitMsg)
        {
            TextWriter consoleOut = Console.Out;

            var writer = new StringWriter();
            Console.SetOut(writer);
            try
            {
                string[] spinner = new String[] { "\u0008/", "\u0008-", "\u0008\\", "\u0008|" };
                consoleOut.WriteLine();
                consoleOut.Write(waitMsg + " ... ");
                consoleOut.Write("|");
                int pos = 0;
                while (!canContinue)
                {
                    try { Thread.Sleep(450); } catch { };
                    consoleOut.Write($"{spinner[pos % spinner.Length]}");
                    consoleOut.Flush();
                    pos++;
                }
                consoleOut.Write("\u0008");
                consoleOut.Write("Done\n");
                consoleOut.Flush();
            }
            finally
            {
                consoleOut.Write(writer.ToString());
                Console.SetOut(consoleOut);
            }
        }

        #endregion

        #region Listenner

        private void StartListening()
        {
            listener = new Listener(listenerPort, (encryptedMessageFromVerity) =>
            {
                try
                {
                    handlers.handleMessage(context, Encoding.UTF8.GetBytes(encryptedMessageFromVerity));
                }
                catch (Exception ex)
                {
                    App.consoleOutput("!! Verity - Error: " + ex.Message);
                }
            });

            listener.listen();
        }

        private void stopListening()
        {
            listener.stop();
        }

        #endregion

        public void Execute()
        {
            try
            {
                StartListening();

                DoSetup();

                DoCreateRelationship();

                DoCreateConnection();

                DoAskQuestion();

                DoWriteLedgerSchema();

                DoWriteLedgerCredDef();

                DoIssueCredential();

                DoRequestProof();
            }
            catch (Exception ex)
            {
                App.consoleOutput("==========");
                App.consoleOutput(ex.Message + "\r\n" + ex.InnerException?.Message);
                App.consoleOutput("==========");
            }
            finally
            {
                stopListening();
            }
        }

        #region Processes

        #region Setup

        void DoSetup()
        {
            if (File.Exists(VERITY_CONTEXT_STORAGE))
            {
                if (consoleYesNo("Reuse Verity Context (in verity-context.json)", true))
                {
                    context = loadContext();
                }
                else
                {
                    context = provisionAgent();
                }
            }
            else
            {
                context = provisionAgent();
            }

            updateWebhookEndpoint();

            App.consolePrintObject(context.toJson(), ">>>", "Context Used:");

            File.WriteAllText(VERITY_CONTEXT_STORAGE, context.toJson().ToString().PrettyPrintJson());

            updateConfigs();

            issuerIdentifier();

            if (_issuerDID == null)
            {
                setupIssuer();
            }
        }

        Context loadContext()
        {
            var data = File.ReadAllText(VERITY_CONTEXT_STORAGE);

            var ctx_b = ContextBuilder.fromJson(data);
            var ctx = ctx_b.build();

            return ctx;
        }

        Context provisionAgent()
        {
            ProvisionV0_7 provisioner;
            if (consoleYesNo("Provide Provision Token", true))
            {
                string token = consoleInput("Token", Environment.GetEnvironmentVariable("TOKEN")).Trim();
                App.consoleOutput($"Using provision token: {token}");
                provisioner = Provision.v0_7(token);
            }
            else
            {
                provisioner = Provision.v0_7();
            }

            string verityUrl = consoleInput("Verity Application Endpoint", Environment.GetEnvironmentVariable("VERITY_SERVER")).Trim();

            if ("".Equals(verityUrl))
            {
                verityUrl = "http://localhost:9000";
            }

            App.consoleOutput($"Using Url: {verityUrl}");

            // create initial Context
            Context ctx = ContextBuilder.fromScratch("examplewallet1", "examplewallet1", verityUrl);

            // ask that an agent by provision (setup) and associated with created key pair
            Context provisioningResponse = null;
            try
            {
                provisioningResponse = provisioner.provision(ctx);
            }
            catch (ProvisionTokenException e)
            {
                App.consoleOutput(e.ToString());
                App.consoleOutput("Provisioning failed! Likely causes:");
                App.consoleOutput("- token not provided but Verity Endpoint requires it");
                App.consoleOutput("- token provided but is invalid or expired");

                throw new Exception();
            }

            return provisioningResponse;
        }

        void updateWebhookEndpoint()
        {
            string webhookFromCtx = "";

            try
            {
                webhookFromCtx = context.EndpointUrl();
            }
            catch (UndefinedContextException ignored)
            {
            }

            string webhook = consoleInput($"Ngrok endpoint for [{webhookFromCtx}:{listenerPort}]", Environment.GetEnvironmentVariable("WEBHOOK_URL")).Trim();

            if ("".Equals(webhook))
            {
                webhook = webhookFromCtx;
            }

            App.consoleOutput($"Using Webhook: {webhook}");
            context = context.ToContextBuilder().endpointUrl(webhook).build();

            // request that verity-application use specified webhook endpoint
            UpdateEndpoint.v0_6().update(context);
        }

        void updateConfigs()
        {
            string INSTITUTION_NAME = "Faber College";
            string LOGO_URL = "https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png";

            UpdateConfigsV0_6 updateConfigs = UpdateConfigs.v0_6(INSTITUTION_NAME, LOGO_URL);
            updateConfigs.update(context);
            updateConfigs.status(context);
        }

        #endregion

        #region issuerIdentifier

        bool issuerComplete = false;

        void issuerIdentifier()
        {
            // constructor for the Issuer Setup protocol
            IssuerSetupV0_6 issuerSetup = IssuerSetup.v0_6();

            issuerIdentifierHandler(issuerSetup/*, issuerComplete*/);

            // query the current identifier
            issuerSetup.currentPublicIdentifier(context);

            // wait for response from verity-application
            WaitFor(ref issuerComplete, "Waiting for current issuer DID");
        }

        void issuerIdentifierHandler(IssuerSetupV0_6 issuerSetup)
        {
            // handler for current issuer identifier message
            handlers.addHandler(
                    issuerSetup,
                    (msgName, message) =>
                    {
                        if ("public-identifier".Equals(msgName))
                        {
                            App.consolePrintMessage(msgName, message);
                            _issuerDID = message.Get("did");
                            _issuerVerkey = message.Get("verKey");

                            issuerComplete = true;
                        }
                    }
            );
        }

        #endregion

        #region setupIssuer();

        bool setup_complete = false;

        void setupIssuer()
        {
            // constructor for the Issuer Setup protocol
            IssuerSetupV0_6 newIssuerSetup = IssuerSetup.v0_6();

            // handler for created issuer identifier message
            setupIssuerHandler(newIssuerSetup);

            // request that issuer identifier be created
            newIssuerSetup.create(context);

            // wait for request to complete
            WaitFor(ref setup_complete, "Waiting for setup to complete");

            App.consoleOutput("The issuer DID and Verkey must be on the ledger.");

            bool automatedRegistration = consoleYesNo("Attempt automated registration via https://selfserve.sovrin.org", true);

            if (automatedRegistration)
            {
                HttpClient client = new HttpClient();

                var request = new HttpRequestMessage(HttpMethod.Post, "https://selfserve.sovrin.org/nym");

                JsonObject payload_builder = new JsonObject();
                payload_builder.Add("network", "stagingnet");
                payload_builder.Add("did", _issuerDID);
                payload_builder.Add("verkey", _issuerVerkey);
                payload_builder.Add("paymentaddr", "");
                string payload = payload_builder.ToString();

                request.Content = new StringContent(payload);
                request.Content.Headers.Add("Accept", "application/json");
                request.Content.Headers.Add("Content-type", "application/json");

                var response = client.PostAsync(request.RequestUri, request.Content).GetAwaiter().GetResult();
                if (response.StatusCode == System.Net.HttpStatusCode.OK)
                {
                    var data = response.Content.ReadAsStringAsync().GetAwaiter().GetResult();
                    App.consoleOutput($"Got response from Sovrin portal: {data}");
                }
                else
                {
                    App.consoleOutput("Something went wrong with contactig Sovrin portal");
                    App.consoleOutput($"Please add DID ({_issuerDID}) and Verkey ({_issuerVerkey}) to ledger manually");

                    WaitReturn("Press ENTER when DID is on ledger");
                }
            }
            else
            {
                App.consoleOutput($"Please add DID ({_issuerDID}) and Verkey ({_issuerVerkey}) to ledger manually");

                WaitReturn("Press ENTER when DID is on ledger");
            }
        }

        void setupIssuerHandler(IssuerSetupV0_6 newIssuerSetup)
        {
            handlers.addHandler(
                    newIssuerSetup,
                    (msgName, message) =>
                    {
                        if ("public-identifier-created".Equals(msgName))
                        {
                            App.consolePrintMessage(msgName, message);

                            var json_identifier = (JsonObject.Parse(message) as JsonObject).Get("identifier");
                            _issuerDID = json_identifier["did"];
                            _issuerVerkey = json_identifier["verKey"];

                            setup_complete = true;
                        }
                        else
                        {
                            App.consoleOutput("Message Name is not handled");
                            App.consolePrintMessage(msgName, message);
                        }
                    }
            );
        }

        #endregion

        #region createRelationship

        bool startRelationshipComplete = false;
        bool invitationComplete = false;

        void DoCreateRelationship()
        {
            // Relationship protocol has two steps
            // 1. create relationship key
            // 2. create invitation

            RelationshipV1_0 relProvisioning = Relationship.v1_0("Faber College");

            // handler for the response to the request to start the Connecting protocol.
            relationshipHandler(relProvisioning);

            relProvisioning.create(context);
            WaitFor(ref startRelationshipComplete, "Waiting to start relationship");

            RelationshipV1_0 relationship = Relationship.v1_0(_relDID, _threadId);
            relationship.connectionInvitation(context);
            WaitFor(ref invitationComplete, "Waiting for invite");
        }

        void relationshipHandler(RelationshipV1_0 relProvisioning)
        {
            // handler for current issuer identifier message
            handlers.addHandler(
                    relProvisioning,
                    (msgName, message) =>
                    {
                        if ("created".Equals(msgName))
                        {
                            App.consolePrintMessage(msgName, message);

                            var json_thread = message.Get("~thread")["thid"];

                            _threadId = json_thread;
                            _relDID = message.Get("did");

                            startRelationshipComplete = true;
                        }
                        else if ("invitation".Equals(msgName))
                        {
                            App.consolePrintMessage(msgName, message);

                            string inviteURL = message.Get("inviteURL");

                            try
                            {
                                QRCodeGenerator qrGenerator = new QRCodeGenerator();
                                QRCodeData qrCodeData = qrGenerator.CreateQrCode(inviteURL, QRCodeGenerator.ECCLevel.L);
                                QRCode qrCode = new QRCode(qrCodeData);
                                Bitmap qrCodeImage = qrCode.GetGraphic(3);
                                qrCodeImage.Save("qrcode.png", ImageFormat.Png);
                            }
                            catch (FileNotFoundException e)
                            {
                                App.consoleOutput(e.Message);
                            }

                            if (!(Environment.GetEnvironmentVariable("HTTP_SERVER_URL") == null))
                            {
                                App.consoleOutput("Open the following URL in your browser and scan presented QR code");
                                App.consoleOutput(Environment.GetEnvironmentVariable("HTTP_SERVER_URL") + "/java-example-app/qrcode.html");
                            }
                            else
                            {
                                App.consoleOutput("QR code generated at: qrcode.png");
                                App.consoleOutput("Open this file and scan QR code to establish a connection");
                            }

                            invitationComplete = true;
                        }
                        else
                        {
                            App.consoleOutput("Message Name is not handled");
                            App.consolePrintMessage(msgName, message);
                        }
                    }
            );

        }

        #endregion

        #region createConnection

        bool requestReceived = false;
        bool startResponse = false;

        void DoCreateConnection()
        {
            // Constructor for the Connecting API
            ConnectionsV1_0 listener = Connecting.v1_0("", "");

            handlers.addHandler(
                               listener,
                               (msgName, message) =>
                               {
                                   if ("request-received".Equals(msgName))
                                   {
                                       requestReceived = true;
                                   }
                                   else if ("response-sent".Equals(msgName))
                                   {
                                       startResponse = true;
                                   }
                                   else
                                   {
                                       App.consoleOutput("Message Name is not handled");
                                       App.consolePrintMessage(msgName, message);
                                   }
                               }
                       );

            WaitFor(ref requestReceived, "Waiting to receive Request");

            WaitFor(ref startResponse, "Responding to connection request");
        }

        #endregion

        #region askQuestion

        bool questionComplete = false;

        void DoAskQuestion()
        {
            string questionText = "Hi Alice, how are you today?";
            string questionDetail = "Checking up on you today.";
            string[] validResponses = { "Great!", "Not so good." };

            CommittedAnswerV1_0 committedAnswer = CommittedAnswer.v1_0(
                    _relDID,
                    questionText,
                    questionDetail,
                    validResponses,
                    true);

            handlers.addHandler(
                    committedAnswer,
                    (msgName, message) =>
                    {
                        if ("answer-given".Equals(msgName))
                        {
                            App.consolePrintMessage(msgName, message);

                            questionComplete = true;
                        }
                        else
                        {
                            App.consoleOutput("Message Name is not handled");
                            App.consolePrintMessage(msgName, message);
                        }
                    }
            );

            committedAnswer.ask(context);

            WaitFor(ref questionComplete, "Waiting for Connect.Me to answer the question");
        }

        #endregion

        #region writeLedgerSchema

        bool schemaComplete = false;

        void DoWriteLedgerSchema()
        {
            // input parameters for schema
            string schemaName = "Diploma " + Guid.NewGuid().ToString().Substring(0, 8);
            string schemaVersion = "0.1";

            // constructor for the Write Schema protocol
            WriteSchemaV0_6 writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, "name", "degree");

            writeSchemaHandler(writeSchema);

            // request schema be written to ledger
            writeSchema.write(context);

            // wait for operation to be complete
            WaitFor(ref schemaComplete, "Waiting to write schema to ledger");
        }

        void writeSchemaHandler(WriteSchemaV0_6 writeSchema)
        {
            // handler for message received when schema is written
            handlers.addHandler(
                   writeSchema,
                   (msgName, message) =>
                   {
                       if ("status-report".Equals(msgName))
                       {
                           App.consolePrintMessage(msgName, message);

                           _schemaIdRef = message["schemaId"];

                           schemaComplete = true;
                       }
                       else
                       {
                           App.consoleOutput("Message Name is not handled");
                           App.consolePrintMessage(msgName, message);
                       }
                   }
           );
        }

        #endregion

        #region writeLedgerCredDef

        bool defComplete = false;

        void DoWriteLedgerCredDef()
        {

            // input parameters for cred definition
            string credDefName = "Trinity Collage Diplomas";
            string credDefTag = "latest";

            // constructor for the Write Credential Definition protocol
            WriteCredentialDefinitionV0_6 def = WriteCredentialDefinition.v0_6(credDefName, _schemaIdRef, credDefTag);

            writeCredDefHandler(def);

            // request the cred def be writen to ledger
            def.write(context);

            // wait for operation to be complete
            WaitFor(ref defComplete, "Waiting to write cred def to ledger");
        }

        void writeCredDefHandler(WriteCredentialDefinitionV0_6 def)
        {
            // handler for message received when schema is written
            handlers.addHandler(
                   def,
                   (msgName, message) =>
                   {
                       if ("status-report".Equals(msgName))
                       {
                           App.consolePrintMessage(msgName, message);

                           _defIdRef = message["credDefId"];

                           defComplete = true;
                       }
                       else
                       {
                           App.consoleOutput("Message Name is not handled");
                           App.consolePrintMessage(msgName, message);
                       }
                   }
           );
        }

        #endregion

        #region issueCredential

        bool offerSent = false;
        bool credSent = false;

        void DoIssueCredential()
        {
            // input parameters for issue credential
            string credentialName = "Degree";
            Dictionary<string, string> credentialData = new Dictionary<string, string>();
            credentialData.Add("name", "Alice Smith");
            credentialData.Add("degree", "Bachelors");

            // constructor for the Issue Credential protocol
            IssueCredentialV1_0 issue = IssueCredential.v1_0(_relDID, _defIdRef, credentialData, credentialName, "0", true);

            issueCredentialHandler(issue);

            // request that credential is offered
            issue.offerCredential(context);

            WaitFor(ref offerSent, "Wait for offer to be sent");

            WaitFor(ref credSent, "Wait for Connect.me to request the credential and credential to be sent");

            Thread.Sleep(3000); // Give time for Credential to get to mobile device
        }

        void issueCredentialHandler(IssueCredentialV1_0 issue)
        {
            // handler for signal messages
            handlers.addHandler(
                   issue,
                   (msgName, message) =>
                   {
                       if ("sent".Equals(msgName) && !offerSent)
                       {
                           App.consolePrintMessage(msgName, message);

                           offerSent = true;
                       }
                       else if ("sent".Equals(msgName))
                       {
                           App.consolePrintMessage(msgName, message);

                           credSent = true;
                       }
                       else
                       {
                           App.consoleOutput("Message Name is not handled");
                           App.consolePrintMessage(msgName, message);
                       }
                   }
           );
        }

        #endregion

        #region requestProof

        bool proofComplete = false;

        void DoRequestProof()
        {
            // input parameters for request proof
            string proofName = "Proof of Degree - " + Guid.NewGuid().ToString().Substring(0, 8);

            Restriction restriction = RestrictionBuilder
                    .blank()
                    .issuerDid(_issuerDID)
                    .build();

            Attribute nameAttr = PresentProofV1_0.attribute("name", restriction);
            Attribute degreeAttr = PresentProofV1_0.attribute("degree", restriction);

            // constructor for the Present Proof protocol
            PresentProofV1_0 proof = PresentProof.v1_0(_relDID, proofName, nameAttr, degreeAttr);

            requestProofHandler(proof);

            // request proof
            proof.request(context);

            // wait for connect.me user to present the requested proof
            WaitFor(ref proofComplete, "Waiting for proof presentation from Connect.me");
        }

        void requestProofHandler(PresentProofV1_0 proof)
        {
            // handler for the result of the proof presentation
            handlers.addHandler(
                   proof,
                   (msgName, message) =>
                   {
                       if ("presentation-result".Equals(msgName))
                       {
                           App.consolePrintMessage(msgName, message);

                           proofComplete = true;
                       }
                       else
                       {
                           App.consoleOutput("Message Name is not handled");
                           App.consolePrintMessage(msgName, message);
                       }
                   }
           );
        }

        #endregion

        #endregion

    }
}