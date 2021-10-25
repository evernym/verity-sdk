# Announcement for New Customers

On August 17, 2021, the Sovrin Foundation announced [new policies and pricing for their networks](https://sovrin.org/announcement-of-procedural-changes-and-a-revised-price-plan-per-september-16th/). The process of writing transactions to the Sovrin networks has been changed, and the new customers will now have to contact Evernym to endorse their transactions on Sovrin StagingNet. This is only relevant to you if you are signing up for the Developer Plan after October 1st.

If this is the case, Evernym will endorse transactions on Sovrin StagingNet for you. In practice, it means that after receiving your Domain DID and API key from Evernym, you will have to go through the process outlined in this document. Please read it and follow the steps below. This is a one-time process, and after going through it, you will be able to initiate SSI protocols that require an endorsed Issuer Identity and Credential Definition, and take full advantage of our sample apps. 

More details about the changes at Sovrin and what it means for our customers can be found [here](https://www.evernym.com/blog/september-2021-release-notes/#sovrin). 

> **NOTE**: We are currently updating our products to use Sovrin BuilderNet. The process described in this document will be in place only while this work is in progress.

## Prerequisites
- You received Domain DID and API key from Evernym
- You have [Postman](https://www.postman.com/) installed
- You have ngrok installed ([https://ngrok.com/](https://ngrok.com/))

> **NOTE**: **Ngrok** is used as a developer tool to provide a publicly available endpoint that tunnels to the local listening port. You will need a publicly available endpoint to be able to receive messages from the Verity Application Server. 

## Steps
You will call several Verity API's endpoints and receive callbacks from the Verity Application Server. The goal is to obtain the DID and Verkey that represent your Issuer Identity and a relevant Credential Definition. You will then send this data to Evernym to have it endorsed. Running a Postman collection is the simplest way to call the Verity REST API, and the collection is provided in this repository. 

### STEP 1 – Start Ngrok and listen to callbacks on port 4000
Verity Application Server will send messages to your endpoint in response to your API calls. You will make API calls on the next step, and you should be listening to these responses. 

#### Start Ngrok
In a separate terminal window start ngrok for port 4000 and leave it running:

```sh
ngrok http 4000
```

#### Listen to callbacks on port 4000
You can use a [Simple Listener app](simple-listener-app) available in this repository.

### STEP 2 – Make API calls to set up Issuer Identity and write Credential Definition
You will need to set up your Issuer Identity and write your Credential Definition to the ledger. You can use this [Postman collection](Verity.postman_collection.json) at this step. 

#### Set Variables in Postman
In Postman, add your `DomainDID` and `Webhook` as variables, and your API key as `X-API-KEY` in Authorization.  

> **NOTE**: **Webhook** is your ngrok endpoint, e.g. `https://a8cd-64-62-226-249.ngrok.io`  
> **DomainDID** and **X-API-KEY** are the Domain DID and API key you received from Evernym. 

#### Update Endpoint
You will first register a webhook where you will be receiving callbacks from the Verity Server. You will do this by calling the [`UpdateEndpoint` endpoint](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0/#/UpdateEndpoint/updateEndpoint) that sets up the callback webhook.

Run `Update Endpoint` request in Postman and check that you received a response on your endpoint: 

```json
{
    "id": "webhook",
    "@type": "did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED"
}
```

If you don't see the response, please check that you have ngrok running on port 4000 and you are listening to responses as described in STEP 1. 

#### Set up Issuer Identity
Run `Setup Issuer` request in Postman. By running this request, you will be calling the [`IssuerSetup` endpoint](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0/#/IssuerSetup) that creates Issuer DID and Verkey keypair. 

#### Write Schema – optional step
If you would like to create your own Schema, you can call the [`WriteSchema` endpoint](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0#/WriteSchema) by running the `Write Schema` request and use that Schema's `schemaId` when you run the `Write CredDef` request. 

#### Write Credential Definition 
Call the [`WriteCredDef` endpoint](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0#/WriteCredDef/writeCredDef) by running the  `Write CredDef` request in Postman. 

> **NOTE**: If you would like to use an alternative Schema for your Credential Definition, you can choose an existing Schema from [indyscan.io](https://indyscan.io/txs/SOVRIN_STAGINGNET/domain?page=1&pageSize=50&filterTxNames=[%22SCHEMA%22]&sortFromRecent=true) and use that Schema's `schemaId` when you run `Write CredDef` request.  

Examples of responses you will receive on your webhook:

for `Setup Issuer` call:

```json
{
    "identifier": {
        "did": "4vQ7WDWc76BpENZmg9b8j2",
        "verKey": "38wWqTAqbHDvvuLkyM9hqEN5WxEa2KaeDkvr2E6yuXhh"
    },
    "@type": "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created",
    "@id": "56c7920c-9a27-4e40-a87b-d97ac41b7423",
    "~thread": {
        "thid": "20734939-a88c-491d-a2c3-a7ec6d68a258 "
    }
}
```

for `Write Schema` call: 

```json
{
    "schemaId": "4vQ7WDWc76BpENZmg9b8j2:2:Diploma:1.0.0",
    "schemaJson": "{\"endorser\":\"Vr9eqqnUJpJkBwcRV4cHnV\",\"identifier\":\"4vQ7WDWc76BpENZmg9b8j2\",\"operation\":{\"data\":{\"attr_names\":[\"firstName\",\"major\",\"degree\",\"lastName\"],\"name\":\"Diploma\",\"version\":\"1.0.0\"},\"type\":\"101\"},\"protocolVersion\":2,\"reqId\":1633625290948266736,\"signatures\":{\"4vQ7WDWc76BpENZmg9b8j2\":\"5J9bKJ8Y2cyF4BAS5yu9pVuUneVpQuhsehu43Kk5yCEdXmSZ6ExVieJaskeqDp29dmthjtfVRkMhUJXNVaZY1Xom\"},\"taaAcceptance\":{\"mechanism\":\"on_file\",\"taaDigest\":\"8cee5d7a573e4893b08ff53a0761a22a1607df3b3fcd7e75b98696c92879641f\",\"time\":1575417600}}",
    "@type": "did:sov:123456789abcdefghi1234;spec/write-schema/0.6/needs-endorsement",
    "@id": "cfc5348a-1926-410a-8a46-73dcd563b23f",
    "~thread": {
        "thid": "ceab7e7a-7b42-4548-8cbb-df2ebbf9acc8"
    }
}
```

for `Write CredDef` call: 

```json
{
    "credDefId": "4vQ7WDWc76BpENZmg9b8j2:3:CL:179684:latest",
    "credDefJson": "{\"endorser\":\"Vr9eqqnUJpJkBwcRV4cHnV\",\"identifier\":\"4vQ7WDWc76BpENZmg9b8j2\",\"operation\":{\"data\":{\"primary\":{\"n\":\"109234663634767745159683242390922043789342471329182854368895261069664826791051459992875023749970593737714431843170311006383995912354440797874333209058601483898877406865644021520448672173982591296055468199052709461299640596344898952873199920259745786595212897768687362080690275202494880660708802694509108857254804065014735639512187066690903518033006723120863645935437107502968401995088003881445025041203927342419197448799791405120909963858231764470311785683998184314593720792883175505974779003059446831487510951118467998942843009747937164663061718001013892247396120255426452103472225725786251352627748803833908357905233\",\"r\":{\"labcode\":\"16849190626136418213565670233130706524803179414576500605087994566575984426294213326592275177346437765739423157988047427689799644927337764196044941730614993909567009760716546882272863924095362642165145090598316863266045413847756797998272846681266173057589124650928889867471244332965047582538292679343604715462564302150419175105695793952877257455293525658354118330381587360978605138919247513786095757842885908305494738423195791496861069437399281122662930412447510974153798776779703368926525396477041645457957493370754627359090915758748425951939254701890835582835416878623003725819246512403751552990120411280410751199476\",\"labname\":\"80959608205592703266625562912085621507446779149170328452912460075411015508056848245946536512521848748213056343180270744366869477674532612235151567721197870515644846481572549605804668858435308156323555379687533292088090069029654519323106079081818244121807981170044892832437627842261587988765421684475547314201363046388513399659160178399822247307471891097455990338409123829120639317159743947416257998392240149789026089788330508183325233515266991164973704160740772108148276041577663756550062506210250607785799957955485649076951080188902272655352800372830064610337309852227673793308366954167185070944045779842057676402670\",\"master_secret\":\"40066834802444902681181142650608749501374553850617175407861449322333040364781355309557815951711903553706891689588499049469981223444013875003673160089738072023239358533114897122056016718268085764228387953518649395770790396253936111623443880989305090663211238928707583014079082548866504138894325355317089839807321354149870289277023940999825475320040204391512984383586857876786558347614385084774365005627477455150124026286657171286166112334716686637392902976571166196062797493571182671565702259524247368205540146587150444341501848328777328246671984967646226139940416956934742757497987179982363257064565454789041820008329\",\"resultdate\":\"105528390191333886748751774494865331672375019571453969168372734319303448087865624175529880864215937282109848591985882173983623857271680800062359725648398498123639947611975297101139788080194683411348775553713146770195922310206880239277394312146192249191426772170332341001691113964607793174033038208122303596390425850948542809012447077347691357355828928572668001547127154498356699563135313915495255001277863773560227288227869573041278417493392836824117175021024040090092328283496738651699369746422888465634385177225295940277335343775693387336432460999281253058414453704937131718596086745054413762157665748169955644858115\",\"takendate\":\"47969591118550094340884963447428292545975704921331169455822786459417219837957965053582633657132199488830727813683829419444591040387409656161175030492921572332529942306002078663639543810270248422727877214734205871002792704527367610688135967190147431560807282699788728575598499240678163678041243866157122352432660267264142168394766972534565988883679697294816227301884405685102558816987416772377301216035386866960052772390317791501676314313158261988679269432908690518038844591346874534716027353763945639147452932642635991328556949930810014995113379960042058793403883211280158970059790648570071197442280378212315358018148\",\"testformat\":\"40966429392599279781061814909414731692626026932280411664810920833472452426856439303182357011673234452144430743455522957580176607471650500890943186721770952064506516051740581453678281167724581150378733990488020954447623944080812368693707021510164679417880862234197793998950214829170088758004921769791559339980580935718634847258614713689204571314034315968004711631333617372141483593683484087314669553267712581306979350770031465482936839333426463311584372320879777110602504285239513042985871478037999701826193877876952301291874067020992936793762735643361884715074180880616574109428059746607607602397599400845122386844934\",\"testlanguage\":\"73417855589355017511618570001475087140541659156376082230764604669133951996118200026999232845706206824634212818111017050110950216768308329055390702095691510251453922348106302675204180209792363575947093404367869400562194624070334637703159475601617653200527775083633489091268030280364173763943020757915364865992333530056644676898913397870304079846393818205468231340073172217140842578624097918710571451110281560667020726192671814638355277890878432851441937907408678718910803666594115437208887387516787404032713743499432003311009592294471207267845767745986195234336234453600563657915584852687202743068087820205578359196692\",\"testmethod\":\"39807426289299122927640040175266421630798693979599190224317747162781682243057961114214563362149333211552498471731427031441606919101415286045646453356761162853152192111873499552071778795059718660500523321704683187778117994683310166508690465637740049793200140183533868213869795197151724357131770735522449791194038876077978626155525493252996661687390360570057377346591592800771098081603444517900812923199971878833814365958754770018613154604430882140158134372234886436760421418394453254399737387673185308613929383996936379826431791227441119206605111069833799976952236572788865765311316833705788298114925857875413496173273\",\"testresult\":\"59471286901788786029473059790882033056704105351841717940741843044100859088613591211689336552854938014674278557317468096490237356107964396263571913757758840994502263970240881712831620767377142575138291883482725316988252245167528936470312433721510378030232290535478394620835842290325651688307565754446068673945490693277970718104734136286975779398560526307225211857584761674603480658021537245209041851285570757474642186956851364043695628257304144835843927395648595679087353599567866027864771689761348277681668025804225725092501335242195747064451308391910148180368863514951465697393820882075813823722306614147661200029059\",\"testtechnique\":\"104091607774150056137567984236449806612975922942268033834400471738777859203729676225091571381195446343666059340034479629184654918775268550065816116376027994151322659478490004400278900859571626392289359715525335214926962455672923538927550433117408585342807424499916906715344688918068102374115051410162577726907938055126628920483048439395693624099114907915986986626263624733380451426609095717617507144366312524657633762213521813583375088122293736167890850803927830079208210385629655872953009768735336006428124762943430229185184061638060746531665292164395567528915589768642357566646144836276259553394766970293399511155320\",\"testtype\":\"33407379431634860427229509487767715524847992437959569912633373582665017569709939941122130196616896647500064282205414075134164168325316655654935831545932317044560782287538393522548136448577964728292099524240312097275243865357846456619886358418479673239139806686646416595176699033964694297043916524926787173041519346512096794513570614141221048620663961849018554630310246400053968946937612503866899372197742003248227097097227386627868985693425768246827172396457447328080571203809043800677596556837667026457280543917284819401584473768895967296518147889580445366891248445117509604247701954223974398909810985967939442843562\"},\"rctxt\":\"31191220193921440974608458521681191212367278364550196995308195779827475725248703161085794607038360593135170274453640299911591904185658536176729816721931961151622289931135144306942366184832480431146517130497000048768229099403633516290940108113407157710282535244718944764614754278578399287092733596601326400490398434894511489546375067911750026448148797929153002855709250444795385591898162251013126227333848251127700254263758196966686273529806563434675143848063711532813488319800128468099365617352573455865591058805349516016578197457898022301550563294700555858882882572569452478944600108261420688802984739647866521410564\",\"s\":\"72856345912784580879351431450705181554989476970086237684494282611480058921745708652765387748506867754053783615300273380123700229024844788289513184794266501329341169639077906933387401906651859623612352994612730425948063262556412213941512930116027857299367061335443867641476648472661977201644877488661519252166864761772495549050802177965038348489789147533057107797785844128260773191115793793151521264942578352289344467468223729630783183961288195773339475283038639773113157681305058822848120804448094788378118990545554002927193522781323633898979635585260848703974914935121928369027251951302248048865658528101018921192817\",\"z\":\"66557568294127500409021596029426212876637869161722600125037348896908740574656570483261208525210512385863655664815272236032112705962033083884885774961909899092179705561544414125906926320219348449766286613970102928951052111335000222691796925010536879184140921699862959401251414578529065117772057679594483048222174361201983716408648272563770955778760288138718459221507403589402800821570581585004755487622027628896973168402183211963815413714864642942699306437245210364490257019419989010947586224382155699715846506156384710772330861895102457601939582727432461022999930781247134669057133666497566272509901568227412215058202\"}},\"ref\":179684,\"signature_type\":\"CL\",\"tag\":\"latest\",\"type\":\"102\"},\"protocolVersion\":2,\"reqId\":1633625342293799808,\"signatures\":{\"4vQ7WDWc76BpENZmg9b8j2\":\"4xXyYPrA9ReZ7Ze2rw5he2yBxNQL2BenZqLP8iJdEpqRAg9nNTqeYDW2euxQQikUuKyRBLrCTF3zcdqPPS1vJC24\"},\"taaAcceptance\":{\"mechanism\":\"on_file\",\"taaDigest\":\"8cee5d7a573e4893b08ff53a0761a22a1607df3b3fcd7e75b98696c92879641f\",\"time\":1575417600}}",
    "@type": "did:sov:123456789abcdefghi1234;spec/write-cred-def/0.6/needs-endorsement",
    "@id": "ac08e4d5-f855-41d4-9172-86a363dc720d",
    "~thread": {
        "thid": "de171b6f-9f37-455e-850c-ec7afaa594a8"
    }
}
```

### STEP 3 – Contact Evernym to endorse your transactions 
Save your Issuer DID and Verkey (`did` and `verKey` that you received as a response for a `Setup Issuer` call), as well as `credDefJson` and `schemaJson` (if writing your own Schema). Send these values to [support@evernym.com](mailto:support@evernym.com) to have them endorsed. We will inform you when we process your request.

If you experience any issues following this document, please [contact support](mailto:support@evernym.com).