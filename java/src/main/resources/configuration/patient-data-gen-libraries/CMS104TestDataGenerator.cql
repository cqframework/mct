library CMS104TestDataGenerator version '1.0.0'

using FHIR version '4.0.1'

parameter MeasurementPeriod default Interval[@2022-01-01, @2022-03-31]
parameter NumberOfTests default 200

define NumberOfTestCases:
    (expand { Interval[ToQuantity(0), ToQuantity(NumberOfTests)] } per ToQuantity(1)) exapandResult
        return Truncate((start of exapandResult).value)

define function GetNumberOfTestCasesList(count Integer):
    (expand { Interval[ToQuantity(0), ToQuantity(count)] } per ToQuantity(1)) exapandResult
        return Truncate((start of exapandResult).value)

define InNumeratorCount:
    GetNumberOfTestCasesList(Truncate(Round(Length(NumberOfTestCases) * 0.6)))

define OtherPopulationsCount:
    GetNumberOfTestCasesList(Truncate(Round(Length(NumberOfTestCases) * 0.1)))

define function getRandomNumber() returns Decimal : external

define function RandomNumberInRange(low Integer, high Integer):
    Truncate(Round(getRandomNumber() * ( high - low ) + low))

define RandomMonth:
    RandomNumberInRange(1, 12)

define RandomDay:
    RandomNumberInRange(1, 28)

define RandomHour:
    RandomNumberInRange(1, 23)

define RandomMinute:
    RandomNumberInRange(1, 59)

define RandomSecond:
    RandomNumberInRange(1, 59)

define RandomMillisecond:
    RandomNumberInRange(1, 999)

define EvaluationTimezone:
    timezoneoffset from Now()

define MaleFirstName_100:
    {'Lucas', 'Markell', 'Leland', 'Jonatan', 'Frederick', 'German', 'Elvis', 'Gage', 'Yisroel', 'Thomas', 'Jayson', 'Kenton', 'Patrick', 'Alberto', 'Willis', 'Davon', 'Kyron', 'Rolando', 'Johnny', 'Cristian', 'Donald', 'Broderick', 'Chaz', 'Elian', 'Darrin', 'Dayton', 'Jude', 'Samir', 'Benito', 'Demetrius', 'Shane', 'Adan', 'Johnpaul', 'Waylon', 'Rigoberto', 'Bo', 'Bruno', 'Kadin', 'Shamar', 'Vernon', 'Bryce', 'Dane', 'Ronaldo', 'Aditya', 'Bowen', 'Luciano', 'Jamison', 'Bernard', 'Brennan', 'Xander', 'Tate', 'Lukas', 'Sydney', 'Jayvon', 'Samuel', 'Jett', 'Octavio', 'Ramon', 'Humberto', 'Elliot', 'Riley', 'Izaiah', 'Ronan', 'Myron', 'Kale', 'Wade', 'Randolph', 'Konner', 'Donnell', 'Jaxon', 'Santino', 'Taylor', 'Jerod', 'Corey', 'Leonardo', 'Ibrahim', 'Eddie', 'Ashton', 'Bobby', 'Keshawn', 'Kerry', 'Garett', 'Adrian', 'Leon', 'Parker', 'Ellis', 'Kalen', 'Austin', 'Ralph', 'Deshawn', 'Keelan', 'Cade', 'Jamel', 'Benjamin', 'Seamus', 'Treyvon', 'Notnamed', 'Greyson', 'Devlin', 'Julian'}

define FemaleFirstName_100:
    {'Lilian', 'Carrie', 'Meagan', 'Maddison', 'Rhonda', 'Kaiya', 'Alysa', 'Angelique', 'Bonnie', 'Rita', 'Santana', 'Daija', 'Carina', 'Madeleine', 'Notnamed', 'Jacie', 'Jaycie', 'Yulissa', 'Elexis', 'Adrianna', 'Nicole', 'Michaela', 'Nallely', 'Brandi', 'Mariah', 'Elisha', 'Jamie', 'Susana', 'Lianna', 'Sierra', 'Lexie', 'Stefany', 'Josefina', 'Lina', 'Alaysia', 'Chana', 'Katy', 'Robyn', 'Sara', 'Charlize', 'Kari', 'Brandy', 'Yamile', 'Kaylin', 'Lauryn', 'Harlie', 'Ellie', 'Verania', 'Madysen', 'Lucinda', 'Melinda', 'Irene', 'Ayesha', 'Jamia', 'Tea', 'Joseline', 'Aiyanna', 'Destini', 'Saige', 'Makiya', 'Dymond', 'Tatyana', 'Layla', 'Maricela', 'Deanna', 'Claudia', 'Averie', 'Amaiya', 'Adia', 'Ximena', 'Caleigh', 'Ainsley', 'Mckayla', 'Maci', 'Lizette', 'Amari', 'Josephine', 'Misty', 'Janna', 'Bayley', 'Heidy', 'Elyssa', 'Julieta', 'Maeve', 'Lori', 'Gracie', 'Mckinley', 'Aiyana', 'Maiya', 'Yessenia', 'Brielle', 'Armani', 'Christie', 'Heidi', 'Kellie', 'Alyssa', 'Madalynn', 'Zara', 'Lea', 'Malia'}

define LastNames_200:
    {'Galvez', 'Najera', 'Cornelius', 'Thrasher', 'Grove', 'Hughes', 'Cosgrove', 'Bollinger', 'Lane', 'Mejia', 'Downey', 'Brewer', 'Duncan', 'Gandy', 'Ash', 'Grimes', 'Forman', 'Robinson', 'Chow', 'Ha', 'Yancey', 'Carlton', 'Shank', 'Hamm', 'Shields', 'Anderson', 'Sheridan', 'Worden', 'Toro', 'Espinosa', 'Nagy', 'Beckwith', 'Molina', 'Ingram', 'Pace', 'Dominguez', 'Clough', 'Andres', 'Raymond', 'Spurlock', 'Liles', 'Chavarria', 'Acuna', 'Neil', 'Peterman', 'Rauch', 'Romero', 'Collazo', 'Burge', 'High', 'Girard', 'Taylor', 'Knudsen', 'Gale', 'Paz', 'Wagoner', 'Luce', 'Patterson', 'Close', 'Jack', 'Kinsey', 'Taggart', 'Engle', 'Masters', 'Clements', 'Brinkman', 'Singh', 'Yoo', 'Bailey', 'Broughton', 'Weis', 'Rock', 'Stokes', 'Elizondo', 'Wolf', 'Ambrose', 'Zapata', 'Garza', 'Godfrey', 'Brant', 'Yoder', 'Mcgrew', 'Sheets', 'Arias', 'Ketchum', 'Pauley', 'Kaminski', 'Devries', 'Beavers', 'Sharp', 'Ayres', 'Dailey', 'Schaffer', 'Cortez', 'Hill', 'Shen', 'Mackenzie', 'Strauss', 'Haynes', 'Kent', 'Andersen', 'Greiner', 'Muhammad', 'Salgado', 'Westfall', 'Dahl', 'Kimble', 'Zielinski', 'Culp', 'Bustos', 'Ramirez', 'Ruggiero', 'Joseph', 'Biddle', 'Meeker', 'Duke', 'Estep', 'Grossman', 'Becerra', 'Mohr', 'Ledbetter', 'Rogers', 'Estrella', 'Daugherty', 'Switzer', 'Weatherford', 'Schulze', 'Ott', 'Scroggins', 'Alcantar', 'Heinz', 'Adamson', 'Lafferty', 'Gifford', 'Peek', 'Burgos', 'Phelan', 'Weller', 'Barba', 'Sorensen', 'Hamel', 'Aguirre', 'Aponte', 'McCormack', 'Lantz', 'Schell', 'Burr', 'Currier', 'Carlson', 'Post', 'Crutchfield', 'Marx', 'Quick', 'Keck', 'Cho', 'Lindley', 'Whitson', 'Flood', 'McClintock', 'Nolan', 'Lightfoot', 'Bostic', 'Ashley', 'Roque', 'Mazur', 'Ware', 'Bautista', 'Kirkland', 'Wheaton', 'Waller', 'Gaddis', 'Tavares', 'Corey', 'Hadley', 'Kessler', 'Lehmann', 'Pringle', 'Lam', 'Gorman', 'Paris', 'Love', 'Goodrich', 'See', 'Ritter', 'Cornwell', 'Newton', 'Rowland', 'Ferrara', 'Benavides', 'Monroe', 'Pack', 'Brannon', 'Dove', 'Goldberg', 'Farrell', 'Harrington', 'Kaur', 'Lassiter', 'Antonio', 'Hale'}

define RandomMaleFirstName:
    MaleFirstName_100[RandomNumberInRange(0, 99)]

define RandomFemaleFirstName:
    FemaleFirstName_100[RandomNumberInRange(0, 99)]

define RandomLastName:
    LastNames_200[RandomNumberInRange(0, 199)]

define ICD10IschemicStrokeCodes:
    Tuple {
        system: 'http://hl7.org/fhir/sid/icd-10-cm',
        codes: { 'I63.011', 'I63.012', 'I63.013', 'I63.019', 'I63.02', 'I63.031', 'I63.032', 'I63.033', 'I63.039', 'I63.09', 'I63.10', 'I63.111', 'I63.112', 'I63.113', 'I63.119', 'I63.12', 'I63.131', 'I63.132', 'I63.133', 'I63.139', 'I63.19', 'I63.20', 'I63.211', 'I63.212', 'I63.213', 'I63.219', 'I63.22', 'I63.231', 'I63.232', 'I63.233', 'I63.239', 'I63.29', 'I63.30', 'I63.311', 'I63.312', 'I63.313', 'I63.319', 'I63.321', 'I63.322', 'I63.323', 'I63.329', 'I63.331', 'I63.332', 'I63.333', 'I63.339', 'I63.341', 'I63.342', 'I63.343', 'I63.349', 'I63.39', 'I63.40', 'I63.411', 'I63.412', 'I63.413', 'I63.419', 'I63.421', 'I63.422', 'I63.423', 'I63.429', 'I63.431', 'I63.432', 'I63.433', 'I63.439', 'I63.441', 'I63.442', 'I63.443', 'I63.449', 'I63.49', 'I63.50', 'I63.511', 'I63.512', 'I63.513', 'I63.519', 'I63.521', 'I63.522', 'I63.523', 'I63.529', 'I63.531', 'I63.532', 'I63.533', 'I63.539', 'I63.541', 'I63.542', 'I63.543', 'I63.549', 'I63.59', 'I63.6', 'I63.81', 'I63.89', 'I63.9' }
    }

define SnomedIschemicStrokeCodes:
    Tuple {
        system: 'http://snomed.info/sct',
        codes: { '111297002', '116288000', '195185009', '195186005', '195189003', '195190007', '195212005', '195213000', '195216008', '195217004', '195230003', '230690007', '230691006', '230692004', '230693009', '230694003' , '230695002', '230696001', '230698000', '230699008', '230700009', '230701008', '307363008', '422504002', '426107000' }
    }

define RxNormAntithromboticCodes:
    Tuple {
        system: 'http://www.nlm.nih.gov/research/umls/rxnorm',
        codes: { '1037045', '1037179', '1092398', '1114198', '1116635', '1232082', '1232086', '1250907', '1361574', '1364435', '1364445', '1536467', '1536498', '1536503', '1536675', '1536815', '1536833', '1536840', '1537029', '1549682', '1593110', '1599543', '1599551', '1599555', '1658717', '1659195', '1659197', '1665356', '1666332', '1722689', '1723476', '1730187', '1804735', '1804737', '1804738', '1811631', '1811632', '198464', '198466', '198467', '198471', '198473', '198475', '198479', '198480', '1992427', '2059015', '212033', '243670', '243694', '252857', '2588062', '259081', '308278', '308297', '308351', '308363', '308409', '308411', '308416', '308417', '309362', '313406', '318272', '359221', '432638', '692836', '702316', '749196', '763111', '763116', '827318', '854228', '854235', '854238', '854241', '854245', '854248', '854252', '854255', '855288', '855296', '855302', '855312', '855318', '855324', '855332', '855338', '855344', '861356', '861360', '861363', '861365', '900528', '978725', '978733', '978736', '978740', '978744', '978746', '978755', '978759', '978777', '994430', '994435', '994528', '994535' }
    }

define MedicationRequestReasonCodes:
    Tuple {
        system: 'http://snomed.info/sct',
        codes: { '105480006', '182890002', '182895007', '443390004', '183966005', '31438003', '407563006', '410529002', '416098002', '419511003', '59037007', '62014003', '79899007' }
    }

define InpatientEncounterCodes:
    { '183452005', '32485007' }

define InpatientEncounterDischargeCodes:
    Tuple {
        system: 'http://snomed.info/sct',
        codes: { '428371000124100', '306701001', '306703003', '434781000124105', '445060000', '371828006', '428361000124107' }
    }

define MedicationRequestStatusCodes:
    { 'active', 'completed' }

define RaceCategoryCodes:
    {
        Tuple { raceCategoryCode: '1002-5', raceCategoryDisplay: 'American Indian or Alaska Native' },
        Tuple { raceCategoryCode: '2028-9', raceCategoryDisplay: 'Asian' },
        Tuple { raceCategoryCode: '2054-5', raceCategoryDisplay: 'Black or African American' },
        Tuple { raceCategoryCode: '2076-8', raceCategoryDisplay: 'Native Hawaiian or Other Pacific Islander' },
        Tuple { raceCategoryCode: '2106-3', raceCategoryDisplay: 'White' }
    }

define EthnicityCategoryCodes:
    {
        Tuple { ethnicityCategoryCode: '2135-1', ethnicityCategoryDisplay: 'Hispanic or Latino' },
        Tuple { ethnicityCategoryCode: '2186-5', ethnicityCategoryDisplay: 'Non Hispanic or Latino' }
    }

define DateMaxDays:
    { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }

define GetRandomDateTimeDuringMeasurementPeriod:
    DateTime(
        year from start of MeasurementPeriod,
        RandomNumberInRange(month from start of MeasurementPeriod, month from end of MeasurementPeriod),
        RandomDay, RandomHour, RandomMinute, RandomSecond, RandomMillisecond, EvaluationTimezone
    )

define function DateInPeriodRough(startDate DateTime, endDate DateTime):
    Tuple {
        theYear: year from startDate,
        theMonth: if month from startDate > month from endDate then month from startDate else RandomNumberInRange(month from startDate, month from endDate),
        theDay: if day from startDate > day from endDate then day from startDate else RandomNumberInRange(day from startDate, day from endDate)
    }

define function GetRandomDateInPeriod(startDate DateTime, endDate DateTime):
    startDate s
        let RoughDate: DateInPeriodRough(startDate, endDate)
    return DateTime(
        RoughDate.theYear,
        RoughDate.theMonth,
        if RoughDate.theDay > DateMaxDays[RoughDate.theMonth] then day from endDate else RoughDate.theDay,
        hour from startDate, minute from startDate, second from startDate, millisecond from startDate, EvaluationTimezone
    )

define function BuildRandomTestCase(TestCaseId String, PatientReference String, IsMale Boolean, HasInpatientEncounterWithDischarge Boolean, HasStroke Boolean, HasAntithromboticMedReq Boolean, AntithromboticMedReqNotPerformed Boolean):
    TestCaseId ID
        let PatientFirstName: if IsMale then RandomMaleFirstName else RandomFemaleFirstName,
            PatientLastName: RandomLastName,
            PatientGender: if IsMale then 'male' else 'female',
            PatientBirthDate: Date(RandomNumberInRange(1950, 2000), RandomMonth, RandomDay),
            AdultPatient: BuildPatient(TestCaseId, PatientFirstName, PatientLastName, PatientGender, PatientBirthDate),
            ConditionReference: 'Condition/' + TestCaseId,
            IschemicStrokeCodeSets: { ICD10IschemicStrokeCodes, SnomedIschemicStrokeCodes },
            RandomIschemicCodeSet: IschemicStrokeCodeSets[RandomNumberInRange(0, 1)],
            IschemicCodeSystem: RandomIschemicCodeSet.system,
            IschemicCodeCode: RandomIschemicCodeSet.codes[RandomNumberInRange(0, Length(RandomIschemicCodeSet.codes) - 1)],
            IschemicCondition: BuildCondition(TestCaseId, IschemicCodeSystem, IschemicCodeCode, PatientReference),
            RandomInpatientEncounterCode: InpatientEncounterCodes[RandomNumberInRange(0, 1)],
            EncounterPeriodEnd: GetRandomDateTimeDuringMeasurementPeriod,
            EncounterPeriodStart: EncounterPeriodEnd - ToQuantity(ToString(RandomNumberInRange(0, 120)) + ' days'),
            EncounterInterval: Interval[EncounterPeriodStart, EncounterPeriodEnd],
            EncounterDischargeSystem: if HasInpatientEncounterWithDischarge then InpatientEncounterDischargeCodes.system else null,
            EncounterDischargeCode: if HasInpatientEncounterWithDischarge then InpatientEncounterDischargeCodes.codes[RandomNumberInRange(0, 6)] else null,
            NonElectiveInpatientEncounter: BuildEncounter(TestCaseId, 'finished', RandomInpatientEncounterCode, PatientReference, EncounterPeriodStart, EncounterPeriodEnd, ConditionReference, EncounterDischargeSystem, EncounterDischargeCode),
            AntithromboticMedReqAuthoredOn: GetRandomDateInPeriod(start of EncounterInterval, end of EncounterInterval),
            AntithromboticMedReqStatus: MedicationRequestStatusCodes[RandomNumberInRange(0, 1)],
            AntithromboticMedReqMedicationConcept: BuildCodeableConcept(RxNormAntithromboticCodes.system, RxNormAntithromboticCodes.codes[RandomNumberInRange(0, Length(RxNormAntithromboticCodes.codes) - 1)]),
            AntithromboticMedReqDoNotPerform: if AntithromboticMedReqNotPerformed then true else false,
            AntithromboticMedReqReasonSystem: if AntithromboticMedReqNotPerformed then MedicationRequestReasonCodes.system else null,
            AntithromboticMedReqReasonCode: if AntithromboticMedReqNotPerformed then MedicationRequestReasonCodes.codes[RandomNumberInRange(0, 12)] else null,
            AntithromboticMedReq: BuildMedicationRequest(TestCaseId, AntithromboticMedReqStatus, 'order', true, false, AntithromboticMedReqDoNotPerform, AntithromboticMedReqMedicationConcept, PatientReference, AntithromboticMedReqAuthoredOn, null, AntithromboticMedReqReasonSystem, AntithromboticMedReqReasonCode)
            return
                Tuple {
                    ThePatient: AdultPatient,
                    TheCondition: IschemicCondition,
                    TheEncounter: NonElectiveInpatientEncounter,
                    TheMedicationRequest: AntithromboticMedReq
                }

define BuildIneligibleTestCases:
    OtherPopulationsCount C
        let TestCaseId: 'ineligible-' + ToString(C),
            PatientReference: 'Patient/' + TestCaseId,
            IsMale: C <= (Last(OtherPopulationsCount) / 2),
            Result: BuildRandomTestCase(TestCaseId, PatientReference, IsMale, false, false, false, false)
            return { Result.ThePatient }

define BuildInInitialPopulationTestCases:
    OtherPopulationsCount C
        let TestCaseId: 'in-initial-population-' + ToString(C),
            PatientReference: 'Patient/' + TestCaseId,
            IsMale: C <= (Last(OtherPopulationsCount) / 2),
            Result: BuildRandomTestCase(TestCaseId, PatientReference, IsMale, false, true, false, false)
            return { Result.ThePatient } union { Result.TheCondition } union { Result.TheEncounter }

define BuildInNumeratorTestCases:
    InNumeratorCount C
        let TestCaseId: 'in-numerator-' + ToString(C),
            PatientReference: 'Patient/' + TestCaseId,
            IsMale: C <= (Last(InNumeratorCount) / 2),
            Result: BuildRandomTestCase(TestCaseId, PatientReference, IsMale, false, true, true, false)
            return { Result.ThePatient } union { Result.TheCondition } union { Result.TheEncounter } union { Result.TheMedicationRequest }

define BuildDenominatorExclusionsTestCases:
    OtherPopulationsCount C
        let TestCaseId: 'denominator-exclusion-' + ToString(C),
            PatientReference: 'Patient/' + TestCaseId,
            IsMale: C <= (Last(OtherPopulationsCount) / 2),
            Result: BuildRandomTestCase(TestCaseId, PatientReference, IsMale, true, true, true, false)
            return { Result.ThePatient } union { Result.TheCondition } union { Result.TheEncounter } union { Result.TheMedicationRequest }

define BuildDenominatorExceptionsTestCases:
    OtherPopulationsCount C
        let TestCaseId: 'denominator-exception-' + ToString(C),
            PatientReference: 'Patient/' + TestCaseId,
            IsMale: C <= (Last(OtherPopulationsCount) / 2),
            Result: BuildRandomTestCase(TestCaseId, PatientReference, IsMale, false, true, true, true)
            return { Result.ThePatient } union { Result.TheCondition } union { Result.TheEncounter } union { Result.TheMedicationRequest }

define BuildTestCases:
    (flatten BuildIneligibleTestCases)
    union (flatten BuildInInitialPopulationTestCases)
    union (flatten BuildInNumeratorTestCases)
    union (flatten BuildDenominatorExclusionsTestCases)
    union (flatten BuildDenominatorExceptionsTestCases)

define TestDataGenerationResult:
    BuildBundle(BuildTestCases)

define function BuildBundle(resources List<Choice<FHIR.Resource>>):
    FHIR.Bundle {
        type: FHIR.BundleType { value: 'transaction'},
        entry: BuildBundleEntry(resources)
    }

define function BuildBundleEntry(resources List<FHIR.Resource>):
    resources resource
        let FHIRType:
            case
                when resource is FHIR.Patient then 'Patient/'
                when resource is FHIR.Encounter then 'Encounter/'
                when resource is FHIR.Condition then 'Condition/'
                when resource is FHIR.MedicationRequest then 'MedicationRequest/'
                when resource is FHIR.ServiceRequest then 'ServiceRequest/'
                else ''
            end
        return
            FHIR.Bundle.Entry {
                resource: resource,
                request: FHIR.Bundle.Entry.Request {
                    method: FHIR.HTTPVerb { value: 'PUT' },
                    url: FHIR.url { value: FHIRType + resource.id.value }
                }
            }

define function BuildMeta(profile String):
    FHIR.Meta {
        profile: List<FHIR.canonical> {
            FHIR.canonical { value: profile }
        }
    }

define function BuildCodeableConcept(system String, code String):
    FHIR.CodeableConcept {
        coding: List<FHIR.Coding> {
            BuildCoding(system, code)
        }
    }

define function BuildCoding(system String, code String):
    FHIR.Coding {
        system: FHIR.uri { value: system },
        code: FHIR.code { value: code }
    }

define function BuildCoding(system String, code String, display String):
    FHIR.Coding {
        system: FHIR.uri { value: system },
        code: FHIR.code { value: code },
        display: FHIR.string { value: display }
    }

define function BuildExtension(url String, value Choice<FHIR.base64Binary,FHIR.boolean,FHIR.canonical,FHIR.code,FHIR.date,FHIR.dateTime,FHIR.decimal,FHIR.id,FHIR.instant,FHIR.integer,FHIR.markdown,FHIR.oid,FHIR.positiveInt,FHIR.string,FHIR.time,FHIR.unsignedInt,FHIR.uri,FHIR.url,FHIR.uuid,FHIR.Address,FHIR.Age,FHIR.Annotation,FHIR.Attachment,FHIR.CodeableConcept,FHIR.Coding,FHIR.ContactPoint,FHIR.Count,FHIR.Distance,FHIR.Duration,FHIR.HumanName,FHIR.Identifier,FHIR.Money,FHIR.Period,FHIR.Quantity,FHIR.Range,FHIR.Ratio,FHIR.Reference,FHIR.SampledData,FHIR.Signature,FHIR.Timing,FHIR.ContactDetail,FHIR.Contributor,FHIR.DataRequirement,FHIR.Expression,FHIR.ParameterDefinition,FHIR.RelatedArtifact,FHIR.TriggerDefinition,FHIR.UsageContext,FHIR.Dosage,FHIR.Meta>):
    FHIR.Extension {
        url: FHIR.uri { value: url },
        value: value
    }

define function BuildPatientRaceAndEthnicityExtensions(raceTuple Tuple{raceCategoryCode String, raceCategoryDisplay String}, ethnicityTuple Tuple{ethnicityCategoryCode String, ethnicityCategoryDisplay String}):
    List<FHIR.Extension> {
        FHIR.Extension {
            url: FHIR.uri { value: 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-race' },
            extension: List<FHIR.Extension> {
                FHIR.Extension {
                    url: FHIR.uri { value: 'ombCategory' },
                    value: BuildCoding('urn:oid:2.16.840.1.113883.6.238', raceTuple.raceCategoryCode, raceTuple.raceCategoryDisplay)
                },
                FHIR.Extension {
                    url: FHIR.uri { value: 'text' },
                    value: FHIR.string { value: raceTuple.raceCategoryDisplay }
                }
            }
        },
        FHIR.Extension {
            url: FHIR.uri { value: 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity' },
            extension: List<FHIR.Extension> {
                FHIR.Extension {
                    url: FHIR.uri { value: 'ombCategory' },
                    value: BuildCoding('urn:oid:2.16.840.1.113883.6.238', ethnicityTuple.ethnicityCategoryCode, ethnicityTuple.ethnicityCategoryDisplay)
                },
                FHIR.Extension {
                    url: FHIR.uri { value: 'text' },
                    value: FHIR.string { value: ethnicityTuple.ethnicityCategoryDisplay }
                }
            }
        }
    }

define function BuildPatient(id String, firstName String, lastName String, gender String, birthDate Date):
    FHIR.Patient {
        id: FHIR.id { value: id },
        meta: BuildMeta('http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-patient'),
        extension: BuildPatientRaceAndEthnicityExtensions(RaceCategoryCodes[RandomNumberInRange(0, 4)], EthnicityCategoryCodes[RandomNumberInRange(0, 1)]),
        identifier: List<FHIR.Identifier> {
            FHIR.Identifier {
                system: FHIR.uri { value: 'urn:oid:1.2.36.146.595.217.0.1' },
                value: FHIR.string { value: '12345' }
            }
        },
        name: List<FHIR.HumanName> {
            FHIR.HumanName {
                use: FHIR.NameUse { value: 'official' },
                family: FHIR.string { value: lastName },
                given: List<FHIR.string> {
                    FHIR.string { value: firstName }
                }
            }
        },
        gender: FHIR.AdministrativeGender { value: gender },
        birthDate: FHIR.date { value: birthDate }
    }

define function BuildCondition(id String, codeSystem String, codeCode String, subject String):
    FHIR.Condition {
        id: FHIR.id { value: id },
        meta: BuildMeta('http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-condition'),
        verificationStatus: BuildCodeableConcept('http://terminology.hl7.org/CodeSystem/condition-ver-status', 'confirmed') ,
        category: List<FHIR.CodeableConcept> {
            BuildCodeableConcept('http://terminology.hl7.org/CodeSystem/condition-category', 'encounter-diagnosis')
        },
        code: BuildCodeableConcept(codeSystem, codeCode),
        subject: FHIR.Reference { reference: FHIR.string { value: subject } }
    }

define function BuildEncounter(id String, status String, type String, subject String, periodStart DateTime, periodEnd DateTime, condition String, dischargeSystem String, dischargeCode String):
    FHIR.Encounter {
        id: FHIR.id { value: id },
        meta: BuildMeta('http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-encounter'),
        status: FHIR.EncounterStatus { value: status },
        class: BuildCoding('http://terminology.hl7.org/CodeSystem/v3-ActCode', 'IMP'),
        type: List<FHIR.CodeableConcept> {
            BuildCodeableConcept('http://snomed.info/sct', type)
        },
        subject: FHIR.Reference { reference: FHIR.string { value: subject } },
        period: FHIR.Period { start: FHIR.dateTime { value: periodStart }, end: FHIR.dateTime { value: periodEnd } },
        diagnosis: List<FHIR.Encounter.Diagnosis> {
            FHIR.Encounter.Diagnosis {
                condition: FHIR.Reference { reference: FHIR.string { value: condition } },
                rank: FHIR.positiveInt { value: 1 }
            }
        },
        hospitalization: if dischargeSystem is not null
            then FHIR.Encounter.Hospitalization {
                dischargeDisposition: BuildCodeableConcept(dischargeSystem, dischargeCode)
            }
            else null
    }

define function BuildServiceRequest(id String, status String, intent String, subject String, authoredOn DateTime):
    FHIR.ServiceRequest {
        id: FHIR.id { value: id },
        meta: BuildMeta('http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-servicerequest'),
        status: FHIR.ServiceRequestStatus { value: status },
        intent: FHIR.ServiceRequestIntent { value: intent },
        subject: FHIR.Reference { reference: FHIR.string { value: subject } },
        authoredOn: FHIR.dateTime { value: authoredOn }
    }

define function BuildProcedure(id String, recorded DateTime, status String, codeSystem String, codeCode String, subject String, performed Choice<DateTime, Interval<DateTime>>):
    FHIR.Procedure {
        id: FHIR.id { value: id },
        meta: BuildMeta('http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-procedure'),
        extension: List<FHIR.Extension> {
            BuildExtension('http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-recorded', FHIR.dateTime{ value: recorded })
        },
        status: FHIR.ProcedureStatus { value: status },
        code: BuildCodeableConcept(codeSystem, codeCode),
        subject: FHIR.Reference { reference: FHIR.string { value: subject } },
        performed:
            if performed is DateTime then FHIR.dateTime { value: performed }
            else FHIR.Period { start: FHIR.dateTime { value: start of performed }, end: FHIR.dateTime { value: end of performed } }
    }

define function BuildMedicationRequest(id String, status String, intent String, isCommunity Boolean, isDischarge Boolean, doNotPerform Boolean, medication Choice<String, FHIR.CodeableConcept>, subject String, authoredOn DateTime, requester String, reasonCodeSystem String, reasonCodeCode String):
    FHIR.MedicationRequest {
        id: FHIR.id { value: id },
        meta: BuildMeta('http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest'),
        status: FHIR.MedicationRequestStatus { value: status },
        intent: FHIR.MedicationRequestIntent { value: intent },
        category: List<FHIR.CodeableConcept> {
            BuildCodeableConcept('http://terminology.hl7.org/CodeSystem/medicationrequest-category',
                if isCommunity then 'community' else if isDischarge then 'discharge' else 'inpatient')
        },
        doNotPerform: FHIR.boolean { value: doNotPerform },
        medication: if medication is String then FHIR.Reference { reference: FHIR.string { value: medication } } else medication,
        subject: FHIR.Reference { reference: FHIR.string { value: subject } },
        authoredOn: FHIR.dateTime { value: authoredOn },
        requester:
            if requester is not null then FHIR.Reference { reference: FHIR.string { value: requester } } else null,
        reasonCode: if reasonCodeSystem is not null and reasonCodeCode is not null
            then List<FHIR.CodeableConcept> { BuildCodeableConcept(reasonCodeSystem, reasonCodeCode) }
            else null
    }